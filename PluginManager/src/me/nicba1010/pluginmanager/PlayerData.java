package me.nicba1010.pluginmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerData {
	public HashMap<CommandSender, ArrayList<String[]>>	resultMap		= new HashMap<CommandSender, ArrayList<String[]>>();
	public HashMap<CommandSender, Timer>				timerMap		= new HashMap<CommandSender, Timer>();
	public HashMap<CommandSender, Integer>				pageMap			= new HashMap<CommandSender, Integer>();
	public HashMap<CommandSender, String>				lastSearch		= new HashMap<CommandSender, String>();
	public HashMap<String, ArrayList<ItemStack>>		pluginItemMap	= new HashMap<String, ArrayList<ItemStack>>();
	public HashMap<String, ArrayList<ItemMeta>>			pluginMetaMap	= new HashMap<String, ArrayList<ItemMeta>>();
	public HashMap<String, Boolean>						canDL			= new HashMap<String, Boolean>();
	public HashMap<String, Boolean>						loading			= new HashMap<String, Boolean>();
}
