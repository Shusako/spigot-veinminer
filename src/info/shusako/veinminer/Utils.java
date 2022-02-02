package info.shusako.veinminer;

//import net.minecraft.server.v1_13_R2.BlockPosition;

import info.shusako.veinminer.commands.types.Value;
import info.shusako.veinminer.enums.ActivationMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Shusako on 12/15/2018.
 * For project Minecraft Veinminer Plugin, 2018
 */
public class Utils {

    public static boolean arrayContains(String[] array, String testItem) {
        for (String arrayItem : array) {
            if (arrayItem.equals(testItem)) {
                return true;
            }
        }
        return false;
    }

    public static int arrayStartsWithIndexOf(String[] configNames, String providedConfigName) {
        for (int i = 0; i < configNames.length; i++) {
            if (providedConfigName.startsWith(configNames[i])) {
                return i;
            }
        }
        return -1;
    }

    public static String getUUIDFromName(String name, Server server) {
        Player player = server.getPlayer(name);
        if (player != null) {
            return player.getUniqueId().toString();
        }
//        Collection<? extends Player> onlinePlayers = server.getOnlinePlayers();
//        for(Player player : onlinePlayers) {
//            // names are case insensitive
//            if(player.getName().equalsIgnoreCase(name)) {
//                return player.getUniqueId().toString();
//            }
//        }

        OfflinePlayer offlinePlayer = server.getOfflinePlayer(name);
        if (offlinePlayer != null) {
            return offlinePlayer.getUniqueId().toString();
        }

//        OfflinePlayer[] offlinePlayers = server.getOfflinePlayers();
//        for(OfflinePlayer offlinePlayer : offlinePlayers) {
//            if(offlinePlayer.getName().equalsIgnoreCase(name)) {
//                return offlinePlayer.getUniqueId().toString();
//            }
//        }

//        throw new IllegalArgumentException("Player " + name + " could not be found");
        return ""; // There are dependencies on this being empty string on non-existance
    }

    public static void smartCompletion(List<String> options, String[] currentOptions, String typed) {
        for (String s : currentOptions) {
            if (s.toLowerCase().contains(typed.toLowerCase())) {
                options.add(s);
            }
        }

        Collections.sort(options);

        // TODO: if an item actually starts with 'typed', then it should be first, regardless of sort order
    }

//    public static BlockPosition locationToBlockPosition(Location location) {
//        return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
//    }

    public static String capitalize(String s) {
        if (s.length() == 0)
            return s;
        if (s.length() == 1)
            return s.toUpperCase();
        else
            return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static boolean isTool(ItemStack itemStack) {
        String materialString = itemStack.getType().name().toUpperCase();
        return materialString.contains("PICKAXE") || materialString.contains("AXE") || materialString.contains
                ("SHOVEL") || materialString.contains("SHEARS");
    }

    public static <T> T getValueFromObject(Class<T> type, Object value) {
        if(type.isEnum()) {
            Optional<T> optional = Arrays.stream(type.getEnumConstants())
                    .filter(activationMode -> activationMode.toString().equalsIgnoreCase(value.toString())).findFirst();
            if (optional.isPresent()) {
                return optional.get();
            } else {
                throw new IllegalArgumentException("Unable to find enum, it had value of " + value);
            }
        } else if(type == Integer.class) {
            try {
                return type.cast(Integer.parseInt(value.toString()));
            } catch (NumberFormatException ignored) {
                throw new IllegalArgumentException("Unable to convert to Integer, it had a value of " + value);
            }
        } else if(type == Boolean.class) {
            return type.cast(Boolean.parseBoolean(value.toString()));
        } else {
            return type.cast(value);
        }
    }

    public static <T> T getConfigValue(Class<T> type, String configPath) {
        Object obj = Veinminer.instance.getConfig().get(configPath);
        if (obj == null) throw new AssertionError();

        return getValueFromObject(type, obj);
    }

    public static boolean hasConfigPath(String configPath) {
        return Veinminer.instance.getConfig().contains(configPath);
    }

    public static <T> T getServerSettings(Class<T> type, String settingName) {
        return getConfigValue(type, "server." + settingName);
    }

    public static <T> T getSetting(Class<T> type, String configPath, String settingName) {
        T value = null;

        if (hasConfigPath(configPath)) {
            value = getConfigValue(type, configPath);

            if (type == Integer.class) {
                value = type.cast(clampToServerBounds(settingName, (Integer) value));
            }
        } else {
            value = getServerSettings(type, settingName + ".default");
        }

        return value;
    }

    public static <T> T getPlayerSetting(Class<T> type, Player player, String settingName) {
        return getSetting(type, ("players." + player.getUniqueId() + "." + settingName), settingName);
    }

    public static String getPlayerConfigLocation(String player, String settingName) {
        return "players." + Utils.getUUIDFromName(player, Veinminer.instance.getServer()) + "." + settingName;
    }

    public static int clampToServerBounds(String settingName, int value) {
        // TODO: better way to deal with /veinminer get server max_blocks.default
        if (settingName.toLowerCase().endsWith(".default")) {
            settingName = settingName.substring(0, settingName.length() - 8);
        }

        int maxValue = getServerSettings(Integer.class, settingName + ".max");
        int minValue = getServerSettings(Integer.class, settingName + ".min");

        if (value > maxValue)
            return maxValue;
        if (value < minValue)
            return minValue;
        return value;
    }

    public static boolean isInServerBounds(String settingName, int value) {
        // TODO: better way to deal with /veinminer get server max_blocks.default
        if (settingName.toLowerCase().endsWith(".default")) {
            settingName = settingName.substring(0, settingName.length() - 8);
        }

        int maxValue = getServerSettings(Integer.class, settingName + ".max");
        int minValue = getServerSettings(Integer.class, settingName + ".min");

        if (value > maxValue)
            return false;
        if (value < minValue)
            return false;
        return true;
    }

    public static void setConfigValue(String configPath, Object value) {
        if(value.getClass().isEnum()) {
            Veinminer.instance.getConfig().set(configPath, value.toString());
        } else {
            Veinminer.instance.getConfig().set(configPath, value);
        }
        Veinminer.instance.saveConfig();
    }
}
