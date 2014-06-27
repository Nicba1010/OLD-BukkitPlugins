package me.nicba1010.pluginmanager;

import static me.nicba1010.pluginmanager.Commons.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public void onEnable() {
		getLogger().info("Enabled!");
		instance = this;
		playerData = new PlayerData();
		guiUtils = new GUIUtils();
		pluginFolder = getClass().getProtectionDomain().getCodeSource().getLocation().toString().replace("file:/", "").replace("PluginManager.jar", "");
		updateFolder = pluginFolder + "update\\";
		PMDBCommandHandler cmdHandler = new PMDBCommandHandler();
		getCommand("pmdb").setExecutor(cmdHandler);
		getServer().getPluginManager().registerEvents(guiUtils, this);
	}

	public void onDisable() {
		getLogger().info("Disabled!");
	}

	public static boolean isNumeric(String str) {
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("pmlist")) {
			for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.sendMessage(plugin.getName() + "   " + plugin.getDescription().getFullName() + "   " + plugin.getDescription().getVersion());
				} else {
					getLogger().info(plugin.getName() + "   " + plugin.getDescription().getFullName() + "   " + plugin.getDescription().getVersion());
				}
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("pm") && args.length == 1) {
			if (getPlayerData().timerMap.get(sender) != null)
				if (getPlayerData().timerMap.get(sender).running)
					if (args[0].equalsIgnoreCase("yes")) {
						getPlayerData().timerMap.get(sender).doTask();
					} else if (args[0].equalsIgnoreCase("no")) {
						getPlayerData().timerMap.get(sender).stop();
					} else if (isNumeric(args[0])) {
						if (Integer.parseInt(args[0]) - 1 < 0 && Integer.parseInt(args[0]) <= getPlayerData().resultMap.get(sender).size())
							return false;
						if (sender instanceof Player)
							((Player) sender).performCommand("pmdb install " + getPlayerData().resultMap.get(sender).get(Integer.parseInt(args[0]) - 1)[0] + " 0");
						else
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pmdb install " + getPlayerData().resultMap.get(sender).get(Integer.parseInt(args[0]) - 1)[0] + " 0");
						getPlayerData().timerMap.get(sender).stop();
					} else {
						getPlayerData().timerMap.get(sender).reset();
					}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("pminfo") && args.length == 1) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.sendMessage(plugin.getName() + "   " + plugin.getDescription().getFullName() + "   " + plugin.getDescription().getVersion());
			} else {
				getLogger().info(plugin.getName() + "   " + plugin.getDescription().getFullName() + "   " + plugin.getDescription().getVersion());
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("test") && args.length == 1) {
			guiUtils.openGUI((Player) sender, Integer.parseInt(args[0]));
			return true;
		}
		return false;
	}

	public static Plugin getInstance() {
		return instance;
	}

	public static PlayerData getPlayerData() {
		return playerData;
	}

	public static GUIUtils getGUIUtils() {
		return guiUtils;
	}
}
