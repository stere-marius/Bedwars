package ro.marius.bedwars.utils;

public class KitUtils {

//	@SuppressWarnings("deprecation")
//	public static String inventoryToString(Inventory inventar) {
//
//		String serialization = inventar.getSize() + ";";
//		for (int i = 0; i < inventar.getSize(); i++) {
//			ItemStack is = inventar.getItem(i);
//			if (is != null) {
//				String serializedItemStack = new String();
//
//				String isType = String.valueOf(is.getType().name());
//				serializedItemStack += "t@" + isType;
//
//				if (is.getDurability() != 0) {
//					String isDurability = String.valueOf(is.getDurability()); 
//					serializedItemStack += ":d@" + isDurability;
//				}
//
//				if (is.getAmount() != 1) {
//					String isAmount = String.valueOf(is.getAmount());
//					serializedItemStack += ":a@" + isAmount;
//				}
//				if (is.hasItemMeta()) {
//					if (is.getItemMeta().getDisplayName() != null) {
//
//						String name = String.valueOf(is.getItemMeta().getDisplayName());
//						serializedItemStack += ":n@" + name;
//					}
//				}
//
//				Map<Enchantment, Integer> isEnch = is.getEnchantments();
//				if (isEnch.size() > 0) {
//					for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
//						serializedItemStack += ":e@" + ench.getKey().getKey().getNamespace() + "@" + ench.getValue();
//					}
//				}
//
//				serialization += i + "#" + serializedItemStack + ";";
//			}
//		}
//		return serialization;
//	}
//
////	@SuppressWarnings("deprecation")
//	@SuppressWarnings("deprecation")
//	public static String armorToString(Player p) {
//		String serialization = p.getInventory().getArmorContents().length + ";";
//		for (int i = 0; i < p.getInventory().getArmorContents().length; i++) {
//			ItemStack is = p.getInventory().getArmorContents()[i];
//			if (is != null) {
//				String serializedItemStack = new String();
//
//				String isType = String.valueOf(is.getType().name());
//				serializedItemStack += "t@" + isType;
//
//				if (is.getDurability() != 0) {
//					String isDurability = String.valueOf(is.getDurability());
//					serializedItemStack += ":d@" + isDurability;
//				}
//
//				if (is.getAmount() != 1) {
//					String isAmount = String.valueOf(is.getAmount());
//					serializedItemStack += ":a@" + isAmount;
//				}
//				if (is.hasItemMeta()) {
//					if (is.getItemMeta().getDisplayName() != null) {
//						String name = String.valueOf(Utils.translate(is.getItemMeta().getDisplayName()));
//						serializedItemStack += ":n@" + name;
//					}
//				}
//
//				Map<Enchantment, Integer> isEnch = is.getEnchantments();
//				if (isEnch.size() > 0) {
//					for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
//						serializedItemStack += ":e@" + ench.getKey().getName() + "@" + ench.getValue();
//					}
//				}
//
//				serialization += i + "#" + serializedItemStack + ";";
//			}
//		}
//		return serialization;
//	}
//
//	@SuppressWarnings("deprecation")
//	public static String itemStackToString(Player p) {
//		String serialization = "";
//		ItemStack is = p.getInventory().getItemInHand();
//		if (is != null) {
//			String serializedItemStack = new String();
//
//			String isType = String.valueOf(is.getType().name());
//			serializedItemStack += "t@" + isType;
//
//			if (is.getDurability() != 0) {
//				String isDurability = String.valueOf(is.getDurability());
//				serializedItemStack += ":d@" + isDurability;
//			}
//
//			if (is.getAmount() != 1) {
//				String isAmount = String.valueOf(is.getAmount());
//				serializedItemStack += ":a@" + isAmount;
//			}
//			Map<Enchantment, Integer> isEnch = is.getEnchantments();
//			if (isEnch.size() > 0) {
//				for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
//					serializedItemStack += ":e@" + ench.getKey().getName() + "@" + ench.getValue();
//				}
//			}
//
//			serialization += "#" + serializedItemStack + ";";
//		}
//		return serialization;
//	}
//
////	@SuppressWarnings("deprecation")
//	@SuppressWarnings("deprecation")
//	public static ItemStack itemStackFromString(String invString) {
//
//		String[] serializedBlock = invString.split("#");
//		// 36;0#t@322:d@1:a@64;
//		ItemStack is = null;
//		Boolean createdItemStack = false;
//
//		String[] serializedItemStack = serializedBlock[1].split(":");
//		for (String itemInfo : serializedItemStack) {
//			String[] itemAttribute = itemInfo.split("@");
//			if (itemAttribute[0].equals("t")) {
//				is = new ItemStack(Material.getMaterial(itemAttribute[1]));
//				createdItemStack = true;
//			} else if (itemAttribute[0].equals("d") && createdItemStack) {
//				is.setDurability(Short.valueOf(itemAttribute[1]));
//			} else if (itemAttribute[0].equals("a") && createdItemStack) {
//				is.setAmount(Integer.valueOf(itemAttribute[1]));
//			} else if (itemAttribute[0].equals("e") && createdItemStack) {
//				is.addUnsafeEnchantment(Enchantment.getByName(itemAttribute[1]),
//						Integer.valueOf(itemAttribute[2]));
//			}
//		}
//
//		return is;
//
//	}
//
////	@SuppressWarnings("deprecation")
//	public static Inventory stringToInventory(String invString) {
//		String[] serializedBlocks = invString.split(";");
//		String invInfo = serializedBlocks[0];
//		Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));
//
//		for (int i = 1; i < serializedBlocks.length; i++) {
//			String[] serializedBlock = serializedBlocks[i].split("#");
//			int stackPosition = Integer.valueOf(serializedBlock[0]);
//
//			if (stackPosition >= deserializedInventory.getSize()) {
//				continue;
//			}
//
//			ItemStack is = null;
//			Boolean createdItemStack = false;
//
//			String[] serializedItemStack = serializedBlock[1].split(":");
//			for (String itemInfo : serializedItemStack) {
//				String[] itemAttribute = itemInfo.split("@");
//				if (itemAttribute[0].equals("t")) {
//					is = new ItemStack(Material.getMaterial(itemAttribute[1]));
//					createdItemStack = true;
//				} else if (itemAttribute[0].equals("d") && createdItemStack) {
//					is.setDurability(Short.valueOf(itemAttribute[1]));
//				} else if (itemAttribute[0].equals("a") && createdItemStack) {
//					is.setAmount(Integer.valueOf(itemAttribute[1]));
//				} else if (itemAttribute[0].equals("n") && createdItemStack) {
//					ItemMeta meta = is.getItemMeta();
//					meta.setDisplayName(String.valueOf(itemAttribute[1].replaceAll("&", "�")));
//					is.setItemMeta(meta);
//				} else if (itemAttribute[0].equals("e") && createdItemStack) {
//
//					is.addUnsafeEnchantment(Enchantment.getByName(itemAttribute[1]),
//							Integer.valueOf(itemAttribute[2]));
//				}
//			}
//			deserializedInventory.setItem(stackPosition, is);
//		}
//
//		return deserializedInventory;
//	}
//
////	@SuppressWarnings("deprecation")
//	public static ItemStack[] armorFromInventory(String invString) {
//		String[] serializedBlocks = invString.split(";");
//		ItemStack helmet = null;
//		ItemStack chestplate = null;
//		ItemStack leggings = null;
//		ItemStack boots = null;
//
//		for (int i = 1; i < serializedBlocks.length; i++) {
//			String[] serializedBlock = serializedBlocks[i].split("#");
//			int stackPosition = Integer.valueOf(serializedBlock[0]);
//
//			ItemStack is = null;
//			Boolean createdItemStack = false;
//
//			String[] serializedItemStack = serializedBlock[1].split(":");
//			for (String itemInfo : serializedItemStack) {
//				String[] itemAttribute = itemInfo.split("@");
//				if (itemAttribute[0].equals("t")) {
//					is = new ItemStack(Material.getMaterial(itemAttribute[1]));
//					createdItemStack = true;
//				} else if (itemAttribute[0].equals("d") && createdItemStack) {
//					is.setDurability(Short.valueOf(itemAttribute[1]));
//				} else if (itemAttribute[0].equals("n") && createdItemStack) {
//					ItemMeta meta = is.getItemMeta();
//					meta.setDisplayName(String.valueOf(itemAttribute[1].replaceAll("&", "�")));
//					is.setItemMeta(meta);
//				} else if (itemAttribute[0].equals("a") && createdItemStack) {
//					is.setAmount(Integer.valueOf(itemAttribute[1]));
//				} else if (itemAttribute[0].equals("e") && createdItemStack) {
//					is.addUnsafeEnchantment(Enchantment.getByName(itemAttribute[1]),
//							Integer.valueOf(itemAttribute[2]));
//				}
//			}
//			if (stackPosition == 0) {
//				helmet = is;
//			} else if (stackPosition == 1) {
//				chestplate = is;
//			} else if (stackPosition == 2) {
//				leggings = is;
//			} else if (stackPosition == 3) {
//				boots = is;
//			}
//		}
//
//		return new ItemStack[] { helmet, chestplate, leggings, boots };
//	}

//	public static void giveKit(Player p, Kit kit) {
//		ItemStack[] inv = kit.getArmor();
//		p.getInventory().setArmorContents(inv);
//		p.getInventory().setContents(kit.getContents().getContents());
//		p.updateInventory();
//	}


}
