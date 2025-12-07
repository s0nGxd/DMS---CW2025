# COMP2042 Coursework - Tetris Refactoring & Extension
## GitHub Repository: https://github.com/s0nGxd/DMS---CW2025

## Compilation Instructions
This project is a Maven-based JavaFX application. Ensure you have Maven and JDK 21 (or higher) installed.

1.  **Open Terminal/Command Prompt** and navigate to the project root directory (where `pom.xml` is located).
2.  **Clean and Compile**:
    ```bash
    mvn clean javafx:run
    ```
    *Alternatively, if using the Maven wrapper included:*
    * Windows: `.\mvnw clean javafx:run`
    * Mac/Linux: `./mvnw clean javafx:run`
3.  **Build Executable (Optional)**:
    ```bash
    mvn package
    ```

## Implemented and Working Properly
The following features were successfully implemented and tested to be working as intended:

***Super Rotation System (SRS)***
  * Official Tetris wall-kick implemetation
  * Correct clockwise and counter-clockwise Rotation
  * Wall-kicks allows bricks to rotate at the side walls
  * Allows advance rotations like: T-Spins, L-Spins, J-Spins, etc.

***Ghost Brick***
  * Shows where the current falling brick will land
  * Calculated using GhostBrickCalculator class
  * Rendered as semi-transparent (30% Opacity) with GhostBrickRenderer

***Next Bricks Preview (4 Bricks)***
  * Shows the next 4 upcoming bricks
  * Rendered with NextPanelRenderer class

***Hold Brick***
  * Added the ability to hold a brick for later use
  * Handles by swapping the current brick with the one holding
  * Initiate hold by pressing the "C" key

***Hard Drop***
  * Instantly drops piece to the bottom where it lands
  * Instantly locks on landing
  * Special scoring for hard drops

***7 Bag Random Generator***
  * Uses the official tetris random brick generator system
  * Random bricks are drawn from the bag randomly until all 7 are gone then refill
  * Prevent long droughts for any single piece

***Delay System***
  * Added a boolean to delay the brick from merging to the background immediately
  * Allow for more rotational and movement plays
  * Bricks can still move upon touching the ground until the next drop tick

***4 Unique Game Modes***
  * All handled through GameModeManager class
  * **Zen** - Endless casual play
  * **Sprint (40 Lines)** - Race against the clock to finish 40 lines as fast as possible
  * **Blitz (3 minutes)** - Score as many points as possible in 3 minutes
  * **Pitfall (Level)** - Bricks falling speed increases through game level progress

***High Score System***
  * High score is stored locally and accessed everytime entering a game mode
  * Store highscore / best time per game mode
  * Automatically loads and update on game start / finish

***Main Menu***
  * An entry menu to lead to selectable game modes
  * Controlled through MainMenuController
  * Transition between game modes managed by StageManager

***Fullscreen Support***
  * Toggles in and out of fullscreen via F11 key

***Responsive Dynamic UI***
  * UI elements repositions automatically base on windows size 
  * Works with the game windowed, maximized and fullscreen
  * All layout handling delegated to UILayoutManager

## Implemented but Not Working Properly
* **None observed.** The core gameplay loop, new mechanics (SRS, Ghost), and UI transitions appear to function as intended without crashing

## Features Not Implemented
* **Audio/Sound Effects:** prioritise core functionality and refactoring due to time contstrain
* **Online Leaderboards:** implemented core game modes due to time contstrain. High scores are currently local-only

