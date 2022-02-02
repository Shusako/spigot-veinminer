package info.shusako.veinminer.commands.types;

import info.shusako.veinminer.commands.CommandContext;
import info.shusako.veinminer.commands.ISubCommand;

public interface ICommandType {

    ISubCommand<?> getCommand();

    boolean isValid();

    void execute(CommandContext context);
}
