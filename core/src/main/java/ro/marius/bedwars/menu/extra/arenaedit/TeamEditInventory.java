package ro.marius.bedwars.menu.extra.arenaedit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.TeamColor;
import ro.marius.bedwars.utils.InventoryUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.conversational.StringPrompt;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;
import ro.marius.bedwars.utils.itembuilder.SkullBuilder;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;


public class TeamEditInventory extends ExtraInventory {

    private final Team team;
    private final GameEdit gameEdit;


    public TeamEditInventory(Team team, GameEdit gameEdit) {
        this.team = team;
        this.gameEdit = gameEdit;
    }


    @Override
    public Inventory getInventory() {

        Inventory teamEditInventory = Bukkit.createInventory(this, 45, Utils.translate("&f&lEditing team " + team.getName()));


        teamEditInventory.setItem(9, new ItemBuilder(XMaterial.BIRCH_SIGN.parseMaterial())
                .setDisplayName("&e&lSet the name of the team")
                .setLore(" ",
                        "&7Current name: &e&l" + team.getName(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        teamEditInventory.setItem(11, new ItemBuilder(team.getTeamColor().getBuildMaterial())
                .setDisplayName("&e&lSet the color of the team")
                .setLore(" ",
                        "&7Current color: &l" + team.getTeamColor().getChatColor() + "" + team.getTeamColor().name(),
                        "",
                        "&e&lCLICK &7to set")
                .build());

        teamEditInventory.setItem(13, new ItemBuilder(Material.BEACON)
                .setDisplayName("&e&lSpawn location")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        teamEditInventory.setItem(15, new ItemBuilder(team.getTeamColor().getBedBlock())
                .setDisplayName("&e&lBed location")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        teamEditInventory.setItem(17, new ItemBuilder(Material.IRON_INGOT)
                .setDisplayName("&e&lIron generator location")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        teamEditInventory.setItem(27, new ItemBuilder(Material.GOLD_INGOT)
                .setDisplayName("&e&lGold generator location")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        teamEditInventory.setItem(29, new ItemBuilder(Material.EMERALD)
                .setDisplayName("&e&lEmerald generator location")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        teamEditInventory.setItem(31, new ItemBuilder(Material.CHEST)
                .setDisplayName("&e&lShop location")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        teamEditInventory.setItem(33, new ItemBuilder(XMaterial.ANVIL.parseMaterial())
                .setDisplayName("&e&lUpgrade location")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        teamEditInventory.setItem(35, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                .setDisplayName("&e&lGo back to teams preview")
                .build());

        InventoryUtils.fillEmptySlotsWithGlass(teamEditInventory);

        return teamEditInventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);

        if (e.getSlot() == 35) {
            TeamPreviewInventory teamPreviewInventory = new TeamPreviewInventory(gameEdit);
            p.openInventory(teamPreviewInventory.getInventory());
            return;
        }

        switch (e.getSlot()) {
            case 9:
                Set<String> teams = new HashSet<>();
                gameEdit.getGame().getTeams().forEach(team -> teams.add(team.getName()));

                ConversationFactory conversationFactoryName = new ConversationFactory(BedWarsPlugin.getInstance());
                StringPrompt teamNamePrompt = new StringPrompt("&e>> Enter the team name",
                        teams::contains,
                        answer -> {
                            team.setName(answer);
                            p.sendRawMessage(Utils.translate("&a>> The name has been successfully changed to " + answer));
                        },
                        answer -> p.sendRawMessage(Utils.translate("&c>> There is already a team with the name " + answer)));

                Conversation conversationName = conversationFactoryName
                        .withFirstPrompt(teamNamePrompt)
                        .withLocalEcho(false)
                        .withTimeout(10)
                        .buildConversation(p);
                conversationName.begin();
                p.closeInventory();
                gameEdit.setCurrentConversation(conversationName);
                gameEdit.setCurrentTask(p);
                return;
            case 11:
                EnumSet<TeamColor> teamColorEnumSet = EnumSet.allOf(TeamColor.class);


                ConversationFactory conversationFactoryColor = new ConversationFactory(BedWarsPlugin.getInstance());
                StringPrompt teamColorPrompt = new StringPrompt("&e>> Enter the team name",
                        answer -> teamColorEnumSet
                                .stream()
                                .anyMatch(color -> color.name().equalsIgnoreCase(answer)),
                        answer -> {
                            team.setName(answer);
                            p.sendRawMessage(Utils.translate("&a>> The name has been successfully changed to " + answer));
                        },
                        answer -> p.sendRawMessage(Utils.translate("&c>> There is already a team with the name " + answer)));

                Conversation conversationColor = conversationFactoryColor
                        .withFirstPrompt(teamColorPrompt)
                        .withLocalEcho(false)
                        .withTimeout(10)
                        .buildConversation(p);
                conversationColor.begin();
                p.closeInventory();
                gameEdit.setCurrentConversation(conversationColor);
                gameEdit.setCurrentTask(p);
                return;
        }

        if (e.getClick() == ClickType.RIGHT) {

            switch (e.getSlot()) {
                case 13:
                    p.teleport(team.getSpawnLocation().getLocation());
                    break;
                case 15:
                    p.teleport(team.getBedLocation().getLocation());
                    break;
                case 17:
                    p.teleport(team.getIronGenerator().getLocation());
                    break;
                case 27:
                    p.teleport(team.getGoldGenerator().getLocation());
                    break;
                case 29:
                    p.teleport(team.getEmeraldGenerator().getLocation());
                    break;
                case 31:
                    p.teleport(team.getShopLocation().getLocation());
                    break;
                case 33:
                    p.teleport(team.getUpgradeLocation().getLocation());
                    break;

            }

            return;
        }

        if (e.getClick() != ClickType.LEFT)
            return;

        Location playerLocation = p.getLocation();
        String convertedStringLocation = Utils.convertingString(playerLocation);
        String convertedIntStringLocation = Utils.getStringIntCoordinates(playerLocation);
        GameLocation gameLocation = new GameLocation(convertedStringLocation, playerLocation, playerLocation.getWorld().getName());

        switch (e.getSlot()) {
            case 13:
                team.setSpawnLocation(gameLocation);
                p.sendMessage(Utils.translate("&e>> The spawn location has been set to " + convertedIntStringLocation));
                break;
            case 15:

                BlockFace bedFace = ManagerHandler.getVersionManager().getVersionWrapper().getBedFace(playerLocation);

                if (bedFace == null) {
                    p.sendMessage(Utils.translate("&c>> You must stay on a bed."));
                    break;
                }

                Location bedHeadLocation = ManagerHandler.getVersionManager().getVersionWrapper().getBedHead(playerLocation);
                gameLocation.setLocation(bedHeadLocation);

                p.sendMessage(Utils.translate("&e>> The bed location has been set to " + convertedIntStringLocation));
                team.setBedLocation(gameLocation);

                break;
            case 17:
                team.getIronFloorGenerator().setLocation(gameLocation);
                team.setIronGenerator(gameLocation);
                p.sendMessage(Utils.translate("&e>> The iron generator location has been set to " + convertedIntStringLocation));
                break;
            case 27:
                team.getGoldFloorGenerator().setLocation(gameLocation);
                team.setGoldGenerator(gameLocation);
                p.sendMessage(Utils.translate("&e>> The gold generator location has been set to " + convertedIntStringLocation));
                break;
            case 29:
                team.getEmeraldFloorGenerator().setLocation(gameLocation);
                team.setEmeraldGenerator(gameLocation);
                p.sendMessage(Utils.translate("&e>> The emerald generator location has been set to " + convertedIntStringLocation));
                break;
            case 31:
                team.setShopLocation(gameLocation);
                p.sendMessage(Utils.translate("&e>> The shop location has been set to " + convertedIntStringLocation));
                break;
            case 33:
                team.setUpgradeLocation(gameLocation);
                p.sendMessage(Utils.translate("&e>> The upgrade location has been set to " + convertedIntStringLocation));
                break;

        }

    }

}
