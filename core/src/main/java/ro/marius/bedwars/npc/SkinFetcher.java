package ro.marius.bedwars.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.NPCSkin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SkinFetcher {

    private static final String MOJANG_PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String MOJANG_SESSION_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    public static final NPCSkin DEFAULT_NPC_SKIN = new NPCSkin(
            "eyJ0aW1lc3RhbXAiOjE1Njg1NjQzMTk3ODgsInByb2ZpbGVJZCI6ImMxYWYxODI5MDYwZTQ0OGRhNjYwOWRmZGM2OGEzOWE4IiwicH" +
                    "JvZmlsZU5hbWUiOiJCQVJLeDQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJod" +
                    "HRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU1NTlkZmU4ZGJhNWM4ODlkMDE2MTJhNTMxYjhkNDY5YzBmMzE3" +
                    "ZDk3OTcxOTA4ZWZiNjNhYTk0MzE1Yzg3YjkiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==",
            "mH5lrRm89N5X65cG2bSC6ukfERdmM1Es1Z2a4JXzJD+42nljSys3QVYmgzZeFgq660fRWyn7zPZQBP/iNQSWb3t1Qx4nVwiMvDS" +
                    "GeEjO80LRIvMJRv4zKPJh2HCJ1a8POkDS0kytYIZ2OhcTnYfHrWHBHJlk9e8tjJIgu9EOtCR3FJVyuUVZpcXF7u65mRiSp5xlAG" +
                    "iGLVVC9LPIUe0suvkRDEGDOWiFKjMGH403RDMb4Qn4vvyXpY2K7T8IA9jB9JwahdXk0or3Oz7DGhMPPDBe3gktN1XJFn3UkaBML" +
                    "jiM4tksHIvCi/AqadFr4bN9PpKdKUde+L4Q/w64l49hkTbfL2DK5XgSBqcCfMRT2gqm4T6xkeANMafF7vIVyOHoP+FARZR9FHv0" +
                    "ER9yhhEVOvihtLpND2+pUau2a+gxbtpPhFDp42rV5mxH5rS2uiNLrfNVqEI4Q8wBXbt03J8aLerVbF8uVzkLrfy7qpgUJk1Lw4l" +
                    "uKqELApf/c5nBRpPyu2h2RzKTWbf6wDHWPMhyzohmGWQSsvL3rkJl2QkOQH1+FUSI3rQ4we3mA9RyWHQEB0BwbNValreHxU0n5a" +
                    "4KYTJS5e0y2wt+63xTgMURxbWsNvPezTghvFOWK3zP+is/NKJcdNad046fnHk3DCNpCDO/naYHhmK0ei+icdlTsf8="
    );

    private static final Map<String, NPCSkin> skinCache = new HashMap<>();

    public static void getSkinFromName(String name, SkinFetcherCallback callback) {

        if (skinCache.containsKey(name)) {
            callback.onFetchDone(skinCache.get(name));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedWarsPlugin.getInstance(), () -> {
            try {
                URL url_0 = new URL(MOJANG_PROFILE_URL + name);
                InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
                String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

                URL url_1 = new URL(MOJANG_SESSION_PROFILE_URL + uuid + "?unsigned=false");
                InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
                String value = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();
                NPCSkin npcSkin = new NPCSkin(value, signature);
                skinCache.put(name, npcSkin);
                callback.onFetchDone(npcSkin);
            } catch (IOException e) {
                System.err.println("Could not get skin data from session servers!");
                e.printStackTrace();
                callback.onFetchDone(DEFAULT_NPC_SKIN);
            }
        });


    }

}
