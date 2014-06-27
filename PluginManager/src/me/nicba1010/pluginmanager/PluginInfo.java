package me.nicba1010.pluginmanager;

import static me.nicba1010.pluginmanager.Commons.pluginFolder;
import static me.nicba1010.pluginmanager.Commons.serverMessage;
import static me.nicba1010.pluginmanager.Commons.unZipIt;
import static me.nicba1010.pluginmanager.Commons.updateFolder;
import static me.nicba1010.pluginmanager.PluginUtils.pluginExistsPluginFolder;
import static me.nicba1010.pluginmanager.PluginUtils.pluginExistsUpdateFolder;
import static me.nicba1010.pluginmanager.PluginUtils.setPercentage;
import static me.nicba1010.pluginmanager.Regex.regexFirst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginInfo {
	String	devHTML, downloadLatestHTML;
	String	jarLink, devLink;
	boolean	exists	= false;
	String	newestDlLink;
	String	fileName;
	String	pluginSize;
	String	gameVersion;
	String	slug;
	boolean	zip;

	public PluginInfo(String slug) {
		this.slug = slug;
		devLink = "http://dev.bukkit.org/bukkit-plugins/" + slug + "/";
		String[] reg1 = regexFirst("<a href=\"/bukkit-plugins(.*)\">Download</a>", devLink, true);
		devHTML = reg1[0];
		newestDlLink = "http://dev.bukkit.org/bukkit-plugins" + reg1[1];
		Bukkit.getLogger().info(newestDlLink);
		exists = reg1 != null;
		if (exists) {
			String[] reg2 = regexFirst("<a href=\"http://dev.bukkit.org/media/files(.*)(jar|zip)\">", newestDlLink, true);
			if (reg2[0] != null && reg2[1] != null) {
				downloadLatestHTML = reg2[0];
				if (downloadLatestHTML.contains("http://dev.bukkit.org/media/files" + reg2[1] + "jar")) {
					jarLink = "http://dev.bukkit.org/media/files" + reg2[1] + "jar";
					fileName = regexFirst("/files/.*/(.*).jar", newestDlLink, true)[1];
				} else if (downloadLatestHTML.contains("http://dev.bukkit.org/media/files" + reg2[1] + "zip")) {
					zip = true;
					jarLink = "http://dev.bukkit.org/media/files" + reg2[1] + "zip";
					fileName = regexFirst("/files/.*/(.*).zip", newestDlLink, true)[1];
				}
				pluginSize = regexFirst("<dt>Size</dt>\\s*<dd>(.*)</dd>", downloadLatestHTML, false)[1];
				gameVersion = regexFirst("<dd><ul class=\"comma-separated-list\"><li>(.*)</li></ul></dd>", downloadLatestHTML, false)[1];
			} else
				exists = false;
		}
	}

	public void downloadFile(String to, String from, boolean gui, String fileName, CommandSender sender) throws IOException {
		URL url = new URL(from);
		url.openConnection();
		InputStream reader = url.openStream();
		FileOutputStream writer = new FileOutputStream(to);
		int totalSizeInB = (int) (Float.parseFloat(pluginSize.replaceAll(" KiB", "")) * 1024f);
		int bufferSize = totalSizeInB / 50;
		byte[] buffer = new byte[bufferSize];
		int totalBytesRead = 0;
		int bytesRead = 0;
		if (!gui)
			while ((bytesRead = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[bufferSize];
				totalBytesRead += bytesRead;
			}
		else {
			Player player = (Player) sender;
			Main.getGUIUtils().openGUI(player, 2);
			while ((bytesRead = reader.read(buffer)) > 0 && Main.getPlayerData().canDL.get(player.getName())) {
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[1024];
				totalBytesRead += bytesRead;
				int percentage = (int) (((float) totalBytesRead / (float) totalSizeInB) * 100f);
				setPercentage(percentage, player);
			}
		}
		writer.close();
		reader.close();
		if (gui)
			if (!Main.getPlayerData().canDL.get(((Player) sender).getName())) {
				(new File(to)).delete();
				return;
			}
		if (fileName.contains(".zip") || fileName.contains(".ZIP")) {
			unZipIt(to, pluginFolder.replace(fileName, ""));
		}
	}

	public void download(final int operation, final CommandSender sender, final boolean gui) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			Main.getPlayerData().canDL.put(player.getName(), true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean install = sender.hasPermission("pluginmanager.pmdb.install");
					boolean update = sender.hasPermission("pluginmanager.pmdb.update");
					String pluginFile;
					if (zip)
						pluginFile = fileName + ".zip";
					else
						pluginFile = fileName + ".jar";
					System.out.println(!pluginExistsUpdateFolder(pluginFile) && update && (operation == 0 || operation == 2));
					System.out.println(operation);
					if (!pluginExistsPluginFolder(pluginFile) && install && (operation == 0 || operation == 1)) {
						try {
							downloadFile(pluginFolder + pluginFile, jarLink, gui, pluginFile, sender);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						if (!pluginExistsUpdateFolder(pluginFile) && update && (operation == 0 || operation == 2)) {
							try {
								downloadFile(updateFolder + pluginFile, jarLink, gui, pluginFile, sender);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if (!install && !update) {
							sender.sendMessage("Insufficient permissions (pluginmanager.pmdb.install && pluginmanager.pmdb.update).");
						} else if (!install && operation == 1) {
							if (sender instanceof Player)
								sender.sendMessage("Insufficient permissions (pluginmanager.pmdb.install).");
						} else if (operation == 2 && !update) {
							if (sender instanceof Player)
								sender.sendMessage("Insufficient permissions (pluginmanager.pmdb.update).");
						} else {
							serverMessage("File exists in both update and plugin folder! The server will reload now to update the appropriate plugin and make space for the new one in the update folder!");
						}
					}
					System.out.println(Main.getPlayerData().canDL.get(player.getName()));
					if (Main.getPlayerData().canDL.get(player.getName())) {
						serverMessage("RELOAD IN:");
						for (int i = 5; i > 0; i--) {
							try {
								serverMessage(Integer.toString(i));
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								serverMessage(e.toString(), true);
							}
						}
						Main.getPlayerData().canDL.put(player.getName(), false);
						Bukkit.getServer().reload();
					}
					Main.getPlayerData().canDL.put(player.getName(), false);
				}
			}).start();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean install = sender.hasPermission("pluginmanager.pmdb.install");
					boolean update = sender.hasPermission("pluginmanager.pmdb.update");
					String pluginFile;
					if (zip)
						pluginFile = fileName + ".zip";
					else
						pluginFile = fileName + ".jar";
					if (!pluginExistsPluginFolder(pluginFile) && install && (operation == 0 || operation == 1)) {
						try {
							downloadFile(pluginFolder + pluginFile, jarLink, gui, pluginFile, sender);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						if (!pluginExistsUpdateFolder(pluginFile) && update && (operation == 0 || operation == 2)) {
							try {
								downloadFile(updateFolder + pluginFile, jarLink, gui, pluginFile, sender);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if (!install && !update) {
							sender.sendMessage("Insufficient permissions (pluginmanager.pmdb.install && pluginmanager.pmdb.update).");
						} else if (!install && operation == 1) {
							if (sender instanceof Player)
								sender.sendMessage("Insufficient permissions (pluginmanager.pmdb.install).");
						} else if (operation == 2 && !update) {
							if (sender instanceof Player)
								sender.sendMessage("Insufficient permissions (pluginmanager.pmdb.update).");
						} else {
							serverMessage("File exists in both update and plugin folder! The server will reload now to update the appropriate plugin and make space for the new one in the update folder!");
						}
					}
					serverMessage("RELOAD IN:");
					for (int i = 5; i > 0; i--) {
						try {
							serverMessage(Integer.toString(i));
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							serverMessage(e.toString(), true);
						}
					}
					Bukkit.getServer().reload();
				}
			}).start();
		}
	}
}
