package ro.marius.bedwars.generator;

import org.bukkit.Material;

public enum GeneratorType {

    DIAMOND(Material.DIAMOND), EMERALD(Material.EMERALD);

    private Material drop;

    GeneratorType(Material drop) {
        this.setDrop(drop);
    }

    public Material getDrop() {
        return this.drop;
    }

    public void setDrop(Material drop) {
        this.drop = drop;
    }

}
