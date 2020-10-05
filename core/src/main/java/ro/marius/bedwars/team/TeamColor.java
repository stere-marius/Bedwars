package ro.marius.bedwars.team;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;

public enum TeamColor {

    WHITE(XMaterial.WHITE_WOOL.parseItem(), "&f", Color.WHITE, XMaterial.WHITE_BED.parseBlockMaterial(), 0),
    ORANGE(XMaterial.ORANGE_WOOL.parseItem(), "&6", Color.ORANGE, XMaterial.ORANGE_BED.parseBlockMaterial(), 1),
    MAGENTA(XMaterial.MAGENTA_WOOL.parseItem(), "&5", Color.fromRGB(199, 78, 189),
            XMaterial.MAGENTA_BED.parseBlockMaterial(), 2),
    LIGHT_BLUE(XMaterial.LIGHT_BLUE_WOOL.parseItem(), "&b", Color.AQUA, XMaterial.LIGHT_BLUE_BED.parseBlockMaterial(),
            3),
    AQUA(XMaterial.LIGHT_BLUE_WOOL.parseItem(), "&b", Color.AQUA, XMaterial.LIGHT_BLUE_BED.parseBlockMaterial(), 3),
    YELLOW(XMaterial.YELLOW_WOOL.parseItem(), "&e", Color.YELLOW, XMaterial.YELLOW_BED.parseBlockMaterial(), 4),
    LIME(XMaterial.LIME_WOOL.parseItem(), "&a", Color.LIME, XMaterial.LIME_BED.parseBlockMaterial(), 5),
    PINK(XMaterial.PINK_WOOL.parseItem(), "&d", Color.fromRGB(243, 139, 170), XMaterial.PINK_BED.parseBlockMaterial(),
            6),
    GRAY(XMaterial.GRAY_WOOL.parseItem(), "&8", Color.fromRGB(170, 170, 170), XMaterial.GRAY_BED.parseBlockMaterial(),
            7),
    LIGHT_GRAY(XMaterial.LIGHT_GRAY_WOOL.parseItem(), "&7", Color.fromRGB(157, 157, 151),
            XMaterial.LIGHT_GRAY_BED.parseBlockMaterial(), 8),
    CYAN(XMaterial.CYAN_WOOL.parseItem(), "&b", Color.fromRGB(22, 156, 156), XMaterial.CYAN_BED.parseBlockMaterial(),
            9),
    PURPLE(XMaterial.PURPLE_WOOL.parseItem(), "&5", Color.PURPLE, XMaterial.PURPLE_BED.parseBlockMaterial(), 10),
    BLUE(XMaterial.BLUE_WOOL.parseItem(), "&9", Color.BLUE, XMaterial.BLUE_BED.parseBlockMaterial(), 11),
    BROWN(XMaterial.BROWN_WOOL.parseItem(), "&f", Color.fromRGB(131, 84, 50), XMaterial.BROWN_BED.parseBlockMaterial(),
            12),
    GREEN(XMaterial.GREEN_WOOL.parseItem(), "&2", Color.GREEN, XMaterial.GREEN_BED.parseBlockMaterial(), 13),
    RED(XMaterial.RED_WOOL.parseItem(), "&c", Color.RED, XMaterial.RED_BED.parseBlockMaterial(), 14),
    BLACK(XMaterial.BLACK_WOOL.parseItem(), "&0", Color.BLACK, XMaterial.BLACK_BED.parseBlockMaterial(), 15),
    ;

    private final ItemStack buildMaterial;
    private final Material bedBlock;
    private final String chatColor;
    private final Color armorColor;
    private final byte data;

    TeamColor(ItemStack buildMaterial, String chatColor, Color armorColor, Material bedBlock, int bedData) {
        this.buildMaterial = buildMaterial;
        this.chatColor = chatColor;
        this.armorColor = armorColor;
        this.bedBlock = bedBlock;
        this.data = (byte) bedData;
    }

    public ItemStack getBuildMaterial() {
        return this.buildMaterial;
    }

    public String getChatColor() {
        return Utils.translate(this.chatColor);
    }

    public String getUntranslatedChatColor() {
        return this.chatColor;
    }

    public Color getArmorColor() {
        return this.armorColor;
    }

    public Material getBedBlock() {
        return this.bedBlock;
    }

    public byte getData() {

        return this.data;
    }

}
