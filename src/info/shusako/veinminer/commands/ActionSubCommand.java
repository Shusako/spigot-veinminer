package info.shusako.veinminer.commands;

import info.shusako.veinminer.Utils;
import info.shusako.veinminer.Veinminer;
import info.shusako.veinminer.commands.types.Action;
import info.shusako.veinminer.commands.types.Setting;
import info.shusako.veinminer.commands.types.Target;
import info.shusako.veinminer.commands.types.Value;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/*
    ACTION: (get <TARGET> <CONFIG>)|(set <TARGET> <CONFIG> <VALUE>)
 */
public class ActionSubCommand implements ISubCommand<Action> {

    @Override
    public Action parse(CommandContext context, String[] args, int index) {
        Action action = new Action(this);
        String actionStr = args[index].toLowerCase();
        if (!(actionStr.equals("get") || actionStr.equals("set"))) {
            context.failParse("Action must be one of: get, set");
            return action;
        }

        action.target = context.parseSubCommand(TargetSubCommand.class, index + 1);
        action.setting = context.parseSubCommand(SettingSubCommand.class, index + 2);

        switch (actionStr) {
            case "get":
                action.type = Action.ActionType.GET;
                break;
            case "set":
                action.type = Action.ActionType.SET;
                action.value = context.parseSubCommand(ValueSubCommand.class, index + 3);
                break;
        }

        return action;
    }

    @Override
    public List<String> autoComplete(CommandContext context, Action action, int index) {
        if(!action.isValid()) {
            if(context.args.length - 1 != index) {
                return new ArrayList<>();
            }

            List<String> options = new ArrayList<>();
            options.add("get");
            options.add("set");
            return options;
        }

        if(!action.target.isValid()) {
            return action.target.getCommand().autoComplete(context, action.target, index + 1);
        }

        if(!action.setting.isValid()) {
            return action.setting.getCommand().autoComplete(context, action.setting, index + 2);
        }

        if(action.type == Action.ActionType.SET && !action.value.isValid()) {
            return action.value.getCommand().autoComplete(context, action.value, index + 3);
        }

        return new ArrayList<>();
    }
}
