package me.nicba1010.pluginmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commons {
	public static String	updateFolder;
	public static String	pluginFolder;
	public static boolean	advancedLogging	= true;
	public static String	newestDlLink;
	public static Plugin	instance;
	public static PlayerData playerData;
	public static GUIUtils guiUtils;

	public static void serverMessage(String msg, CommandSender sender) {
		if (sender instanceof Player) {
			((Player) sender).sendMessage(msg);
		} else {
			serverMessage(msg);
		}
	}

	public static void serverMessage(String msg) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say " + msg);
	}

	public static void serverMessage(String msg, boolean b) {
		if (advancedLogging)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say " + msg);
	}

	public static void unZipIt(String zipFile, String outputFolder) {
		byte[] buffer = new byte[1024];
		try {
			File outFolder = new File(outputFolder);
			if (!outFolder.exists()) {
				outFolder.mkdir();
			}
			File zip = new File(zipFile);
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				String fileName = zipEntry.getName();
				File newFile = new File(outputFolder + File.separator + fileName);
				System.out.println("File unzip : " + newFile.getAbsoluteFile());
				new File(newFile.getParent().replace(zip.getName(), "")).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
//			zip.delete();
			System.out.println("Done");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
