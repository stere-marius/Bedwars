package ro.marius.bedwars.menu.extra.arenaedit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.listeners.GameEditListener;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.utils.InventoryUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.conversational.NumberPrompt;
import ro.marius.bedwars.utils.conversational.StringPrompt;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;
import ro.marius.bedwars.utils.itembuilder.SkullBuilder;

public class GameEditInventory extends ExtraInventory {

    private final GameEdit gameEdit;

    public GameEditInventory(GameEdit gameEdit) {
        this.gameEdit = gameEdit;
    }

    @Override
    public Inventory getInventory() {

        Game game = gameEdit.getGame();

        Inventory inventory = Bukkit.createInventory(this, 54, Utils.translate("&eEditing arena " + game.getName()));
        inventory.setItem(1, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViOGIzZDhjNzdkZmI4ZmJkMjQ5NWM4NDJlYWM5NGZmZmE2ZjU5M2JmMTVhMjU3NGQ4NTRkZmYzOTI4In19fQ==")
                .setDisplayName("&e&lName of the arena")
                .setLore(
                        "",
                        "&7 Current name: &e&l" + game.getName(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        inventory.setItem(3, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=")
                .setDisplayName("&e&lWaiting location")
                .setLore(
                        " ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());


        inventory.setItem(5, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQxZTk5NzkyODlmMDMwOTlhN2M1ODdkNTJkNDg4ZTI2ZTdiYjE3YWI1OTRiNjlmOTI0MzhkNzdlYWJjIn19fQ==")
                .setDisplayName("&e&lSpectate location")
                .setLore(
                        " ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        inventory.setItem(7, new ItemBuilder(XMaterial.IRON_BARS.parseMaterial())
                .setDisplayName("&e&lArena selection")
                .setLore("",
                        "&e&lCLICK &7to open")
                .build());


        inventory.setItem(9, new ItemBuilder(XMaterial.IRON_BARS.parseMaterial())
                .setDisplayName("&e&lWaiting lobby selection")
                .setLore("",
                        "&e&lCLICK &7to open")
                .build());

        inventory.setItem(11, new ItemBuilder(XMaterial.DIAMOND_BLOCK.parseMaterial())
                .setDisplayName("&e&lDiamond Generators")
                .setLore("",
                        "&e&lCLICK &7to open")
                .build());


        inventory.setItem(13, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0==")
                .setDisplayName("&e&lType of the arena")
                .setLore("",
                        "&7Current arena type: &e&l" + game.getArenaType(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        inventory.setItem(15, new SkullBuilder()
                .setDisplayName("&e&lNumber of players per team")
                .setLore("",
                        "&7Current players per team: &e&l" + game.getPlayersPerTeam(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        inventory.setItem(17, new ItemBuilder(XMaterial.COMMAND_BLOCK.parseMaterial())
                .setDisplayName("&e&lTeams settings")
                .setLore("",
                        "&e&lCLICK &7to open")
                .build());


        inventory.setItem(19, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial())
                .setDisplayName("&e&lEmerald Generators")
                .setLore("",
                        "&e&lCLICK &7to open"
                )
                .build());

        inventory.setItem(21, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E2ZTUzYmZiN2MxM2ZlZGJkZmU4OTY3NmY4MWZjMmNhNzk3NDYzNGE2ODQxNDFhZDFmNTE2NGYwZWRmNGEyIn19fQ==")
                .setDisplayName("&e&lMinimum teams to start")
                .setLore("",
                        "&7Current number: &e&l" + game.getMinTeamsToStart(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        inventory.setItem(21, new ItemBuilder(XMaterial.BEACON.parseMaterial())
                .setDisplayName("&e&lLobby CuboidSelection")
                .setLore("",
                        "&e&lCLICK &7to open")
                .build());

        inventory.setItem(23, new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial())
                .setDisplayName("&e&lScoreboard path")
                .setLore("",
                        "&7Current path: &e&l" + game.getScoreboardPath(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        inventory.setItem(25, new ItemBuilder(XMaterial.BIRCH_SIGN.parseMaterial())
                .setDisplayName("&e&lShop path")
                .setLore("",
                        "&7Current path: &e&l" + game.getShopPathName(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        inventory.setItem(27, new ItemBuilder(XMaterial.ACACIA_SIGN.parseMaterial())
                .setDisplayName("&e&lUpgrade path")
                .setLore("",
                        "&7Current path: &e&l" + game.getUpgradePathName(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        inventory.setItem(49, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                .setDisplayName("&e&lSave changes and exit")
                .setLore("",
                        "&e&lCLICK &7to save")
                .build());


        InventoryUtils.fillEmptySlotsWithGlass(inventory);

        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();

        if (!itemStack.hasItemMeta())
            return;
        if (!itemStack.getItemMeta().hasDisplayName())
            return;

        e.setCancelled(true);

        Game game = gameEdit.getGame();
        String displayName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());

        if (displayName.equalsIgnoreCase("Name of the arena")) {

            StringPrompt gameNamePrompt = new StringPrompt("&e>> Enter the name of the game",
                    answer -> (ManagerHandler.getGameManager().getGame(answer) != null),
                    answer -> {
                        game.setName(answer);
                        p.sendRawMessage(Utils.translate("&e>> The name has been changed to " + answer));
                    },
                    answer -> p.sendRawMessage(Utils.translate("&e>> There is already an arena with the name " + answer)));

            openGameEditConversation(p, gameNamePrompt);
            return;
        }

        if (displayName.equalsIgnoreCase("Type of the arena")) {

            StringPrompt arenaTypePrompt = new StringPrompt("&e>> Enter the type of the arena",
                    answer -> game.getArenaType().equals(answer),
                    answer -> {
                        game.setArenaType(answer);
                        p.sendRawMessage(Utils.translate("&e>> The arena type has been changed to " + answer));
                    },
                    answer -> p.sendRawMessage(Utils.translate("&e>> The arena has already the arena type " + answer)));

            openGameEditConversation(p, arenaTypePrompt);
        }

        if (displayName.equalsIgnoreCase("Number of players per team")) {

            NumberPrompt numberPrompt = new NumberPrompt("&e>> Enter the number of players per team",
                    answer -> answer < 0,
                    answer -> {
                        game.setPlayersPerTeam(answer);
                        game.setMaxPlayers(answer * game.getTeams().size());
                        p.sendRawMessage(Utils.translate("&e>> The number of players per team has been changed to " + answer));
                    },
                    answer -> p.sendRawMessage(Utils.translate("&e>> The number must be greater than zero")));

            openGameEditConversation(p, numberPrompt);
            return;
        }

        if (displayName.equalsIgnoreCase("Save changes and exit")) {

            StringPrompt confirmPrompt = new StringPrompt("&eType &a&lYES &eor &4&lNO &ein chat to confirm the changes.",
                    answer -> !answer.equalsIgnoreCase("Yes") && !answer.equalsIgnoreCase("No"),
                    answer -> {

                        if (answer.equalsIgnoreCase("YES")) {
                            p.sendRawMessage(Utils.translate("&a&lThe changes has been saved"));
                            gameEdit.saveEditedGame();
                            p.getInventory().removeItem(GameEditListener.EDIT_GAME_ITEM);
                            ManagerHandler.getGameManager().getGameEdit().remove(p.getUniqueId());
                            return;
                        }

                        p.sendRawMessage(Utils.translate("&e&l>> You have declined the changes."));
                        ManagerHandler.getGameManager().getGameEdit().remove(p.getUniqueId());
                    },

                    answer -> p.sendRawMessage(Utils.translate("&e>> The answer must be YES or NO ")));

            openGameEditConversation(p, confirmPrompt);
            return;
        }

        if (displayName.equalsIgnoreCase("Waiting location")) {

            if (e.getClick() == ClickType.LEFT) {

                GameLocation gameLocation = new GameLocation(
                        Utils.convertingString(p.getLocation()),
                        p.getLocation(),
                        p.getLocation().getWorld().getName()
                );

                game.setWaitingLocation(gameLocation);
                p.sendMessage(Utils.translate("&e>> The location has been changed to " + Utils.getStringIntCoordinates(p.getLocation())));
                return;
            }

            p.teleport(game.getWaitingLocation().getLocation());

            return;
        }

        if (displayName.equalsIgnoreCase("Spectate location")) {

            if (e.getClick() == ClickType.LEFT) {

                GameLocation gameLocation = new GameLocation(
                        Utils.convertingString(p.getLocation()),
                        p.getLocation(),
                        p.getLocation().getWorld().getName()
                );

                game.setSpectateLocation(gameLocation);
                p.sendMessage(Utils.translate("&e>> The location has been changed to " + Utils.getStringIntCoordinates(p.getLocation())));
                return;
            }

            p.teleport(game.getSpectateLocation().getLocation());

            return;
        }

        if (displayName.equalsIgnoreCase("Set minimum teams to start")) {
            NumberPrompt numberPrompt = new NumberPrompt("&e>> Enter the number of minimum teams to start the arena",
                    answer -> answer < 0,
                    answer -> {
                        game.setMinTeamsToStart(answer);
                        p.sendRawMessage(Utils.translate("&e>> The number of minimum teams to start the arena has been changed to " + answer));
                    },
                    answer -> p.sendRawMessage(Utils.translate("&e>> The number must be greater than zero")));

            openGameEditConversation(p, numberPrompt);
            return;
        }

        if (displayName.equalsIgnoreCase("Waiting lobby selection")) {
            LobbySelectionInventory lobbySelectionInventory = new LobbySelectionInventory(gameEdit);
            p.openInventory(lobbySelectionInventory.getInventory());
            return;
        }

        if (displayName.equalsIgnoreCase("Scoreboard path")) {

            StringPrompt scoreboardPathPrompt = new StringPrompt("&e>> Enter the scoreboard path",
                    answer -> ManagerHandler.getScoreboardManager().getConfig().contains("ScoreboardPath." + answer),
                    answer -> {
                        game.setScoreboardPath(answer);
                        p.sendRawMessage(Utils.translate("&e>> The scoreboard path has been changed to " + answer));
                    },
                    answer -> p.sendRawMessage(Utils.translate(
                            "&e>> Could not find the scoreboard path " + answer
                                    + " . Use the command /bedwars generateScoreboardPath " + game.getName() + " to generate it.")));

            openGameEditConversation(p, scoreboardPathPrompt);
            return;
        }

        if (displayName.equalsIgnoreCase("Arena selection")) {
            GameSelectionInventory gameSelectionInventory = new GameSelectionInventory(gameEdit);
            p.openInventory(gameSelectionInventory.getInventory());
            return;
        }

        if (displayName.equalsIgnoreCase("Diamond Generators")) {
            GeneratorEditInventory generatorInventory = new GeneratorEditInventory(gameEdit, game.getDiamondGeneratorLocation(), XMaterial.DIAMOND_BLOCK.parseMaterial());
            p.openInventory(generatorInventory.getInventory());
            return;
        }

        if (displayName.equalsIgnoreCase("Teams settings")) {
            TeamPreviewInventory teamPreviewInventory = new TeamPreviewInventory(gameEdit);
            p.openInventory(teamPreviewInventory.getInventory());
            return;
        }

        if (displayName.equalsIgnoreCase("Emerald Generators")) {
            GeneratorEditInventory generatorInventory = new GeneratorEditInventory(gameEdit, game.getEmeraldGeneratorLocation(), XMaterial.EMERALD_BLOCK.parseMaterial());
            p.openInventory(generatorInventory.getInventory());
        }

    }

    private void openGameEditConversation(Player p, Prompt prompt) {
        ConversationFactory conversationFactory = new ConversationFactory(BedWarsPlugin.getInstance());

        Conversation conversation = conversationFactory
                .withFirstPrompt(prompt)
                .withLocalEcho(false)
                .withTimeout(10)
                .buildConversation(p);

        conversation.begin();
        p.closeInventory();
        gameEdit.setCurrentConversation(conversation);
        gameEdit.setCurrentTask(p);

    }

}
