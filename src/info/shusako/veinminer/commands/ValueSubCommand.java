package info.shusako.veinminer.commands;

import info.shusako.veinminer.Utils;
import info.shusako.veinminer.commands.types.Setting;
import info.shusako.veinminer.commands.types.Value;

import java.util.ArrayList;
import java.util.List;

public class ValueSubCommand implements ISubCommand<Value> {

    @Override
    public Value parse(CommandContext context, String[] args, int index) {
        Value value = new Value(this);

        if (args.length <= index) {
            context.failParse("Need to provide a value");
            return value;
        }

        Setting setting = context.getContext(Setting.class);
        if (!setting.isValid()) {
            context.failParse("Setting invalid");
            return value;
        }

        try {
            value.value = Utils.getValueFromObject(setting.clazz, args[index]);
        } catch (Exception ignored) {}

        return value;
    }

    @Override
    public List<String> autoComplete(CommandContext context, Value type, int index) {
        if (context.args.length - 1 != index) {
            return new ArrayList<>();
        }

        List<String> strings = new ArrayList<>();

        Setting setting = context.getContext(Setting.class);

        if(setting.clazz.isEnum()) {
            Object[] arr = setting.clazz.getEnumConstants();
            for (Object o : arr) {
                if (o.toString().toLowerCase().startsWith(context.args[index].toLowerCase())) {
                    strings.add(o.toString());
                }
            }
        } else if(setting.clazz == Boolean.class) {
            strings.add("TRUE");
            strings.add("FALSE");
        }

        return strings;
    }
}
