package com.example.marshmallowgame1;

// Pelin päätilat
public enum GameState {
    EMPTY,          // aloitustila
    FIRE_BUILT,     // nuotio rakennettu
    FIRE_LIT,       // tuli sytytetty
    STICK_TAKEN,    // tikku otettu
    COOKING,        // vaahtokarkkia paahdetaan
    DONE            // valmis
}