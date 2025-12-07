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

***Hard Drop***
  * Instantly drops piece to the bottom where it lands
  * Instantly locks on landing
  * Special scoring for hard drops

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
| Class | Location | Description |
| :--- | :--- | :--- |
| `MainMenuController` | `controllers` | Manages the logic for the new main menu and game mode selection. |
| `StageManager` | `view` | Singleton class that handles scene switching and window properties (like fullscreen) centrally. |
| `GameModeManager` | `logic` | Encapsulates logic for different game rules (Zen, Sprint, Blitz, Pitfall), effectively separating rules from the board. |
| `SRSKickData` | `logic` | Contains the offset data tables required for the Super Rotation System (SRS) wall kicks. |
| `GhostBrickCalculator` | `logic` | Logic for calculating the y-coordinate of the ghost piece based on the current board state. |
| `GhostBrickRenderer` | `view` | Handles the specific UI rendering logic for the semi-transparent ghost brick. |
| `HighScore` | `data` | Singleton that manages loading and saving high scores/times to a properties file. |
| `GameMessage` | `view` | Handles displaying overlay messages (e.g., "Game Over", "New Record") to declutter the Controller. |
| `UILayoutManager` | `view` | Manages the responsive resizing and centering of UI elements when the window size changes. |
| `ColourMapper` | `view` | Extracted logic for mapping integer block IDs to JavaFX Paint colors. |

## Modified Java Classes
| Class | Changes Made | Rationale |
| :--- | :--- | :--- |
| `SimpleBoard` | Added `GameModeManager` integration, `holdCurrentBrick`, and SRS rotation calls. | To support new gameplay mechanics while keeping the board class focused on grid operations. |
| `GuiController` | Removed hardcoded game logic; delegates UI sizing to `UILayoutManager` and rendering to `GhostBrickRenderer`. | The original class was a "God Class". Refactoring satisfied the Single Responsibility Principle (SRP). |
| `GameController` | Updated to handle `InputEventListener` for new inputs (Hold, Hard Drop) and Game Mode logic. | To bridge the gap between the new UI inputs and the extended model capabilities. |
| `ViewData` | Added fields for `ghostPosition`, `heldBrick`, and `nextBricks`. | To transfer the necessary data for the new UI features from Model to View immutable. |
| `Brick` & subclasses | Updated matrix definitions for I, J, L, S, T, Z bricks. | To align with standard SRS initial rotation states. |
| `RandomBrickGenerator` | Updated to provide a list of future bricks (bag system). | Required for the "Next Pieces" preview feature. |
| `BrickRotator` | Added `getNextShapeClockwise` and `CounterClockwise`. | Necessary for SRS to test different rotation directions. |

## Unexpected Problems
1.  **SRS Complexity:** Implementing the Super Rotation System was significantly harder than expected. The "Wall Kick" data requires precise offset tables. I resolved this by creating a dedicated `SRSKickData` class to separate this data from the logic, preventing the `SimpleBoard` class from becoming unreadable.
2.  **JavaFX Layouts:** Making the game board responsive (resizing correctly when maximizing the window) was tricky because the original code using fixed coordinates. I solved this by creating the `UILayoutManager` to recalculate positions dynamically on window property change listeners.