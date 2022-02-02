package info.shusako.veinminer.commands.types;

import info.shusako.veinminer.commands.CommandContext;
import info.shusako.veinminer.commands.ISubCommand;

public class Target implements ICommandType {

    private ISubCommand<Target> command;
    public TargetEnum type;
    public String playerName;

    public Target(ISubCommand<Target> command) {
        this.command = command;
    }

    @Override
    public String toString() {
        if(type == TargetEnum.PLAYER) {
            return this.playerName;
        } else {
            return type.toString();
        }
    }

    @Override
    public ISubCommand<Target> getCommand() {
        return command;
    }

    @Override
    public boolean isValid() {
        if(type == null) return false;
        if(type == TargetEnum.PLAYER && playerName != null && playerName.equals("")) return false;
        return true;
    }

    @Override
    public void execute(CommandContext context) {

    }

    public enum TargetEnum {
        MY, PLAYER, SERVER;
    }
}

