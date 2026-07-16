package fr.rp.topluck.managers;

import fr.rp.topluck.TopLuckRP;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StatsManager {

    private final TopLuckRP plugin;

    public StatsManager(TopLuckRP plugin) {
        this.plugin = plugin;
    }

    public Set<String> getMinerais() {
        return Set.copyOf(plugin.getConfig().getStringList("minerais"));
    }

    public Set<String> getNonMinerais() {
        return Set.copyOf(plugin.getConfig().getStringList("non-minerais"));
    }

    public int getSeuilMinimum() {
        return plugin.getConfig().getInt("seuil-minimum-blocs", 100);
    }

    public static class RapportJoueur {
        public int totalMinerais;
        public int totalNonMinerais;
        public double ratioGeneral; // minerais / non-minerais
        public Map<String, Integer> detailMinerais = new LinkedHashMap<>();
        public Map<String, Double> ratioParMinerai = new LinkedHashMap<>(); // minerai specifique / non-minerais
        public boolean donneesFiables;
    }

    public RapportJoueur calculerRapport(UUID uuid) {
        Map<String, Integer> stats = plugin.getDatabaseManager().getStatsJoueur(uuid);
        Set<String> minerais = getMinerais();
        Set<String> nonMinerais = getNonMinerais();

        RapportJoueur rapport = new RapportJoueur();

        for (Map.Entry<String, Integer> entree : stats.entrySet()) {
            String materiau = entree.getKey();
            int compte = entree.getValue();

            if (minerais.contains(materiau)) {
                rapport.totalMinerais += compte;
                rapport.detailMinerais.put(materiau, compte);
            } else if (nonMinerais.contains(materiau)) {
                rapport.totalNonMinerais += compte;
            }
        }

        rapport.donneesFiables = rapport.totalNonMinerais >= getSeuilMinimum();
        rapport.ratioGeneral = rapport.totalNonMinerais == 0 ? 0 :
                (double) rapport.totalMinerais / rapport.totalNonMinerais;

        for (Map.Entry<String, Integer> entree : rapport.detailMinerais.entrySet()) {
            double ratio = rapport.totalNonMinerais == 0 ? 0 : (double) entree.getValue() / rapport.totalNonMinerais;
            rapport.ratioParMinerai.put(entree.getKey(), ratio);
        }

        return rapport;
    }
}
