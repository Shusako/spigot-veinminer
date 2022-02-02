package info.shusako.veinminer.commands.types;

import info.shusako.veinminer.commands.CommandContext;
import info.shusako.veinminer.commands.ISubCommand;

import java.util.Map;

public class Setting implements ICommandType {
    private ISubCommand<Setting> command;
    public String configPath;
    public String settingName;
    public Class<?> clazz;
    public Map<String, Class<?>> allowedValues;

    public Setting(ISubCommand<Setting> command) {
        this.command = command;
    }

    @Override
    public ISubCommand<Setting> getCommand() {
        return command;
    }

    @Override
    public boolean isValid() {
        return (configPath != null && settingName != null && !configPath.equals("") && !settingName.equals("") &&
                clazz != null);
    }

    @Override
    public void execute(CommandContext context) {

    }
}
