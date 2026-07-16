package fr.rp.topluck.listeners;

import fr.rp.topluck.TopLuckRP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningListener implements Listener {

    private final TopLuckRP plugin;

    public MiningListener(TopLuckRP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        String materiau = event.getBlock().getType().name();
        var minerais = plugin.getStatsManager().getMinerais();
        var nonMinerais = plugin.getStatsManager().getNonMinerais();

        if (!minerais.contains(materiau) && !nonMinerais.contains(materiau)) return;

        plugin.getDatabaseManager().incrementer(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getName(),
                materiau
        );
    }
}
