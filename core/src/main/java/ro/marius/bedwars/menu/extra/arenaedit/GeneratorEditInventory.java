package ro.marius.bedwars.menu.extra.arenaedit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.utils.InventoryUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.conversational.StringPrompt;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;
import ro.marius.bedwars.utils.itembuilder.SkullBuilder;

import java.util.List;

public class GeneratorEditInventory extends ExtraInventory {

    private final GameEdit gameEdit;
    private final List<Location> locationList;
    private final Material materialDisplayed;

    public GeneratorEditInventory(GameEdit gameEdit, List<Location> locationList, Material materialDisplayed) {
        this.gameEdit = gameEdit;
        this.locationList = locationList;
        this.materialDisplayed = materialDisplayed;
    }

    @Override
    public Inventory getInventory() {

        int inventorySize = (int) Math.ceil(locationList.size() / 9.0);
        Inventory inventory = Bukkit.createInventory(this, inventorySize * 9, Utils.translate("&e&lEditing generators"));

        for (int i = 0; i < locationList.size(); i++) {

            inventory.setItem(i, new ItemBuilder(materialDisplayed)
                    .setDisplayName("&e&lLocation " + i)
                    .setLore(
                            "",
                            "&e&lLEFT &7click to &a&lset",
                            "&e&lRIGHT &7click to &e&lteleport",
                            "&e&lSHIFT &7click to &4&ldelete")
                    .build());
        }

        inventory.setItem(inventory.getSize() - 1, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                .setDisplayName("&e&lGo back to arena edit")
                .build());

        InventoryUtils.fillEmptySlotsWithGlass(inventory);

        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {


        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);

        switch (e.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                e.setCancelled(true);
        }

        if (e.getSlot() >= 0 && e.getSlot() < locationList.size()) {


            if (e.getClick() == ClickType.SHIFT_RIGHT || e.getClick() == ClickType.SHIFT_LEFT) {
                e.setCancelled(true);
                p.updateInventory();

                StringPrompt confirmPrompt = new StringPrompt("&e>> Are you sure you want to delete this generator? Type YES or NO in chat to confirm.",
                        answer -> !answer.equalsIgnoreCase("Yes") && !answer.equalsIgnoreCase("No"),
                        answer -> {

                            if (answer.equalsIgnoreCase("YES")) {
                                locationList.remove(e.getSlot());
                                p.sendRawMessage(Utils.translate("&e>> The generator has been deleted"));
                                p.openInventory(getInventory());
                                return;
                            }

                            p.sendRawMessage(Utils.translate("&e>> You have declined the removing of the generator."));
                        },
                        answer -> p.sendRawMessage(Utils.translate("&e>> The answer must be YES or NO ")));

                ConversationFactory conversationFactory = new ConversationFactory(BedWarsPlugin.getInstance());

                Conversation conversation = conversationFactory
                        .withFirstPrompt(confirmPrompt)
                        .withLocalEcho(false)
                        .withTimeout(10)
                        .buildConversation(p);

                conversation.begin();
                p.closeInventory();
                gameEdit.setCurrentConversation(conversation);
                gameEdit.setCurrentTask(p);

                return;
            }

            if (e.getClick() == ClickType.LEFT) {
                locationList.set(e.getSlot(), p.getLocation());
                p.sendMessage(Utils.translate("&e>> The location has been changed to " + Utils.getStringIntCoordinates(p.getLocation())));
                return;
            }

            if (e.getClick() == ClickType.RIGHT) {
                p.teleport(locationList.get(e.getSlot()));
                p.sendMessage(Utils.translate("&e>> You have been teleported. "));
            }

            return;
        }

        if (e.getSlot() == e.getInventory().getSize() - 1) {

            GameEditInventory gameEditInventory = new GameEditInventory(gameEdit);
            p.openInventory(gameEditInventory.getInventory());

        }


    }
}
