package ro.marius.bedwars.manager.type;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.LevelConfiguration;
import ro.marius.bedwars.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BWLevelManager {

    private File file = new File(BedWarsPlugin.getInstance().getDataFolder(), "bedwars_level.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(this.file);
    private Map<String, LevelConfiguration> levels = new HashMap<>();

    public BWLevelManager() {

    }

    public void loadLevels() {

        for (String str : this.config.getConfigurationSection("levels").getKeys(false)) {

            if (str.length() == 1) {

                int level = Utils.getInteger(str, -1);

                if (level == -1) {
                    // TODO: Send error
                    continue;
                }


                continue;
            }

            String[] interval = str.split(",");

            if (interval.length == 0) {

                continue;
            }

            int firstNumber = Utils.getInteger(interval[0], -1);
            int secondNumber = Utils.getInteger(interval[1], -1);

            if ((firstNumber == -1) || (secondNumber == -1)) {

                continue;
            }


        }

    }

    public File getFile() {
        return this.file;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public Map<String, LevelConfiguration> getLevels() {
        return this.levels;
    }
}
