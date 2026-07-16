package fr.rp.topluck.managers;

import fr.rp.topluck.TopLuckRP;

import java.io.File;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final TopLuckRP plugin;
    private Connection connection;

    public DatabaseManager(TopLuckRP plugin) {
        this.plugin = plugin;
    }

    public void connecter() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "topluck.db");
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement st = connection.createStatement()) {
                st.execute("""
                    CREATE TABLE IF NOT EXISTS minage (
                        uuid TEXT NOT NULL,
                        pseudo TEXT NOT NULL,
                        materiau TEXT NOT NULL,
                        compte INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY (uuid, materiau)
                    )
                """);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Impossible de se connecter a la base de donnees", e);
        }
    }

    public void fermer() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erreur fermeture base de donnees", e);
        }
    }

    public void incrementer(UUID uuid, String pseudo, String materiau) {
        String sql = "INSERT INTO minage (uuid, pseudo, materiau, compte) VALUES (?, ?, ?, 1) " +
                "ON CONFLICT(uuid, materiau) DO UPDATE SET compte = compte + 1, pseudo = excluded.pseudo";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, pseudo);
            ps.setString(3, materiau);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erreur incrementer", e);
        }
    }

    /** Renvoie une map materiau -> compte pour un joueur donne */
    public Map<String, Integer> getStatsJoueur(UUID uuid) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT materiau, compte FROM minage WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) stats.put(rs.getString("materiau"), rs.getInt("compte"));
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erreur getStatsJoueur", e);
        }
        return stats;
    }

    public UUID getUuidParPseudo(String pseudo) {
        String sql = "SELECT uuid FROM minage WHERE pseudo = ? COLLATE NOCASE LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, pseudo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return UUID.fromString(rs.getString("uuid"));
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erreur getUuidParPseudo", e);
        }
        return null;
    }

    public void resetJoueur(UUID uuid) {
        String sql = "DELETE FROM minage WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Erreur resetJoueur", e);
        }
    }
}
