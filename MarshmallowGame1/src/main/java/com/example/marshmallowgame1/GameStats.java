package com.example.marshmallowgame1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Vastaa pelin sisäisestä tilastosta.
 * Lukee ja kirjoittaa tilaston tekstitiedostoon.
 */
public class GameStats {

    private static final Path FILE = Path.of("stats.txt");
    private int marshmallowsCooked = 0;

    public GameStats() {
        loadFromFile();
    }

    public void incrementMarshmallows() {
        marshmallowsCooked++;
        saveToFile();
    }

    public int getMarshmallowsCooked() {
        return marshmallowsCooked;
    }

    private void loadFromFile() {
        try {
            if (Files.exists(FILE)) {
                String content = Files.readString(FILE).trim();
                marshmallowsCooked = Integer.parseInt(content);
            }
        } catch (Exception e) {
            marshmallowsCooked = 0;
        }
    }

    private void saveToFile() {
        try {
            Files.writeString(FILE, String.valueOf(marshmallowsCooked));
        } catch (IOException e) {
            System.out.println("Tilaston tallennus epäonnistui");
        }
    }

    public String getStatusText() {
        int code = 1 + marshmallowsCooked - 1;

        if (marshmallowsCooked == 1) {
            return "Status: " + code + " Delicious";
        } else if (marshmallowsCooked == 2) {
            return "Status: " + code + " Still tasty";
        } else if (marshmallowsCooked == 3) {
            return "Status: " + code + " Wow you are hungry";
        } else {
            return "Status: " + code + " Too much sugar darling";
        }
    }
}