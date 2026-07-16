package fr.rp.topluck.listeners;

import fr.rp.topluck.TopLuckRP;
import fr.rp.topluck.commands.TopLuckCommand;
import fr.rp.topluck.gui.TopLuckGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class TopLuckGuiListener implements Listener {

    private final TopLuckRP plugin;
    private final TopLuckCommand topLuckCommand;

    public TopLuckGuiListener(TopLuckRP plugin, TopLuckCommand topLuckCommand) {
        this.plugin = plugin;
        this.topLuckCommand = topLuckCommand;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(TopLuckGui.TITRE)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player staff)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() != Material.PLAYER_HEAD) return;
        if (!(item.getItemMeta() instanceof SkullMeta meta) || meta.getOwningPlayer() == null) return;

        staff.closeInventory();
        topLuckCommand.afficherRapport(staff, meta.getOwningPlayer().getName(), meta.getOwningPlayer().getUniqueId());
    }
}
