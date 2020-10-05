package ro.marius.bedwars.game.mechanics.worldadapter;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import org.bukkit.Bukkit;
import ro.marius.bedwars.BedWarsPlugin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BWSlimeLoader implements SlimeLoader {

    private final File worldDir;

    public BWSlimeLoader() {
        this.worldDir = new File(BedWarsPlugin.getInstance().getDataFolder(), "/slime_worlds");

        if (this.worldDir.exists() && !this.worldDir.isDirectory()) {
            Bukkit.getLogger().warning("A file named '" + this.worldDir.getName()
                    + "' has been deleted, as this is the name used for the worlds directory.");
            this.worldDir.delete();
        }

        this.worldDir.mkdirs();
    }

    @Override
    public byte[] loadWorld(String worldName, boolean readOnly)
            throws UnknownWorldException, IOException, WorldInUseException {
        if (!this.worldExists(worldName)) {
            throw new UnknownWorldException(worldName);
        }

        if (!readOnly) {
            File lockFile = new File(this.worldDir, worldName + ".slime_lock");

            if (lockFile.exists()) {
                throw new WorldInUseException(worldName);
            }

            lockFile.createNewFile();

            try (FileOutputStream fileStream = new FileOutputStream(lockFile);
                 DataOutputStream dataStream = new DataOutputStream(fileStream)) {
                dataStream.writeLong(System.currentTimeMillis());
            }
        }

        File file = new File(this.worldDir, worldName + ".slime");

        return Files.readAllBytes(file.toPath());
    }

    @Override
    public boolean worldExists(String worldName) {
        return new File(this.worldDir, worldName + ".slime").exists();
    }

    @Override
    public List<String> listWorlds() throws NotDirectoryException {
        if (this.worldDir.list() == null) {
            throw new NotDirectoryException(this.worldDir.getPath());
        }

        return Arrays.stream(this.worldDir.list()).filter(c -> c.endsWith(".slime"))
                .map((c) -> c.substring(0, c.length() - 6)).collect(Collectors.toList());
    }

    @Override
    public void saveWorld(String worldName, byte[] serializedWorld, boolean lock) throws IOException {
        File lastBackup = new File(this.worldDir, worldName + ".slime_old");

        if (lastBackup.exists()) {
            lastBackup.delete();
        }

        File file = new File(this.worldDir, worldName + ".slime");

        if (file.exists()) {
            file.renameTo(lastBackup);
        }

        try (FileOutputStream fileStream = new FileOutputStream(file, false)) {
            fileStream.write(serializedWorld);
            fileStream.flush();
            lastBackup.delete();
        }

        if (lock) {
            // Make sure the lock file is there
            File lockFile = new File(this.worldDir, worldName + ".slime_lock");

            if (!lockFile.exists()) {
                lockFile.createNewFile();

                try (FileOutputStream fileStream = new FileOutputStream(lockFile);
                     DataOutputStream dataStream = new DataOutputStream(fileStream)) {
                    dataStream.writeLong(System.currentTimeMillis());
                }
            }
        }
    }

    @Override
    public void unlockWorld(String worldName) throws UnknownWorldException {
        if (!this.worldExists(worldName)) {
            throw new UnknownWorldException(worldName);
        }

        File lockFile = new File(this.worldDir, worldName + ".slime_lock");
        lockFile.delete();
    }

    @Override
    public boolean isWorldLocked(String worldName) {
        return new File(this.worldDir, worldName + ".slime_lock").exists();
    }

    @Override
    public void deleteWorld(String worldName) throws UnknownWorldException {
        if (!this.worldExists(worldName)) {
            throw new UnknownWorldException(worldName);
        }

        new File(this.worldDir, worldName + ".slime_lock").delete();
        new File(this.worldDir, worldName + ".slime").delete();
    }
}
