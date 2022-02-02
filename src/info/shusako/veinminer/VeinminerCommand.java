package info.shusako.veinminer;

import com.sun.istack.internal.NotNull;
import info.shusako.veinminer.commands.ActionSubCommand;
import info.shusako.veinminer.commands.CommandContext;
import info.shusako.veinminer.commands.types.Action;
import info.shusako.veinminer.commands.types.ICommandType;
import info.shusako.veinminer.enums.ActivationMode;
import info.shusako.veinminer.enums.RadiusType;
import info.shusako.veinminer.enums.VeinSearchType;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Shusako on 12/14/2018.
 * For project Minecraft Veinminer Plugin, 2018
 */
public class VeinminerCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            CommandContext context = new CommandContext(sender, args);
            ICommandType type = context.parseSubCommand(ActionSubCommand.class, 0);
            type.execute(context);

            if(context.parseFailed) {
                sender.sendMessage(context.parseFailureReason);
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        CommandContext context = new CommandContext(sender, args);
        try {
            return context.autoComplete(ActionSubCommand.class, 0);
        } catch (Exception e) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add(e.getMessage());
            if(context.parseFailed) {
                strings.add(context.parseFailureReason);
            }
            return strings;
        }
    }
}
