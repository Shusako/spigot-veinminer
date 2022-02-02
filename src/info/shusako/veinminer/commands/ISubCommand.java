package info.shusako.veinminer.commands;

import info.shusako.veinminer.commands.types.ICommandType;

import java.util.List;

public interface ISubCommand<T extends ICommandType> {

    T parse(CommandContext context, String[] args, int index);

    List<String> autoComplete(CommandContext context, T type, int index);
}
