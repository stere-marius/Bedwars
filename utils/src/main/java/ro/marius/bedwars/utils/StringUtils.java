package ro.marius.bedwars.utils;

import org.bukkit.ChatColor;

import java.util.TreeMap;

public class StringUtils {

    private final static TreeMap<Integer, String> map = new TreeMap<>();
    private final static int CENTER_PX = 154;

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String getFirstLetterUpperCase(String string) {

        if ((string == null) || string.isEmpty()) {
            return "";
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String toRoman(int number) {

        if (number == 0) {
            return "0";
        }

        int l = map.floorKey(number);

        if (number == l) {
            return map.get(number);
        }

        return map.get(l) + toRoman(number - l);
    }

    public static String formatIntoHHMMSS(int secs) {
        int remainder = secs % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;

        return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    public static String getCenteredMessage(String message) {
        if ((message == null) || "".equals(message)) {
            return "";
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                if ((c == 'l') || (c == 'L')) {
                    isBold = true;
                    continue;
                } else {
                    isBold = false;
                }
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }

}
