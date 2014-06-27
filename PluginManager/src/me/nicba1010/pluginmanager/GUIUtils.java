package me.nicba1010.pluginmanager;

import static me.nicba1010.pluginmanager.Commons.serverMessage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIUtils implements Listener {
	public byte openInventory(InventoryClickEvent event) {
		if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase("Plugins"))
			return 0;
		else if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase("Install?"))
			return 1;
		else if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase("Download"))
			return 2;
		else if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase("OK"))
			return 3;
		else if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase("WAIT"))
			return 4;
		else
			return -1;

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final String playerName = player.getName();
		switch (openInventory(event)) {
			case 0: {
				Main.getPlayerData().loading.put(playerName, false);
				ArrayList<ItemStack> pluginItemMap = Main.getPlayerData().pluginItemMap.get(playerName);
				ArrayList<String[]> resultMap = Main.getPlayerData().resultMap.get((CommandSender) player);
				Integer page = Main.getPlayerData().pageMap.get((CommandSender) player);
				String search = Main.getPlayerData().lastSearch.get((CommandSender) player);
				event.setCancelled(true);
				System.out.println(page);
				if (event.getCurrentItem().hasItemMeta()) {
					ItemMeta currentItemMeta = event.getCurrentItem().getItemMeta();
					if (currentItemMeta.hasLore()) {
						for (int i = 0; i < pluginItemMap.size(); i++) {
							if (currentItemMeta.getLore().get(1).equalsIgnoreCase("Slug: " + resultMap.get(i)[0])) {
								player.sendMessage(currentItemMeta.getLore().get(1).replaceAll("Slug: ", ""));
								player.closeInventory();
								openGUI(player, 1, currentItemMeta.getLore().get(1).replaceAll("Slug: ", ""));
								return;
							}
						}
					} else if (currentItemMeta.getDisplayName().equalsIgnoreCase("Cancel")) {
						player.sendMessage("Cancelled");
						player.closeInventory();
						return;
					} else if (currentItemMeta.getDisplayName().equalsIgnoreCase("Previous") && (page != 1)) {
						player.sendMessage("Previous page");
						int newpage = (page - 1);
						player.performCommand("pmdb search " + search + " " + newpage + " -g");
						Main.getPlayerData().loading.put(playerName, true);
						openGUI(player, 5);
						return;
					} else if (currentItemMeta.getDisplayName().equalsIgnoreCase("Next") && resultMap.size() == 20) {
						player.sendMessage("Next page");
						int newpage = (page + 1);
						player.performCommand("pmdb search " + search + " " + newpage + " -g");
						Main.getPlayerData().loading.put(playerName, true);
						openGUI(player, 5);
						return;
					} else {
						return;
					}
				}
				break;
			}
			case 1: {
				final CommandSender sender = player;
				event.setCancelled(true);
				if (event.getCurrentItem().hasItemMeta()) {
					ItemMeta currentItemMeta = event.getCurrentItem().getItemMeta();
					if (ChatColor.stripColor(currentItemMeta.getDisplayName()).equalsIgnoreCase("YES")) {
						player.sendMessage("Installing");
						String plslug = ChatColor.stripColor(player.getOpenInventory().getTopInventory().getItem(4).getItemMeta().getDisplayName());
						player.closeInventory();
						openGUI(player, 3);
						final PluginInfo p = new PluginInfo(plslug);
						if (!p.exists) {
							serverMessage("Sorry, the plugin(or it's download) wasn't found. Please double check the plugin ID(e.g. http://dev.bukkit.org/bukkit-plugins/&lsignshop&r/, the id is the bold text(between the &l and &r tags if you're in the console). If the problems persist please contact the developer!)");
						} else {
							new Thread(new BukkitRunnable() {

								@Override
								public void run() {
									player.closeInventory();
									p.download(0, sender, true);
									player.closeInventory();
								}
							}).start();
						}
						return;
					} else if (ChatColor.stripColor(currentItemMeta.getDisplayName()).equalsIgnoreCase("NO")) {
						player.sendMessage("Cancelled");
					}
				}
				player.closeInventory();
				break;
			}
			case 2: {
				if (event.getCurrentItem().hasItemMeta()) {
					ItemMeta currentItemMeta = event.getCurrentItem().getItemMeta();
					System.out.println(currentItemMeta.getDisplayName());
					if (ChatColor.stripColor(currentItemMeta.getDisplayName()).equalsIgnoreCase("Cancel")) {
						Main.getPlayerData().canDL.put(playerName, false);
						player.closeInventory();
					}
				}
				event.setCancelled(true);
				return;
			}
			case 3: {
				event.setCancelled(true);
				return;
			}
			case 4: {
				event.setCancelled(true);
				return;
			}
			default:
				return;
		}
		return;
	}

	public void openGUI(final Player player, int type, String... arg) {
		final String playerName = player.getName();
		switch (type) {
			case 0: {
				Inventory inv = Bukkit.createInventory(null, 27, "Plugins");
				ArrayList<ItemStack> pluginItemMap = Main.getPlayerData().pluginItemMap.get(playerName);
				ArrayList<ItemMeta> pluginMetaMap = Main.getPlayerData().pluginMetaMap.get(playerName);
				ArrayList<String[]> resultMap = Main.getPlayerData().resultMap.get((CommandSender) player);
				if (pluginItemMap != null)
					pluginItemMap.clear();
				else
					pluginItemMap = new ArrayList<ItemStack>();
				if (pluginMetaMap != null)
					pluginMetaMap.clear();
				else
					pluginMetaMap = new ArrayList<ItemMeta>();
				for (int i = 0; i < resultMap.size(); i++) {
					pluginItemMap.add(new ItemStack(Material.WOOL, 1, (byte) 5));
					pluginMetaMap.add(pluginItemMap.get(i).getItemMeta());
					pluginMetaMap.get(i).setDisplayName(resultMap.get(i)[1]);
					List<String> lore = new ArrayList<String>();
					lore.add("Info: " + resultMap.get(i)[1]);
					lore.add("Slug: " + resultMap.get(i)[0]);
					pluginMetaMap.get(i).setLore(lore);
					pluginItemMap.get(i).setItemMeta(pluginMetaMap.get(i));
				}
				ItemStack cancel = new ItemStack(Material.WOOL, 1, (byte) 14);
				ItemMeta cancelMeta = cancel.getItemMeta();
				cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
				cancel.setItemMeta(cancelMeta);
				for (int i = 0; i < pluginItemMap.size(); i++) {
					inv.setItem(i, pluginItemMap.get(i));
				}
				inv.setItem(26, cancel);
				new MenuItem("Page: " + Integer.toString(Main.getPlayerData().pageMap.get((CommandSender) player)), Material.WOOL, 1, 22, inv, new byte[] { 0 });
				new MenuItem("Previous", Material.WOOL, 1, 23, inv, new byte[] { 10 });
				new MenuItem("Next", Material.WOOL, 1, 24, inv, new byte[] { 11 });
				player.openInventory(inv);
				break;
			}
			case 1: {
				Inventory inv = Bukkit.createInventory(null, 54, "Install?");
				ItemStack cancel = new ItemStack(Material.WOOL, 1, (byte) 14);
				ItemMeta cancelMeta = cancel.getItemMeta();
				cancelMeta.setDisplayName(ChatColor.RED + "NO");
				cancel.setItemMeta(cancelMeta);
				ItemStack yes = new ItemStack(Material.WOOL, 1, (byte) 5);
				ItemMeta yesMeta = yes.getItemMeta();
				yesMeta.setDisplayName(ChatColor.GREEN + "YES");
				yes.setItemMeta(yesMeta);
				ItemStack none = new ItemStack(Material.GLASS);
				ItemMeta noneMeta = none.getItemMeta();
				noneMeta.setDisplayName(arg[0]);
				List<String> lore = new ArrayList<String>();
				lore.add("Plugin");
				noneMeta.setLore(lore);
				none.setItemMeta(noneMeta);
				for (int j = 0; j < 6; j++) {
					for (int i = 0; i < 4; i++) {
						inv.setItem(i + j * 9, yes);
					}
				}
				for (int j = 0; j < 6; j++) {
					for (int i = 5; i < 9; i++) {
						inv.setItem(i + j * 9, cancel);
					}
				}
				for (int j = 0; j < 6; j++) {
					inv.setItem(j * 9 + 4, none);
				}
				player.openInventory(inv);
				break;
			}
			case 2: {
				Inventory inv = Bukkit.createInventory(null, 54, "Download");
				inv.setMaxStackSize(100);
				ItemStack cancel = new ItemStack(Material.WOOL, 1, (byte) 14);
				ItemMeta cancelMeta = cancel.getItemMeta();
				cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
				cancel.setItemMeta(cancelMeta);
				inv.setItem(53, cancel);
				player.openInventory(inv);
				break;
			}
			case 3: {
				Inventory inv = Bukkit.createInventory(null, 54, "OK");
				ItemStack ok = new ItemStack(Material.WOOL, 1, (byte) 5);
				ItemMeta okMeta = ok.getItemMeta();
				okMeta.setDisplayName(" ");
				inv.setItem(1, ok);
				inv.setItem(2, ok);
				inv.setItem(5, ok);
				inv.setItem(8, ok);
				inv.setItem(9, ok);
				inv.setItem(12, ok);
				inv.setItem(14, ok);
				inv.setItem(16, ok);
				inv.setItem(18, ok);
				inv.setItem(21, ok);
				inv.setItem(23, ok);
				inv.setItem(24, ok);
				inv.setItem(27, ok);
				inv.setItem(30, ok);
				inv.setItem(32, ok);
				inv.setItem(33, ok);
				inv.setItem(36, ok);
				inv.setItem(39, ok);
				inv.setItem(41, ok);
				inv.setItem(43, ok);
				inv.setItem(46, ok);
				inv.setItem(47, ok);
				inv.setItem(50, ok);
				inv.setItem(53, ok);
				player.openInventory(inv);
				break;
			}
			case 4: {
				AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler() {
					@Override
					public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
						if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
							event.setWillClose(true);
							event.setWillDestroy(true);
							player.performCommand("pmdb search " + event.getName() + " 0 -g");
						} else {
							event.setWillClose(false);
							event.setWillDestroy(false);
						}
					}
				});
				ItemStack search = new ItemStack(Material.NAME_TAG);
				ItemMeta searchMeta = search.getItemMeta();
				searchMeta.setDisplayName("Search");
				search.setItemMeta(searchMeta);
				gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, search);
				gui.open();
				break;
			}
			case 5: {
				final int[] plc = new int[] { 3, 4, 5, 15, 24, 33, 41, 40, 39, 29, 20, 11 };
				final Inventory inv = Bukkit.createInventory(null, 45, "WAIT");
				final ItemStack WOOL = new ItemStack(Material.WOOL, 1, (byte) 5);
				ItemMeta okMeta = WOOL.getItemMeta();
				okMeta.setDisplayName("Loading");
				player.openInventory(inv);
				new Thread(new BukkitRunnable() {
					@Override
					public void run() {
						int i = 0;
						int i1 = 1;
						int i2 = 2;
						Main.getPlayerData().loading.put(playerName, true);
						while (Main.getPlayerData().loading.get(playerName)) {
							inv.clear();
							inv.setItem(plc[i], WOOL);
							inv.setItem(plc[i1], WOOL);
							inv.setItem(plc[i2], WOOL);
							if (i < 11)
								i++;
							else
								i = 0;
							if (i1 < 11)
								i1++;
							else
								i1 = 0;
							if (i2 < 11)
								i2++;
							else
								i2 = 0;
							try {
								Thread.sleep(250);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						player.closeInventory();
					}
				}).start();
			}
			case 6: {
				Main.getPlayerData().loading.put(playerName, false);
			}
			default:
				return;
		}
	}
}
