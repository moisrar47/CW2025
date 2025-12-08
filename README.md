# COMP2042 Tetris Coursework – Moiz Israr Malik (ID 20705922)

This project is my maintenance and extension work for the COMP2042 CW2025 Tetris coursework.  
I started from the provided CW2025 codebase and focused on cleaning up the design (MVC-style structure, clearer responsibilities, and tests) and then adding gameplay and quality-of-life features such as a pause menu, ghost piece, hard drop, level progression, audio, and a persistent high-score system.

---

## GitHub

Public GitHub repository (fork of the original CW2025 repo):

- <https://github.com/moisrar47/CW2025>

---

## Compilation Instructions

### Requirements

- **Java:** JDK 23 (project also compiles with Java 21)
- **Maven:** 3.9.x
- **IDE:** IntelliJ IDEA (used for development and testing)

### Running in IntelliJ IDEA

1. **Clone the repository from GitHub:**

        git clone https://github.com/moisrar47/CW2025.git
        cd CW2025

2. **Open IntelliJ IDEA** and choose **“Open”**.  
   Select the project folder (the one containing `pom.xml`).  
   IntelliJ will automatically detect it as a Maven project.

3. **Ensure the Project SDK is JDK 23 (or Java 21).**  
   You can check/set this via:
    - *File → Project Structure → Project → SDK*

4. **Open the Maven tool window** in IntelliJ  
   (usually a vertical panel on the right side of the IDE).

5. In the Maven window, expand the project entry (e.g. `CW2025`) and navigate to:

        Plugins → javafx → javafx:run

6. **Double-click `javafx:run`** to start the game.

   This will:
    - Automatically download all required dependencies (including JavaFX)
    - Compile the project
    - Launch the game using the JavaFX Maven plugin
    - No manual VM arguments or module-path configuration is needed

---

## Implemented and Working Properly

### 1. Refactoring, Design & Organisation

**MVC-style structure and clearer packages**

- Introduced a clearer separation into packages under `src/main/java/com/comp2042`:
  - `app` – application entry point (`Main`).
  - `controller` – `GameController`, which coordinates the model and view.
  - `view` – GUI-related classes (`GuiController`, `GameOverPanel`, `NotificationPanel`).
  - `model` – core game state and logic (`Board`, `SimpleBoard`, `Score`, `MatrixOperations`, `ClearRow`, `LevelManager`, `HighScoreManager`, `HighScoreEntry`, `NextShapeInfo`, `ViewData`, `DownData`).
  - `logic.bricks` – tetromino classes and brick logic (`AbstractBrick`, `IBrick`, `JBrick`, `LBrick`, `OBrick`, `SBrick`, `TBrick`, `ZBrick`, `Brick`, `RandomBrickGenerator`, `BrickGenerator`).
  - `audio` – sound and music handling (`AudioManager`).
  - `input` – input event types and listener interfaces (`EventSource`, `EventType`, `InputEventListener`, `MoveEvent`).

**Moving classes into the correct layers**

- Moved GUI-heavy classes into `view`:
  - `GuiController`, `GameOverPanel`, and `NotificationPanel` into `com.comp2042.view`.
- Moved game state and logic classes into `model`:
  - `Board`, `SimpleBoard`, `Score`, `ClearRow`, `MatrixOperations`, `NextShapeInfo`, `DownData`, `ViewData`.
- Moved `GameController` into `com.comp2042.controller`.
- Introduced `com.comp2042.app.Main` as a clean entry point and updated the Maven `mainClass` accordingly.
- Grouped input-related classes into `com.comp2042.input`:
  - `EventSource`, `EventType`, `InputEventListener`, `MoveEvent`.

**Reducing magic numbers and improving clarity**

- Replaced hard-coded spawn coordinates in `SimpleBoard` with named constants.
- Replaced hard-coded board width/height in `GameController` with named constants, so changes to the playfield size are centralised.
- Cleaned up `Main` with constants and an FXML null check to avoid runtime errors when loading `gameLayout.fxml`.

**Better method structure and responsibilities**

- Extracted helper methods to remove duplication and clarify the flow:
  - `tryMoveBrick`-style helpers for brick movement logic.
  - `handleBrickLanded` helper for the logic that runs when a brick touches down in `GameController`.
  - Helper for updating brick position in `GuiController` instead of duplicating coordinate logic.
- Made suitable fields (like the board reference in `GameController`) `final` where possible to express immutability and intent.
- Tidied up and refactored `MatrixOperations`, fixing a potential “out of bounds” crash during row clearing.

**Level and audio responsibilities**

- Introduced **`LevelManager`** (in `com.comp2042.model`) to:
  - Handle level progression thresholds.
  - Control the fall speed based on the current level.
  - Expose simple methods to query current level and speed.
