package info.shusako.veinminer.commands;

import info.shusako.veinminer.commands.types.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TargetSubCommandTest {

    ISubCommand<Target> targetCommand;

    @BeforeEach
    void init() {
        targetCommand = new TargetSubCommand();
    }

    @Test
    void myTargetAsOperator() {
        Target target = targetCommand.parse(CommandContextFactory.buildOperatorContext(), new String[]{"my"}, 0);
        assertSame(target.type, Target.TargetEnum.MY);
    }

    @Test
    void myTargetAsPlayer() {
        Target target = targetCommand.parse(CommandContextFactory.buildPlayerContext(), new String[]{"my"}, 0);
        assertSame(target.type, Target.TargetEnum.MY);
    }

    @Test
    void serverTargetAsOperator() {
        Target target = targetCommand.parse(CommandContextFactory.buildOperatorContext(), new String[]{"server"}, 0);
        assertSame(target.type, Target.TargetEnum.SERVER);
    }

    @Test
    void serverTargetAsPlayer() {
        try {
            Target target = targetCommand.parse(CommandContextFactory.buildPlayerContext(), new String[]{"server"}, 0);
            fail();
        } catch (Exception e) {
            // pass
        }
    }

    @Test
    void playerTargetAsOperator() {
        Target target =
                targetCommand.parse(CommandContextFactory.buildOperatorContext(), new String[]{"player:Shusako"}, 0);
        assertSame(target.type, Target.TargetEnum.PLAYER);
        assertEquals("Shusako", target.playerName);
    }

    @Test
    void playerTargetAsPlayer() {
        try {
            Target target =
                    targetCommand.parse(CommandContextFactory.buildPlayerContext(), new String[]{"player:Shusako"}, 0);
            fail();
        } catch (Exception e) {
            // pass
        }
    }
}