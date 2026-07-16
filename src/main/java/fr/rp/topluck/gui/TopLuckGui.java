package fr.rp.topluck.gui;

import fr.rp.topluck.TopLuckRP;
import fr.rp.topluck.managers.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopLuckGui {

    private final TopLuckRP plugin;
    public static final String TITRE = ChatColor.DARK_AQUA + "⛏ TopLuck - Joueurs en ligne";

    public TopLuckGui(TopLuckRP plugin) {
        this.plugin = plugin;
    }

    public void ouvrirMenu(Player staff) {
        Inventory inv = Bukkit.createInventory(null, 54, TITRE);

        int slot = 0;
        for (Player cible : Bukkit.getOnlinePlayers()) {
            if (slot >= 54) break;

            StatsManager.RapportJoueur rapport = plugin.getStatsManager().calculerRapport(cible.getUniqueId());

            ItemStack tete = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) tete.getItemMeta();
            meta.setOwningPlayer(cible);
            meta.setDisplayName(ChatColor.YELLOW + cible.getName());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Ratio general : " + couleurRatio(rapport.ratioGeneral) +
                    String.format("%.4f", rapport.ratioGeneral));
            lore.add(ChatColor.GRAY + "Minerais mines : " + ChatColor.AQUA + rapport.totalMinerais);
            lore.add(ChatColor.GRAY + "Blocs non-minerai : " + ChatColor.AQUA + rapport.totalNonMinerais);
            if (!rapport.donneesFiables) {
                lore.add(ChatColor.YELLOW + "⚠ Donnees encore limitees");
            }
            lore.add("");
            lore.add(ChatColor.YELLOW + "Clique pour le detail complet");
            meta.setLore(lore);

            tete.setItemMeta(meta);
            inv.setItem(slot, tete);
            slot++;
        }

        staff.openInventory(inv);
    }

    private ChatColor couleurRatio(double ratio) {
        if (ratio >= 0.15) return ChatColor.RED;
        if (ratio >= 0.08) return ChatColor.YELLOW;
        return ChatColor.GREEN;
    }
}