- Removed level-related fields and logic from `GuiController` and pushed them into `LevelManager`, improving single responsibility.
- Introduced **`AudioManager`** (in `com.comp2042.audio`) to:
  - Load and play background music (`bgm.mp3`).
  - Play sound effects (`line_clear.wav`, `move.wav`, `rotate.wav`, `hard_drop.wav`, `game_over.wav`, `level_up.wav`).
  - Handle pausing/resuming/stopping audio and mute behaviour.
- Moved audio concerns out of `GuiController` so the GUI is mainly responsible for UI and delegating to `AudioManager`.

**Brick design cleanup**

- Introduced **`AbstractBrick`** in `com.comp2042.logic.bricks` as a base class for all tetrominoes.
  - Stores rotation matrices and shared behaviour used by `IBrick`, `JBrick`, `LBrick`, `OBrick`, `SBrick`, `TBrick`, `ZBrick`, and `Brick`.
- Updated all brick types to reuse `AbstractBrick` for matrix storage instead of duplicating arrays and logic.
- Refined `RandomBrickGenerator` (and supporting `BrickGenerator`) to deduplicate random-selection logic.

---

### 2. Testing

Under `src/test/java`, I added JUnit tests (JUnit 5) to cover key parts of the model:

- **Matrix operations**
  - Tests for row clearing behaviour in `MatrixOperations` (e.g. when full lines are present and when they are not).
- **Scoring**
  - Tests for the `Score` class covering score increments for different line clears.
- **Board behaviour**
  - Tests for `SimpleBoard` `newGame` / reset behaviour to ensure the board is correctly cleared and state is reset.
- **Brick rotation**
  - Tests for `BrickRotator` to ensure rotations are valid and return the expected matrices.
- **Immutability**
  - A test to verify that `ClearRow` behaves immutably where appropriate.
- **LevelManager**
  - Tests to confirm level thresholds and fall-speed progression behave as expected and do not regress.

These tests were used throughout refactoring to ensure that structural changes (e.g. moving classes, changing spawn coordinates) did not break core game logic.

---

### 3. Gameplay Additions & Enhancements

**Playfield and layout**

- Initially expanded the playfield to use the full 360×640 window, then re-aligned it to be closer to a classic **10×20 Tetris grid**.
- Redesigned the playfield layout:
  - Improved grid visuals using the existing `gameLayout.fxml`.
  - Adjusted the positions of the score and HUD elements to match the new playfield.
  - Refined HUD placement on the right-hand side for a cleaner look.

**Controls**

- Added **hard drop** on the **Space** key:
  - Instantly drops the active piece to its landing row.
  - Triggers the appropriate sound effect (`hard_drop.wav`).
- Added **mouse and scroll controls**:
  - Mouse controls for horizontal movement and other interactions.
  - Scroll wheel input to rotate or move pieces (depending on configuration).
  - Ensured mouse controls stay aligned with the grid even after layout changes.

**Game feedback and visual helpers**

- Implemented a **ghost piece preview**:
  - Shows a faint outline of where the current piece will land if dropped.
  - Updates in real time as the player moves or rotates the piece.
- Added a **next-piece preview**:
  - Displays the upcoming tetromino in a separate panel on the right-hand HUD.
  - Uses `NextShapeInfo` and the new layout to draw the preview.

**Pause menu and overlays**

- Added a **pause menu overlay**:
  - Semi-transparent overlay with pause controls.
  - Buttons for resuming the game, restarting, or returning to the main menu (as applicable).
  - “Safe click” handling so accidental clicks outside the active area don’t cause odd behaviour.
  - Hover animations for buttons to give visual feedback.
- Improved the **game over** flow:
  - Cleaner transition into the game over screen.
  - Ensured mouse interactions are safe and don’t leak into the underlying game.

**Audio and music**

- Added background music (`bgm.mp3`) that plays during gameplay.
- Added sound effects:
  - `move.wav` – when moving pieces.
  - `rotate.wav` – when rotating pieces.
  - `line_clear.wav` – when clearing lines.
  - `hard_drop.wav` – when performing a hard drop.
  - `game_over.wav` – when the game ends.
  - `level_up.wav` – on level transitions.
- Integrated audio controls into the **pause menu**:
  - Buttons to toggle music and/or sound effects.
  - Fixed behaviour so the **background music correctly stops when pausing**, including after starting a new game.
  - Fixed issues where music would previously continue playing in the background unless the user toggled it manually.

**Level progression and speed**

- Implemented **level progression**:
  - The level increases as the player clears lines (thresholds handled by `LevelManager`).
  - Each new level increases the fall speed of the tetrominoes.
