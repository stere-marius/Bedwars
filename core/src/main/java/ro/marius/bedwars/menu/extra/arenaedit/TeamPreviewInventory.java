package ro.marius.bedwars.menu.extra.arenaedit;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.InventoryUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.conversational.StringPrompt;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;
import ro.marius.bedwars.utils.itembuilder.SkullBuilder;

public class TeamPreviewInventory extends ExtraInventory {

    private final GameEdit gameEdit;

    public TeamPreviewInventory(GameEdit gameEdit) {
        this.gameEdit = gameEdit;
    }

    @Override
    public Inventory getInventory() {

        Game game = gameEdit.getGame();
        int inventorySize = (int) Math.ceil(game.getTeams().size() / 9.0);
        Inventory inventory = Bukkit.createInventory(this, inventorySize * 9, Utils.translate("&e&lTeams preview"));

        for (int i = 0; i < game.getTeams().size(); i++) {
            Team team = game.getTeams().get(i);
            inventory.setItem(i, new ItemBuilder(team.getTeamColor().getBuildMaterial())
                    .setDisplayName("&e&lTeam " + team.getName())
                    .setLore(
                            "",
                            "&e&lLEFT &7click to &a&ledit",
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
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        Game game = gameEdit.getGame();

        System.out.println(e.getAction());

        if (e.getCurrentItem() == null)
            return;

        switch (e.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                e.setCancelled(true);
        }

        if (e.getSlot() >= 0 && e.getSlot() < game.getTeams().size()) {

            Team team = game.getTeams().get(e.getSlot());

            if (e.getClick() == ClickType.SHIFT_RIGHT || e.getClick() == ClickType.SHIFT_LEFT) {

                StringPrompt confirmPrompt = new StringPrompt("&e>> Are you sure you want to delete the team " + team.getName() + "? Type &a&lYES &eor &4&lNO &ein chat to confirm.",
                        answer -> !answer.equalsIgnoreCase("Yes") && !answer.equalsIgnoreCase("No"),
                        answer -> {

                            if (answer.equalsIgnoreCase("YES")) {
                                p.sendRawMessage(Utils.translate("&e>> The team " + game.getTeams().get(e.getSlot()).getName() + " has been deleted"));
                                game.getTeams().remove(e.getSlot());
                                p.openInventory(getInventory());
                                return;
                            }

                            p.sendRawMessage(Utils.translate("&e>> You have declined the removing of the team."));
                        },
                        answer -> p.sendRawMessage(Utils.translate("&e>> The answer must be YES or NO ")));

                ConversationFactory conversationFactory = new ConversationFactory(BedWarsPlugin.getInstance());

                Conversation conversation = conversationFactory
                        .withFirstPrompt(confirmPrompt)
                        .withLocalEcho(false)
                        .withTimeout(10)
                        .buildConversation(p);

                conversation.begin();
                e.setCancelled(true);
                p.closeInventory();
                gameEdit.setCurrentConversation(conversation);
                gameEdit.setCurrentTask(p);

                return;
            }

            if (e.getClick() == ClickType.LEFT) {
                TeamEditInventory teamEditInventory = new TeamEditInventory(team, gameEdit);
                p.openInventory(teamEditInventory.getInventory());
            }

            return;
        }

        if (e.getSlot() == e.getInventory().getSize() - 1) {

            GameEditInventory gameEditInventory = new GameEditInventory(gameEdit);
            p.openInventory(gameEditInventory.getInventory());

        }

    }
}
