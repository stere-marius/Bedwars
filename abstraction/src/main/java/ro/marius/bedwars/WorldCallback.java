package ro.marius.bedwars;

import org.bukkit.World;

public interface WorldCallback {

    void onComplete(World result, String[] message);

    void onError(String[] message);

}