- HUD is **bound** to show the current level and other stats.
- Added a **level-up sound effect** and visual feedback when the player levels up.
- Tuned the **base fall speed** to feel fair at Level 1 and progressively more challenging.

**High scores and persistence**

- Added a **persistent high-score system**:
  - High scores are stored in `highscores.txt` in the project root.
  - `.gitignore` is configured so this file is not accidentally committed with local scores.
  - Scores are loaded on startup and updated on game over.
- Implemented a **game-over leaderboard**:
  - After a game ends, the best scores are shown to the player.
  - New high scores are inserted in the appropriate place.

**Visual resources**

- Kept assets under `src/main/resources`:
  - `sounds/` for all `.mp3` and `.wav` files.
  - `background_image.png` for the playfield background.
  - `digital.ttf` font for HUD styling.
  - `gameLayout.fxml` – JavaFX layout.
  - `window_style.css` – CSS styling.
- Ensured all resources are resolved via the classpath so the game runs from the Maven build.

---

## Implemented but Not Working Properly

Based on my testing, all of the above features are **working as expected**.  

I have not identified any major broken features. There may still be minor edge-case issues (for example, visual quirks if you pause/unpause very rapidly or resize the window), but in normal gameplay the following are all stable:

- Standard movement and rotation.
- Hard drop and ghost piece behaviour.
- Level progression and fall speed changes.
- Pause menu overlay and audio controls.
- High-score saving and loading.

There is one minor UX issue related to audio:

- If you start a **New Game from the pause menu**, the background music can continue playing while the pause overlay is visible. It can still be muted or lowered using the audio controls, and this behaviour does **not** occur when using **“Play Again”** from the game-over screen.


If any unexpected behaviour is encountered, it is unintentional and not known at the time of submission.

---

## Features Not Implemented

Some potential ideas that I considered but did not implement due to time:

- No additional special game modes (e.g. timed mode, endless marathon separate from the main game, or challenge variants).
- No power-ups or penalties beyond standard Tetris rules.
- No multi-piece preview (only the next piece is shown, not a queue of several).
- No online leaderboard or networked multiplayer.
- No configurable keybindings (controls are fixed to arrow keys + space + mouse).

---

## New Java Classes

These are the **new Java classes** I introduced for this assignment, with a brief description and package:

- `com.comp2042.audio.AudioManager`  
  Central class responsible for playing and stopping background music and sound effects.  
  Provides simple methods used by the GUI to trigger audio in response to game events and to handle pausing/muting.

- `com.comp2042.logic.bricks.AbstractBrick`  
  Base class for all tetromino types.  
  Stores common rotation matrices and behaviour so `IBrick`, `JBrick`, `LBrick`, `OBrick`, `SBrick`, `TBrick`, `ZBrick`, and `Brick` do not duplicate logic.

- `com.comp2042.model.LevelManager`  
  Encapsulates level progression rules and fall speeds.  
  Tracks current level, determines when to level up based on cleared lines, and provides the appropriate drop delay for the current level.

- `com.comp2042.model.HighScoreEntry`  
  Simple data class representing a single high-score entry (e.g. score and maybe timestamp/player name if extended).  
  Used by `HighScoreManager` to manage and display ordered scores.

- `com.comp2042.model.HighScoreManager`  
  Handles loading and saving high scores from `highscores.txt`.  
  Provides methods to insert a new score in order and to return the list for display on the game-over screen.

- JUnit test classes under `src/test/java`  
  New test classes were created to exercise:
  - `MatrixOperations` (row clearing).
  - `Score`.
  - `SimpleBoard` reset behaviour.
  - `BrickRotator` rotation logic.
  - `ClearRow` immutability.
  - `LevelManager` progression and speeds.

---

## Modified Java Classes

I modified several existing classes from the provided code base. Below is a summary of the main ones and why they changed.

- `com.comp2042.app.Main`
  - Moved into the `app` package as a clear entry point.
  - Added constants for configuration and an FXML null check for safer startup.
  - Updated `pom.xml` so `Main` is used by the JavaFX Maven plugin.

- `com.comp2042.controller.GameController`
  - Now sits in the `controller` package as part of an MVC separation.
  - Uses named constants instead of hard-coded board size.
  - Delegates level progression and speed values to `LevelManager`.
  - Uses extracted helpers such as `handleBrickLanded` and movement helpers to simplify `onDownEvent` and other event handlers.
  - Coordinates with `GuiController` and model classes in a cleaner way.

