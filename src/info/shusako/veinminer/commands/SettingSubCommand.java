package info.shusako.veinminer.commands;

import info.shusako.veinminer.Utils;
import info.shusako.veinminer.commands.types.Setting;
import info.shusako.veinminer.commands.types.Target;
import info.shusako.veinminer.enums.ActivationMode;
import info.shusako.veinminer.enums.RadiusType;
import info.shusako.veinminer.enums.VeinSearchType;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.*;

public class SettingSubCommand implements ISubCommand<Setting> {

    private static final Map<String, Class<?>> CONFIG_MAP = new HashMap<>();

    static {
        CONFIG_MAP.put("max_blocks", Integer.class);
        CONFIG_MAP.put("particles_per_edge", Integer.class);
        CONFIG_MAP.put("open_duration", Integer.class);
        CONFIG_MAP.put("particles_enabled", Boolean.class);
        CONFIG_MAP.put("sound_enabled", Boolean.class);
        CONFIG_MAP.put("sound_volume", Integer.class);
        CONFIG_MAP.put("particle", Particle.class);
        CONFIG_MAP.put("sound", Sound.class);
        CONFIG_MAP.put("vein_search_type", VeinSearchType.class);
        CONFIG_MAP.put("activation_mode", ActivationMode.class);
        CONFIG_MAP.put("blocks_per_tick", Integer.class);
        CONFIG_MAP.put("tick_skip", Integer.class);
        CONFIG_MAP.put("max_radius", Integer.class);
        CONFIG_MAP.put("radius_type", RadiusType.class);
        CONFIG_MAP.put("check_type", Boolean.class);
    }

    private static final Map<String, Class<?>> SERVER_CONFIG_MAP = new HashMap<>();

    static {
        for (Map.Entry<String, Class<?>> entry : CONFIG_MAP.entrySet()) {
            if (Integer.class.equals(entry.getValue())) {
                SERVER_CONFIG_MAP.put(entry.getKey() + ".min", Integer.class);
                SERVER_CONFIG_MAP.put(entry.getKey() + ".max", Integer.class);
            }
            SERVER_CONFIG_MAP.put(entry.getKey() + ".default", entry.getValue());
        }
    }

    @Override
    public Setting parse(CommandContext context, String[] args, int index) {
        Setting setting = new Setting(this);

        if (args.length <= index) {
            context.failParse("Need to provide a setting");
            return setting;
        }

        setting.settingName = args[index];

        // we need to know the target to display the correct settings
        Target target = context.getContext(Target.class);
        if (!target.isValid()) {
            context.failParse("Invalid target");
            return setting;
        }

        String configPath = "";

        switch (target.type) {
            case SERVER:
                setting.allowedValues = SERVER_CONFIG_MAP;
                configPath += "server.";
                break;
            case MY:
                setting.allowedValues = CONFIG_MAP;
                configPath +=
                        "players." + Utils.getUUIDFromName(context.sender.getName(), context.sender.getServer()) + ".";
                break;
            case PLAYER:
                setting.allowedValues = CONFIG_MAP;
                configPath +=
                        "players." + Utils.getUUIDFromName(target.playerName, context.sender.getServer()) + ".";
                break;
            default:
                // not possible
                context.failParse("Invalid target type provided");
                return setting;
        }

        if (!setting.allowedValues.containsKey(setting.settingName)) {
            context.failParse("Invalid key provided: " + setting + ", must be one of: " +
                    String.join(",", setting.allowedValues.keySet()));
            return setting;
        }

        Class<?> clazz = setting.allowedValues.get(setting.settingName);
        configPath += setting.settingName;

        setting.configPath = configPath;
        setting.clazz = clazz;

        return setting;
    }

    @Override
    public List<String> autoComplete(CommandContext context, Setting type, int index) {
        if (context.args.length - 1 != index) {
            return new ArrayList<>();
        }

        List<String> options = new ArrayList<>();
        if (type.allowedValues != null) {
            for (String value : type.allowedValues.keySet()) {
                if (value.toLowerCase().startsWith(context.args[index].toLowerCase())) {
                    options.add(value);
                }
            }
        }

        return options;
    }
}
