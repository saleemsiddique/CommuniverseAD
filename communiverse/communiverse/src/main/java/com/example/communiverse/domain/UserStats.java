package com.example.communiverse.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserStats {
    private static final int MAX_LEVEL = 30;
    private static final int MIN_LEVEL = 1;

    @NotNull
    private int level;
    @NotNull
    private int totalPoints;

    public UserStats() {
        this.level = 10;
        this.totalPoints = 0;
    }

    public void increasePoints(int points) {
        totalPoints += points;
        if (totalPoints >= pointsToNextLevel(level) && level < MAX_LEVEL) {
            totalPoints -= pointsToNextLevel(level);
            level++;
        }
    }

    public void decreasePoints(int points) {
        totalPoints -= points;
        if (totalPoints < 0 && level > MIN_LEVEL) {
            int difference = -totalPoints;
            totalPoints = pointsToBackLevel(level) - difference;
            level--;
        }
    }

    // Método para obtener la cantidad de puntos necesarios para alcanzar el siguiente nivel
    private int pointsToNextLevel(int currentLevel) {
        return 100 * currentLevel;
    }

    private int pointsToBackLevel(int currentLevel) {
        return 100 * (currentLevel - 1);
    }
}