## New Java Classes
| Class | Package | Description |
| :--- | :--- | :--- |
| `MainMenuController` | `controllers` | Manages the entry point UI and game mode selection logic. |
| `InputHandler` | `controllers` | Decouples input handling from the Controller, mapping key events to game actions. |
| `StageManager` | `view` | **Singleton Pattern.** Centralizes scene navigation and window property management (Fullscreen/Resizing). |
| `UILayoutManager` | `view` | Handles the mathematical logic required to center the game board and panels dynamically when the window resizes. |
| `GameModeManager` | `logic` | **Strategy-like Pattern.** Encapsulates rules for different modes (Zen, Sprint, etc.), separating game rules from the board grid. |
| `SRSKickData` | `logic` | A data-holder class containing the static offset tables required for standard Tetris wall kicks. |
| `GhostBrickCalculator` | `logic` | Specialized logic to calculate the `y` coordinate of the ghost piece without modifying the actual board state. |
| `GameConstants` | `constant` | Centralized file for magic numbers (Board size, brick size, speeds) to improve maintainability. |
| `HighScore` | `data` | **Singleton Pattern.** Manages loading and saving persistent data to `highscores.properties`. |
| `BrickPanelRenderer` | `render` | **SRP.** Extracted from `GuiController`; handles drawing the active falling brick. |
| `BoardRenderer` | `render` | **SRP.** Extracted from `GuiController`; handles drawing the static game board. |
| `GhostBrickRenderer` | `render` | **SRP.** Specialized renderer for the semi-transparent ghost brick. |
| `NextPanelRenderer` | `render` | **SRP.** Handles drawing the "Next" and "Hold" UI components. |
| `ColourMapper` | `view` | Maps integer block IDs to JavaFX Colors, removing view dependency from the Model. |

## Modified Java Classes
The original code (by kooitt) was heavily refactored to adhere to the **Single Responsibility Principle (SRP)** and improve modularity.

| Class | Refactoring / Modification | Rationale |
| :--- | :--- | :--- |
| `GuiController` | **Major Refactor.** Stripped of all rendering, input, and layout logic. It now acts as a true controller, delegating tasks to `Renderer` classes and `InputHandler`. | The original class was a "God Class" (Antipattern). Splitting it makes the code testable, readable, and maintainable. |
| `SimpleBoard` | Added `holdCurrentBrick`, `rotateLeft/Right` (SRS), and integration with `GameModeManager`. | Expanded the model to support modern Tetris mechanics while keeping the grid logic isolated from rule logic. |
| `GameController` | Updated to implement `InputEventListener` for new inputs (Hold, Hard Drop) and handle Game Mode end-conditions. | Needed to bridge the gap between the new UI inputs and the extended logic layer. |
| `ViewData` | Converted to an Immutable Data Transfer Object. Added fields for `ghostPosition`, `heldBrick`, and `nextBricks`. | To safely pass the state of the new features (Ghost, Hold) from Model to View without exposing internal logic. |
| `Brick` (and subclasses) | Matrices updated to start in the standard SRS "Spawn" orientation. | Old matrices used arbitrary starting rotations; SRS requires specific initial states for wall kicks to work correctly. |
| `BrickRotator` | Added `Clockwise` and `CounterClockwise` methods. | SRS requires distinct logic for left vs. right rotation to calculate the correct kick offset from `SRSKickData`. |
| `RandomBrickGenerator` | Updated to generate a queue of future bricks rather than a single next brick. | Required to support the new "Next Pieces" preview panel showing 4 upcoming blocks. |

## Unexpected Problems & Solutions
1.  **SRS Wall Kick Logic:** Implementing the Super Rotation System was complex. The standard rotation formulas often resulted in blocks rotating "inside" walls or overlapping other blocks.
- *Solution:* I implemented `SRSKickData` which holds lookup tables for every possible rotation state transition (e.g., 0->1, 1->0). The `SimpleBoard` now iterates through these test cases until a valid position is found, falling back to "no rotation" if all kicks fail.
2.  **JavaFX Layout Responsiveness:** The original code used hardcoded `LayoutX/Y` coordinates. When adding a Main Menu and Fullscreen support, the game board would drift off-center or crop incorrectly.
- *Solution:* I created `UILayoutManager`. It listens to the `Stage` width/height properties and mathematically recalculates the center position of the `GridPane`, ensuring the game stays centered at any resolution.
3.  **God Class Decomposition:** `GuiController` was initially handling logic, view, and input, making it difficult to add new features like the Hold queue.
- *Solution:* I applied the Single Responsibility Principle by extracting rendering logic into specific classes (`BrickPanelRenderer`, `GhostBrickRenderer`, etc.) and moving input handling to `InputHandler`. This made adding the "Hold" feature much easier as I only had to modify the specific renderer and input map.
4.  **Game Transition:** The original transition between the main menu and game modes causes bugs and glitches between transition
- *Solution:* I added the `Stage Manager` class to handle transition and rearranged the UI spawning sequence to ensure that the initial UI spawn transition is smooth without any weird behaviour