package ro.marius.bedwars.floorgenerator;

import org.bukkit.Material;
import ro.marius.bedwars.utils.XMaterial;

public enum FloorGeneratorType {

    IRON(XMaterial.IRON_INGOT.parseMaterial(), "FloorGeneratorLimit.Iron.Enabled", "FloorGeneratorLimit.Iron.Amount"),
    GOLD(XMaterial.GOLD_INGOT.parseMaterial(), "FloorGeneratorLimit.Gold.Enabled", "FloorGeneratorLimit.Gold.Amount"),
    EMERALD(XMaterial.EMERALD.parseMaterial(), "FloorGeneratorLimit.Emerald.Enabled", "FloorGeneratorLimit.Emerald.Amount"),
    ;

    private Material drop;
    private String isLimitPath;
    private String limitAmountPath;

    FloorGeneratorType(Material drop, String isLimitPath, String limitAmountPath) {
        this.drop = drop;
        this.isLimitPath = isLimitPath;
        this.limitAmountPath = limitAmountPath;
    }

    public Material getDrop() {
        return this.drop;
    }

    public String getIsLimitPath() {
        return this.isLimitPath;
    }

    public String getLimitAmountPath() {
        return this.limitAmountPath;
    }
}
