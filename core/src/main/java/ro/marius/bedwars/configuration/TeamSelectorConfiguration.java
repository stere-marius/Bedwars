package ro.marius.bedwars.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.utils.FileUtils;

import java.io.File;

public class TeamSelectorConfiguration {

    private final File file;
    private final YamlConfiguration config;

    public TeamSelectorConfiguration() {
        this.file = new File(BedWarsPlugin.getInstance().getDataFolder(), "team_selector.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void loadConfiguration() {
        FileUtils.saveDefaultResource(file, BedWarsPlugin.getInstance().getResource("team_selector.yml"));
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
