package fr.rp.topluck;

import fr.rp.topluck.commands.TopLuckCommand;
import fr.rp.topluck.gui.TopLuckGui;
import fr.rp.topluck.listeners.MiningListener;
import fr.rp.topluck.listeners.TopLuckGuiListener;
import fr.rp.topluck.managers.DatabaseManager;
import fr.rp.topluck.managers.StatsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TopLuckRP extends JavaPlugin {

    private DatabaseManager databaseManager;
    private StatsManager statsManager;
    private TopLuckGui topLuckGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        databaseManager.connecter();

        statsManager = new StatsManager(this);
        topLuckGui = new TopLuckGui(this);

        TopLuckCommand topLuckCommand = new TopLuckCommand(this);
        getCommand("topluck").setExecutor(topLuckCommand);

        getServer().getPluginManager().registerEvents(new MiningListener(this), this);
        getServer().getPluginManager().registerEvents(new TopLuckGuiListener(this, topLuckCommand), this);

        getLogger().info("TopLuckRP a ete active avec succes !");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.fermer();
        getLogger().info("TopLuckRP a ete desactive.");
    }

    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public TopLuckGui getTopLuckGui() { return topLuckGui; }
}
