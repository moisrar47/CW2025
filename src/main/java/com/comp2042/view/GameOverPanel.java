package com.comp2042.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Simple UI panel displayed when the game ends.
 * <p>
 * Shows a centered "GAME OVER" label styled via CSS.
 */
public class GameOverPanel extends BorderPane {

    /**
     * Creates a new {@code GameOverPanel} and initialises it with a
     * centered "GAME OVER" label using the {@code gameOverStyle} CSS class.
     */
    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }
    
}
