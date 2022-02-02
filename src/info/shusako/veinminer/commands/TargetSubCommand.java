package info.shusako.veinminer.commands;

import info.shusako.veinminer.Veinminer;
import info.shusako.veinminer.commands.types.Target;

import java.util.ArrayList;
import java.util.List;

public class TargetSubCommand implements ISubCommand<Target> {
    @Override
    public Target parse(CommandContext context, String[] args, int index) {
        Target target = new Target(this);

        // get my

        if(args.length <= index) {
            context.failParse("Need to provide a target");
            return target;
        }

        String targetStr = args[index].toLowerCase();

        if (!targetStr.equals("my") && !targetStr.equals("server") && !targetStr.startsWith("player:")) {
            context.failParse("Invalid target: " + targetStr + ", must be one of: my, server, player:PLAYERNAME");
            return target;
        }

        if (!targetStr.equals("my") && !context.sender.isOp()) {
            context.failParse("You are not an operator, you may only change target: my");
            return target;
        }

        if (targetStr.equals("my")) {
            target.type = Target.TargetEnum.MY;
        } else if (targetStr.equals("server")) {
            target.type = Target.TargetEnum.SERVER;
        } else {
            target.playerName = args[index].substring(7);
            target.type = Target.TargetEnum.PLAYER;
        }
        return target;
    }

    @Override
    public List<String> autoComplete(CommandContext context, Target type, int index) {
        if(context.args.length - 1 != index) {
            return new ArrayList<>();
        }

        List<String> options = new ArrayList<>();
        options.add("my");
        options.add("player:<PLAYERNAME>");
        options.add("server");
        return options;
    }

}
