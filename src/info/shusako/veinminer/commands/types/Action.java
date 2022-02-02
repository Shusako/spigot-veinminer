package info.shusako.veinminer.commands.types;

import info.shusako.veinminer.Utils;
import info.shusako.veinminer.commands.CommandContext;
import info.shusako.veinminer.commands.ISubCommand;
import org.bukkit.ChatColor;

public class Action implements ICommandType {

    private ISubCommand<Action> command;
    public ActionType type;
    public Target target;
    public Setting setting;
    public Value value;

    public Action(ISubCommand<Action> command) {
        this.command = command;
    }

    @Override
    public ISubCommand<Action> getCommand() {
        return command;
    }

    @Override
    public boolean isValid() {
        if (type == null) return false;
        if (target == null || setting == null) return false;
        if (type == ActionType.SET && value == null) return false;

        return true;
    }

    @Override
    public void execute(CommandContext context) {
        if (type == Action.ActionType.SET) {
            Utils.setConfigValue(setting.configPath, value.value);

            if (!Utils.getSetting(setting.clazz, setting.configPath, setting.settingName)
                    .toString()
                    .equalsIgnoreCase(value.value.toString())) {
                context.sender.sendMessage(ChatColor.RED + "Value outside of server bounds, it has been set to " +
                        "within the server bounds");
            }
        }

        context.sender.sendMessage(
                ChatColor.GRAY + Utils.capitalize(target.toString()) + " has a " + setting.settingName +
                        " is " + ChatColor.GREEN +
                        Utils.getSetting(setting.clazz, setting.configPath, setting.settingName) +
                        ChatColor.GRAY + ".");
    }

    public enum ActionType {
        GET, SET;
    }
}
