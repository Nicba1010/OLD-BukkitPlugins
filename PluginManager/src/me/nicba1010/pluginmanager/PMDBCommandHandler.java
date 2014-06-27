package me.nicba1010.pluginmanager;

import static me.nicba1010.pluginmanager.Commons.serverMessage;
import static me.nicba1010.pluginmanager.PluginUtils.search;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class PMDBCommandHandler implements CommandExecutor, Listener {

	String[]			flags	= new String[] { "-g" };
	static boolean[]	flagsOn	= new boolean[] { false };

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, String label, final String[] oldargs) {
		if (oldargs.length > 0) {
			new Thread(new BukkitRunnable() {
				@Override
				public void run() {
					boolean[] flagArray = getFlags(oldargs);
					String[] args = stripFlags(oldargs);
					flagsOn = new boolean[] { false };
					String[] args1 = new String[3];
					if (args.length == 1) {
						args1[0] = args[0];
						args1[1] = "0";
					} else if (args.length == 2) {
						args1[0] = args[1];
						args1[1] = args[0];
						args1[2] = "0";
					} else if (args.length == 3) {
						args1[0] = args[1];
						args1[1] = args[0];
						args1[2] = args[2];
					}
					System.out.println("EXEC");
					pmdb(cmd, args1, sender, flagArray);
				}
			}).start();
			return true;
		} else
			return false;
	}

	public void pmdb(final Command cmd, final String[] args, final CommandSender sender, final boolean[] flags) {
		final int operation;
		boolean gui = false;
		if (flags[0]) {
			gui = true;
		}
		if (args.length > 2) {
			if (args[1].equalsIgnoreCase("both") || args[1].equalsIgnoreCase("0")) {
				operation = 0;
			} else if (args[1].equalsIgnoreCase("install") || args[1].equalsIgnoreCase("1")) {
				operation = 1;
			} else if (args[1].equalsIgnoreCase("update") || args[1].equalsIgnoreCase("2")) {
				operation = 2;
			} else if (args[1].equalsIgnoreCase("search") || args[1].equalsIgnoreCase("3")) {
				operation = 3;
			} else {
				operation = 4;
			}
		} else {
			operation = 0;
		}
		if (operation != 4) {
			if (operation >= 0 && operation < 3) {
				if (!flags[0] || !(sender instanceof Player)) {
					final PluginInfo p = new PluginInfo(args[0]);
					if (!p.exists) {
						serverMessage("Sorry, the plugin(or it's download) wasn't found. Please double check the plugin ID(e.g. http://dev.bukkit.org/bukkit-plugins/&lsignshop&r/, the id is the bold text(between the &l and &r tags if you're in the console). If the problems persist please contact the developer!)");
					} else {
						serverMessage("Do you want to install " + p.fileName + " for " + p.gameVersion + " (" + p.pluginSize + ")?");
						Main.getPlayerData().timerMap.put(sender, new Timer(sender));
						Timer t = Main.getPlayerData().timerMap.get(sender);
						t.assignTask(new Runnable() {
							@Override
							public void run() {
								p.download(operation, sender, false);
							}
						});
						t.start("Answer\n/pm yes      to install\n/pm no   not to install");
					}
				} else {
					Main.getGUIUtils().openGUI((Player) sender, 1, args[0]);
				}
			} else if (operation == 3) {
				ArrayList<String[]> results;
				Main.getPlayerData().pageMap.put(sender, Integer.parseInt(args[2]));
				Main.getPlayerData().lastSearch.put(sender, args[0]);
				if (!gui || !(sender instanceof Player)) {
					if (args.length > 2) {
						results = search(args[0], sender, Integer.parseInt(args[2]));
					} else {
						results = search(args[0], sender, 1);
					}
					if (args.length > 2)
						for (String[] strings : results) {
							bukkitSearchResults(strings, sender);
						}
					else {
						for (String[] strings : results) {
							bukkitSearchResults(strings, sender);
						}
					}
					Main.getPlayerData().timerMap.put(sender, new Timer(sender));
					final Timer t = Main.getPlayerData().timerMap.get(sender);
					t.specialTime(40000);
					t.assignTask(new Runnable() {

						@Override
						public void run() {
							String[] args1 = new String[2];
							args1[0] = Main.getPlayerData().resultMap.get(sender).get(t.optional - 1)[0];
							args1[1] = "0";
							pmdb(cmd, args1, sender, flags);
						}
					});
					t.start("Answer /pm (number of plugin) to install, or /pm 0 to cancel");
				} else {
					if (args.length > 2) {
						search(args[0], sender, Integer.parseInt(args[2]));
					} else {
						search(args[0], sender, 1);
					}
					Main.getGUIUtils().openGUI((Player) sender, 0);
				}
			}
		}
	}

	private void bukkitSearchResults(String[] strings, CommandSender sender) {
		serverMessage((Main.getPlayerData().resultMap.get(sender).indexOf(strings) + 1) + ". " + strings[1], sender);
	}

	private String[] stripFlags(String[] array) {
		int countedFlags = 0;
		String[] stripped = null;
		for (int i = 0; i < flags.length; i++) {
			for (int j = 0; j < array.length; j++) {
				if (array[j].equalsIgnoreCase(flags[i]))
					countedFlags++;
			}
		}
		stripped = new String[array.length - countedFlags];
		int place = 0;
		for (int i = 0; i < array.length; i++) {
			boolean skip = false;
			int j = 0;
			for (String flag : flags) {
				if (array[i].equalsIgnoreCase(flag)) {
					Bukkit.getLogger().info(flag);
					skip = true;
					flagsOn[j] = true;
					Bukkit.getLogger().info(Boolean.toString(flagsOn[j]) + j);
				}
				j++;
			}
			if (!skip) {
				stripped[place] = array[i];
				place++;
			}
		}
		return stripped;
	}

	private boolean[] getFlags(String[] array) {
		for (int i = 0; i < array.length; i++) {
			int j = 0;
			for (String flag : flags) {
				if (array[i].equalsIgnoreCase(flag)) {
					flagsOn[j] = true;
				}
				j++;
			}
		}
		return flagsOn.clone();
	}
}
