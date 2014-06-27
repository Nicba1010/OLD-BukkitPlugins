package me.nicba1010.pluginmanager;

import static me.nicba1010.pluginmanager.Commons.pluginFolder;
import static me.nicba1010.pluginmanager.Commons.updateFolder;
import static me.nicba1010.pluginmanager.Regex.regexAll;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PluginUtils {

	public static ArrayList<String[]> search(String search, CommandSender sender, Integer... a) {
		ArrayList<String[]> results = new ArrayList<String[]>();
		Integer page = a.length > 0 ? a[0] : 1;
		System.out.println(page);
		for (String string : regexAll("<td class=\"col-search-entry\"><h2><a href=\"/bukkit-plugins/(.*)/\">(.*)</a></h2></td>", "http://dev.bukkit.org/search/?page=" + page
			+ "&scope=projects&search=" + search, true)[1]) {
			string = string.replaceAll("<(?:/)?mark>", "").replaceAll("  ", "");
			String[] arr = string.split("-:-");
			results.add(arr);
		}
		Main.getPlayerData().resultMap.put(sender, results);
		return results;
	}

	public static boolean pluginExistsPluginFolder(String fileName) {
		File f = new File(pluginFolder + fileName);
		return f.exists() && !f.isDirectory();
	}

	public static boolean pluginExistsUpdateFolder(String fileName) {
		File f = new File(updateFolder + fileName);
		return f.exists() && !f.isDirectory();
	}

	public static void setPercentage(int percentage, Player p) {
		Inventory inv = p.getOpenInventory().getTopInventory();
		float percent = (float) percentage / 100f;
		int numOfSlots = (int) (percent * (float) (inv.getSize() - 2));
		if (numOfSlots > 51)
			numOfSlots = 51;
		for (int i = 0; i < numOfSlots; i++) {
			ItemStack perc = new ItemStack(Material.WOOL, 1, (byte) 5);
			ItemMeta percMeta = perc.getItemMeta();
			percMeta.setDisplayName(ChatColor.GREEN + Integer.toString(i * 2) + "%");
			perc.setItemMeta(percMeta);
			inv.setItem(i, perc);
		}
	}
}
