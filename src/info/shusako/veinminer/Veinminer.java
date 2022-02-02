package info.shusako.veinminer;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Shusako on 12/13/2018.
 * For project Minecraft Veinminer Plugin, 2018
 */
public class Veinminer extends JavaPlugin {

    public static Veinminer instance;
    public static MinerListener minerListener;
    public static VeinminerCommand veinminerCommand;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyDefaults(true);
        saveConfig();

        minerListener = new MinerListener();
        getServer().getPluginManager().registerEvents(minerListener, this);

        veinminerCommand = new VeinminerCommand();
        getCommand("veinminer").setExecutor(veinminerCommand);
        getCommand("veinminer").setTabCompleter(veinminerCommand);

        super.onEnable();
    }


}
