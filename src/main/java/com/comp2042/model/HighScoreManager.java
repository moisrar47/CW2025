package com.comp2042.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HighScoreManager {

    private final Path storageFile;
    private final List<HighScoreEntry> entries = new ArrayList<>();

    public HighScoreManager(String filename) {
        this.storageFile = Paths.get(filename);
        load();
    }

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

    public Optional<HighScoreEntry> getHighest() {
        return entries.stream()
            .max(Comparator.comparingInt(HighScoreEntry::getScore));
    }

    public boolean isNewHighScore(int score) {
        Optional<HighScoreEntry> best = getHighest();
        return best.map(entry -> score > entry.getScore()).orElse(true);
    }

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

    public List<HighScoreEntry> getEntries() {
        return new ArrayList<>(entries); // return a copy so callers can't modify internal list
    }


    private void sortAndSave() {
        entries.sort(Comparator.comparingInt(HighScoreEntry::getScore).reversed());
        save();
    }
}
