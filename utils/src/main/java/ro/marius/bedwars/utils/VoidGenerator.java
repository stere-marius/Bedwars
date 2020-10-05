package ro.marius.bedwars.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {

    private final List<BlockPopulator> blockPopulator = Arrays.asList(new BlockPopulator[0]);
    private final byte[] bytes = new byte[32768];

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {

        ChunkData chunkData = this.createChunkData(world);
        chunkData.setRegion(0, 0, 0, 16, 1, 16, Material.AIR);
        chunkData.setRegion(0, 1, 0, 16, 2, 16, Material.AIR);

        return chunkData;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {

        return true;
    }

    public byte[] generate(World world, Random rand) {

        return this.bytes;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {

        return this.blockPopulator;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        Location location = new Location(world, 0, 10, 0);
        location.getBlock().setType(Material.STONE);

        return location;
    }

}
