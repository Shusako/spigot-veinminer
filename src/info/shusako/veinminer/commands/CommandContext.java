package info.shusako.veinminer.commands;

import info.shusako.veinminer.Veinminer;
import info.shusako.veinminer.commands.types.ICommandType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandContext {

    public String[] args;
    public CommandSender sender;
    private final List<ICommandType> commandContexts;

    public boolean parseFailed;
    public String parseFailureReason;

    public CommandContext(CommandSender sender, String[] args) {
        this.args = args;
        this.sender = sender;
        this.commandContexts = new ArrayList<>();
    }

    public <T extends ISubCommand<G>, G extends ICommandType> G parseSubCommand(Class<T> commandClass, int index) {
        try {
            T subCommand = commandClass.newInstance();
            G commandType = subCommand.parse(this, this.args, index);
            this.commandContexts.add(commandType);
            return commandType;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends ICommandType> T getContext(Class<T> clazz) {
        for (ICommandType type : commandContexts) {
            if(type.getClass() == clazz) {
                return clazz.cast(type);
            }
        }
        return null;
    }

    public void addContext(ICommandType type) {
        this.commandContexts.add(type);
    }

    public <T extends ISubCommand<G>, G extends ICommandType> List<String> autoComplete(Class<T> commandClass,
                                                                                        int index) {
        try {
            T subCommand = commandClass.newInstance();
            G commandType = subCommand.parse(this, this.args, index);
            return subCommand.autoComplete(this, commandType, index);
        } catch (InstantiationException | IllegalAccessException e) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add(e.getMessage());
            if(this.parseFailed) {
                strings.add(this.parseFailureReason);
            }
            return strings;
        }
    }

    public void failParse(String reason) {
        if(parseFailed) return;

        parseFailed = true;
        parseFailureReason = reason;
    }
}
