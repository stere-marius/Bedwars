package ro.marius.bedwars.utils.itembuilder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.meta.SkullMeta;
import ro.marius.bedwars.VersionWrapper;
import ro.marius.bedwars.utils.ReflectionUtils;
import ro.marius.bedwars.utils.XMaterial;

import java.util.UUID;

public class SkullBuilder extends ItemBuilder {

    //	private final static Material PLAYER_HEAD = XMaterial.PLAYER_HEAD.parseMaterial();
    public final static ItemBuilder HEAD_BUILDER = new ItemBuilder(XMaterial.parsePlayerHead());

    public SkullBuilder() {
        super(new ItemBuilder(HEAD_BUILDER));
    }

    public SkullBuilder withOwner(UUID uuid, VersionWrapper nmsVersion) {

        SkullMeta skullMeta = (SkullMeta) this.getItemStack().getItemMeta();
        nmsVersion.setOwner(skullMeta, uuid);
        this.getItemStack().setItemMeta(skullMeta);

        return this;
    }

    public SkullBuilder withTexture(String textureValue) {

        if (textureValue.isEmpty())
            return this;

        SkullMeta skullMeta = (SkullMeta) this.getItemStack().getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", textureValue));
        ReflectionUtils.setFieldValue("profile", skullMeta.getClass(), skullMeta, profile);
//        head.setItemMeta(meta);
//        return head;
//        SkullMeta headMeta = (SkullMeta) this.getItemStack().getItemMeta();
//        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
//        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", propertyValue).getBytes());
//        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
//        ReflectionUtils.setFieldValue("profile", headMeta.getClass(), headMeta, profile);
        this.getItemStack().setItemMeta(skullMeta);

        return this;
    }


}
