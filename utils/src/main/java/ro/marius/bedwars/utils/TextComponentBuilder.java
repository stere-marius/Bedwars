package ro.marius.bedwars.utils;


import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentBuilder {

    private final TextComponent textComponent;

    public TextComponentBuilder(String message) {
        this.textComponent = new TextComponent(Utils.translate(message));
    }

    public TextComponentBuilder withClickEvent(ClickEvent.Action action, String value) {
        this.textComponent.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public TextComponent build() {
        return this.textComponent;
    }

}

