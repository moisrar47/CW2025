package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreTest {

    @Test
    void scoreStartsAtZero() {
        Score score = new Score();
        assertEquals(0, score.scoreProperty().get(), "New score should start at 0");
    }

    @Test
    void addIncreasesScore() {
        Score score = new Score();

        score.add(5);
        score.add(10);

        assertEquals(15, score.scoreProperty().get(),
            "Score should be the sum of all increments");
    }

    @Test
    void resetBringsScoreBackToZero() {
        Score score = new Score();
        score.add(20);

        score.reset();

        assertEquals(0, score.scoreProperty().get(),
            "Reset should bring score back to 0");
    }

    @Test
    void addZeroKeepsScoreUnchanged() {
        Score score = new Score();
        score.add(7);

        score.add(0);

        assertEquals(7, score.scoreProperty().get(),
            "Adding zero should not change the score");
    }
}
