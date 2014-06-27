package me.nicba1010.pluginmanager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GUI extends JavaPlugin implements Listener {

	ArrayList<ItemStack>	plugins		= new ArrayList<ItemStack>();
	ArrayList<ItemMeta>		pluginsMeta	= new ArrayList<ItemMeta>();

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void test(Player p) {
		p.sendMessage("LALAL");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("pmlist") && sender instanceof Player) {
			openGUI((Player) sender);
		}
		return true;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase("Download")) {
			return;
		}
		event.setCancelled(true);
		return;
	}

	private void openGUI(final Player p) {
		Inventory inv = Bukkit.createInventory(null, 54, "Download");
		inv.setMaxStackSize(100);
		ItemStack cancel = new ItemStack(Material.WOOL, 1, (byte) 14);
		ItemMeta cancelMeta = cancel.getItemMeta();
		cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
		cancel.setItemMeta(cancelMeta);
		inv.setItem(53, cancel);
		p.openInventory(inv);
		new Thread(new BukkitRunnable() {

			@Override
			public void run() {
				int i = 0;
				while (i < 100) {
					setPercentage(i, p);
					i++;
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void setPercentage(int percentage, Player p) {
		Inventory inv = p.getOpenInventory().getTopInventory();
		float percent = (float) percentage / 100f;
		int numOfSlots = (int) (percent * (float) (inv.getSize() - 2));
		for (int i = 0; i < numOfSlots; i++) {
			ItemStack perc = new ItemStack(Material.WOOL, 1, (byte) 5);
			ItemMeta percMeta = perc.getItemMeta();
			percMeta.setDisplayName(ChatColor.GREEN + Integer.toString(i * 2) + "%");
			perc.setItemMeta(percMeta);
			inv.setItem(i, perc);
		}
	}

}
