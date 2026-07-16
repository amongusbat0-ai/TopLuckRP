package fr.rp.topluck.commands;

import fr.rp.topluck.TopLuckRP;
import fr.rp.topluck.managers.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class TopLuckCommand implements CommandExecutor {

    private final TopLuckRP plugin;

    public TopLuckCommand(TopLuckRP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("topluckrp.staff")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }
        if (args.length < 1) {
            if (sender instanceof org.bukkit.entity.Player staff) {
                plugin.getTopLuckGui().ouvrirMenu(staff);
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /topluck <joueur>");
            }
            return true;
        }

        UUID uuid = plugin.getDatabaseManager().getUuidParPseudo(args[0]);
        if (uuid == null) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
            if (offline.hasPlayedBefore()) uuid = offline.getUniqueId();
        }
        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "Aucune donnee de minage trouvee pour ce joueur.");
            return true;
        }

        afficherRapport(sender, args[0], uuid);
        return true;
    }

    public void afficherRapport(CommandSender sender, String pseudo, UUID uuid) {
        StatsManager.RapportJoueur rapport = plugin.getStatsManager().calculerRapport(uuid);

        sender.sendMessage(ChatColor.GOLD + "=== Ratio de minage de " + pseudo + " ===");
        if (!rapport.donneesFiables) {
            sender.sendMessage(ChatColor.YELLOW + "⚠ Donnees peu fiables : moins de " +
                    plugin.getStatsManager().getSeuilMinimum() + " blocs non-minerai mines (" +
                    rapport.totalNonMinerais + " actuellement).");
        }
        sender.sendMessage(ChatColor.WHITE + "Total minerais mines : " + ChatColor.AQUA + rapport.totalMinerais);
        sender.sendMessage(ChatColor.WHITE + "Total blocs non-minerai mines : " + ChatColor.AQUA + rapport.totalNonMinerais);
        sender.sendMessage(ChatColor.WHITE + "Ratio general (minerais/non-minerais) : " +
                couleurRatio(rapport.ratioGeneral) + String.format("%.4f", rapport.ratioGeneral) +
                ChatColor.GRAY + " (" + String.format("%.2f", rapport.ratioGeneral * 100) + "%)");

        if (!rapport.detailMinerais.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "--- Detail par minerai ---");
            for (Map.Entry<String, Integer> entree : rapport.detailMinerais.entrySet()) {
                double ratio = rapport.ratioParMinerai.get(entree.getKey());
                sender.sendMessage(ChatColor.WHITE + formaterNom(entree.getKey()) + " : " +
                        ChatColor.AQUA + entree.getValue() + ChatColor.GRAY + " (ratio " +
                        couleurRatio(ratio) + String.format("%.4f", ratio) + ChatColor.GRAY + ")");
            }
        }
    }

    private ChatColor couleurRatio(double ratio) {
        if (ratio >= 0.15) return ChatColor.RED;
        if (ratio >= 0.08) return ChatColor.YELLOW;
        return ChatColor.GREEN;
    }

    private String formaterNom(String materiau) {
        String[] mots = materiau.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String mot : mots) {
            sb.append(Character.toUpperCase(mot.charAt(0))).append(mot.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
