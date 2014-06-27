package me.nicba1010.pluginmanager;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuItem {
	public MenuItem(String name, Material material, int qty, int place, Inventory inv, byte... metadata) {
		ItemStack item;
		if (metadata.length > 0)
			item = new ItemStack(material, qty, metadata[0]);
		else
			item = new ItemStack(material, qty);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		inv.setItem(place, item);
	}
}
