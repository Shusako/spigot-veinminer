package info.shusako.veinminer.commands;

import info.shusako.veinminer.Utils;
import info.shusako.veinminer.commands.types.Setting;
import info.shusako.veinminer.commands.types.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SettingSubCommandTest {

    ISubCommand<Setting> settingCommand;

    @BeforeEach
    void init() {
        settingCommand = new SettingSubCommand();
    }

    @Test
    void test() {
        assertEquals(true, Utils.getValueFromObject(Boolean.class, "TRUE"));
    }

    @Test
    void mySettings() {
//        CommandContext context = CommandContextFactory.buildOperatorContext();
//        context.addContext(new Target(Target.TargetEnum.MY));
//
//        Setting test = context.invokeSubCommand(SettingSubCommand.class, 2);
//        Setting setting = settingCommand.parse(context, new String[]{"get", "my", "max_blocks"}, 2);
//        assertEquals("max_blocks", setting.configPath);
    }

    @Test
    void playerSettings() {
//        CommandContext context = CommandContextFactory.buildOperatorContext();
//        context.addContext(new Target("edgecw"));
//
//        Setting test = context.invokeSubCommand(SettingSubCommand.class, 2);
//        Setting setting = settingCommand.parse(context, new String[]{"get", "player:edgecw", "max_blocks"}, 2);
//        assertEquals("max_blocks", setting.configPath);
    }

    @Test
    void serverSettings() {

    }
}