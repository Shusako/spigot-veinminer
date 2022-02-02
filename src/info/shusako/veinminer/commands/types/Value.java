package info.shusako.veinminer.commands.types;

import info.shusako.veinminer.commands.CommandContext;
import info.shusako.veinminer.commands.ISubCommand;

public class Value implements ICommandType {

    private ISubCommand<Value> command;
    public Object value;


    public Value(ISubCommand<Value> command) {
        this.command = command;
    }

    @Override
    public ISubCommand<Value> getCommand() {
        return command;
    }

    @Override
    public boolean isValid() {
        return (value != null);
    }

    @Override
    public void execute(CommandContext context) {

    }
}