- `com.comp2042.view.GuiController`
  - Focuses on GUI responsibilities: updating HUD elements, responding to input, and calling into `GameController`, `AudioManager`, and `LevelManager`.
  - Uses helper methods and GUI constants instead of duplicating coordinate logic.
  - Handles:
    - Drawing the active piece, ghost piece, and next-piece preview.
    - Showing the pause menu overlay and dealing with safe click regions.
    - Displaying game-over information and the high-score leaderboard.
  - Had redundant level fields removed in favour of `LevelManager`.
  - Audio logic moved out to `AudioManager`, reducing class bloat.

- `com.comp2042.view.GameOverPanel` and `com.comp2042.view.NotificationPanel`
  - Moved into the `view` package.
  - Updated to work with the new HUD layout and high-score display where appropriate.
  - Small UI clean-ups to match the new design.

- `com.comp2042.model.Board` and `com.comp2042.model.SimpleBoard`
  - Moved into the `model` package.
  - Removed magic numbers for spawn positions; replaced with named constants.
  - Clarified game-over checks when spawning a new piece (used a named boolean to make the logic easier to read).
  - Integrated with ghost-piece calculations and hard-drop logic.
  - Works with `LevelManager` to respect current fall speed / tick rate.

- `com.comp2042.model.MatrixOperations`
  - Moved into `model`.
  - Cleaned up matrix manipulation and row-clearing logic.
  - Fixed a potential out-of-bounds crash.
  - Simplified the interface to be easier to test, and added unit tests.

- `com.comp2042.model.ClearRow`
  - Moved into `model`.
  - Adjusted to be more clearly immutable where possible.
  - Tested with a dedicated JUnit test for immutability.

- `com.comp2042.model.Score`
  - Moved into `model`.
  - Cleaned up scoring logic and added JUnit tests.
  - Integrates with `LevelManager` and line-clearing so that scoring is consistent.

- `com.comp2042.model.NextShapeInfo`, `com.comp2042.model.ViewData`, `com.comp2042.model.DownData`
  - Relocated to `model`.
  - Updated imports and usage in `SimpleBoard`, `BrickRotator`, and `GuiController`.
  - Extended to support HUD bindings (e.g. next-piece preview).

- `com.comp2042.logic.bricks.RandomBrickGenerator`
  - Refactored to delegate creation logic to `BrickGenerator`.
  - Simplified random selection and removed duplicated code.

- `com.comp2042.input.EventSource`, `EventType`, `InputEventListener`, `MoveEvent`
  - Grouped into `com.comp2042.input` for clarity.
  - Used by `GuiController` and `GameController` to manage input cleanly.

Overall, the goal was to respect single responsibility wherever reasonable, reduce duplication, and make the code easier to extend.

---

## Unexpected Problems

I encountered several unexpected challenges while working on this coursework. Below are the main ones and how I addressed them.

- **Java / Maven / JavaFX setup**
  - Initially struggled to get JavaFX to run cleanly with Maven and my JDK version.
  - Fixed this by:
    - Installing Temurin OpenJDK 23 and configuring it as the project SDK.
    - Adjusting `pom.xml` to use the JavaFX Maven plugin with the correct `mainClass`.
    - Verifying that `mvn clean compile` and `mvn javafx:run` work from the command line.

- **Out-of-bounds and stability issues**
  - While testing line clears and different board sizes, I encountered a potential out-of-bounds problem in `MatrixOperations`.
  - Refactored the row-clearing logic to guard index access and added targeted JUnit tests so this would not silently reappear.

- **Background music and pause behaviour**
  - At one point, the background music would continue playing when:
    - Pausing the game.
    - Starting a new game.
  - The fix was to introduce `AudioManager` and centralise all audio control logic, then ensure the pause and new-game flows explicitly pause or stop music.
  - Also fixed a bug where pausing a *new* game sometimes behaved differently from pausing during ongoing play.

- **Playfield size vs. mouse controls**
  - After changing the playfield to use the full 360×640 window and later re-aligning it to a classic 10×20 grid, the mouse interactions temporarily became misaligned.
  - Solved by:
    - Introducing board and cell-size constants.
    - Deriving mouse-to-grid calculations from those constants rather than from hard-coded pixel values.
    - Doing manual testing to check all columns and rows match the expected positions.

- **Balancing level progression**
  - Making level progression feel fair but challenging required some iteration.
  - Tweaked the thresholds and speed multipliers in `LevelManager` until gameplay felt responsive and not too punishing.
  - The final version feels close to a classic Tetris difficulty ramp.

- **High-score persistence**
  - Needed to ensure `highscores.txt`:
    - Is created if it doesn’t exist.
    - Can be loaded on startup without crashing.
    - Is not accidentally committed with my personal scores.
  - Addressed this by:
    - Handling missing-file cases in `HighScoreManager`.
    - Adding `highscores.txt` to `.gitignore`.
    - Testing the game after deleting the file to confirm that it re-creates cleanly.

---
