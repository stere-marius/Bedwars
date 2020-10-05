package ro.marius.bedwars.menu;

import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.List;

public class GUIItem {

    private final ItemBuilder builder;
    private final List<String> playerCommands;

    public GUIItem(ItemBuilder builder, List<String> playerCommands) {
        this.builder = builder;
        this.playerCommands = playerCommands;
    }

    public ItemBuilder getBuilder() {
        return this.builder;
    }

    public List<String> getPlayerCommands() {
        return this.playerCommands;
    }

}
