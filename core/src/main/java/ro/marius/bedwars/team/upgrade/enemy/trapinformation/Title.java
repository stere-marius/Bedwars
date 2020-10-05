package ro.marius.bedwars.team.upgrade.enemy.trapinformation;

import org.bukkit.entity.Player;
import ro.marius.bedwars.manager.ManagerHandler;

public class Title {

    private final String title;
    private final String subTitle;
    private int fadein;
    private int stay;
    private int fadeout;

    public Title(String title, String subTitle, int fadein, int stay, int fadeout) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadein = fadein;
        this.stay = stay;
        this.fadeout = fadeout;
    }

    public void sendTitle(Player p) {
        ManagerHandler.getVersionManager().getVersionWrapper().sendTitle(p, this.fadein, this.stay, this.fadeout, this.title, this.subTitle, true, false);
    }

    public String getTitle() {
        return this.title;
    }

    public int getFadein() {
        return this.fadein;
    }

    public void setFadein(int fadein) {
        this.fadein = fadein;
    }

    public int getStay() {
        return this.stay;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public int getFadeout() {
        return this.fadeout;
    }

    public void setFadeout(int fadeout) {
        this.fadeout = fadeout;
    }
}
