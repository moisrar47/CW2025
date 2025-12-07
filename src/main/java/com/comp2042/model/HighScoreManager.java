package com.comp2042.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Manages loading, storing and updating high-score entries for the game.
 * <p>
 * High scores are persisted to a text file, with one entry per line in the
 * format {@code name;score}. The manager provides helper methods to query
 * the best score and record new results.
 */
public class HighScoreManager {

    /**
     * Path to the file where high scores are stored.
     */
    private final Path storageFile;

    /**
     * In-memory list of all high-score entries currently loaded.
     */
    private final List<HighScoreEntry> entries = new ArrayList<>();

    /**
     * Creates a new {@code HighScoreManager} for the given file and
     * immediately attempts to load existing scores from it.
     *
     * @param filename the path to the high-score file
     */
    public HighScoreManager(String filename) {
        this.storageFile = Paths.get(filename);
        load();
    }

    /**
     * Loads high-score entries from the backing file into memory.
     * <p>
     * If the file does not exist, the method returns without error and the
     * in-memory list remains empty.
     */
    public void load() {
        entries.clear();

        if (!Files.exists(storageFile)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(storageFile);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    entries.add(new HighScoreEntry(name, score));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load high scores: " + e.getMessage());
        }
    }

    /**
     * Writes the current in-memory list of high-score entries to the backing file.
     * <p>
     * Each entry is written as {@code name;score} on its own line.
     */
    public void save() {
        List<String> out = new ArrayList<>();
        for (HighScoreEntry entry : entries) {
            out.add(entry.getPlayerName() + ";" + entry.getScore());
        }

        try {
            Files.write(storageFile, out);
        } catch (IOException e) {
            System.err.println("Could not save high scores: " + e.getMessage());
        }
    }

    /**
     * Returns the highest-scoring entry currently in memory.
     *
     * @return an {@link Optional} containing the best {@link HighScoreEntry},
     *         or empty if there are no entries
     */
    public Optional<HighScoreEntry> getHighest() {
        return entries.stream()
            .max(Comparator.comparingInt(HighScoreEntry::getScore));
    }

    /**
     * Determines whether the given score would count as a new high score.
     *
     * @param score the score to test
     * @return {@code true} if there are no existing entries or the score is
     *         strictly greater than the current highest score; {@code false} otherwise
     */
    public boolean isNewHighScore(int score) {
        Optional<HighScoreEntry> best = getHighest();
        return best.map(entry -> score > entry.getScore()).orElse(true);
    }

    /**
     * Records a high score for the given player name.
     * <p>
     * If an entry already exists for the same name (case-insensitive), it will
     * only be updated if the new score is higher. Otherwise, a new entry is
     * added. After updating the list, the scores are sorted and saved.
     *
     * @param name  the player's name
     * @param score the score to record
     */
    public void recordHighScore(String name, int score) {
        // update if same name exists
        for (int i = 0; i < entries.size(); i++) {
            HighScoreEntry e = entries.get(i);
            if (e.getPlayerName().equalsIgnoreCase(name)) {
                if (score > e.getScore()) {
                    entries.set(i, new HighScoreEntry(name, score));
                }
                sortAndSave();
                return;
            }
        }

        // otherwise add new entry
        entries.add(new HighScoreEntry(name, score));
        sortAndSave();
    }

    /**
     * Returns a copy of the current list of high-score entries.
     * <p>
     * The returned list can be modified by callers without affecting the
     * internal state of the manager.
     *
     * @return a new {@link List} containing all high-score entries
     */
    public List<HighScoreEntry> getEntries() {
        return new ArrayList<>(entries); // return a copy so callers can't modify internal list
    }

    /**
     * Sorts the in-memory entries by score in descending order and persists
     * them to the backing file.
     */
    private void sortAndSave() {
        entries.sort(Comparator.comparingInt(HighScoreEntry::getScore).reversed());
        save();
    }
}
