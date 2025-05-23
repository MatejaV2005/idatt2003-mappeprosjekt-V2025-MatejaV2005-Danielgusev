# idatt2003-mappeprosjekt-V2025-MatejaV2005-danielgusev
# The Pimp Playground: Board Game Collection

## Project Description
This project is a JavaFX-based desktop application developed for the IDATT2003 Programming 2 course. It allows users to play various board games, primarily focusing on "Snakes & Ladders" with different difficulty levels and a custom space-themed race game called "Astro Rally". The application features a graphical user interface for game selection, player setup, and gameplay.

## Functionalities
The application supports the following key features:

* **Game Selection:**
    * Play "Snakes & Ladders" with predefined boards (Easy, Normal, Hard).
    * Play "Astro Rally", a custom loop-based race game.
* **Custom Boards (Snakes & Ladders):**
    * Load custom "Snakes & Ladders" board layouts from JSON files.
* **Player Management:**
    * Create new players with custom names and selectable icons.
    * Save player profiles to a CSV file for later use.
    * Load existing player profiles from a CSV file.
    * Support for 2-4 players per game session.
* **Gameplay:**
    * Interactive graphical game board.
    * Dice rolling mechanism (typically two 6-sided dice).
    * Automatic handling of tile actions (e.g., moving up ladders, sliding down snakes, skip turns, boost pads, asteroid fields).
    * Visual feedback for player turns, dice rolls, and game events.
    * Clear indication of the game winner.
* **User Interface:**
    * Intuitive navigation between title screen, game selection, game setup, and the game board.
    * Visual components for managing players, selecting game difficulty/boards, and viewing game information.

## Prerequisites
* Java Development Kit (JDK) 21 or higher (as per `pom.xml` and course requirements).
* Apache Maven 3.6.0 or higher.

## How to Run the Project
1.  **Clone the Repository and navigate to it:**
    ```bash
    git clone https://github.com/MatejaV2005/idatt2003-mappeprosjekt-V2025-MatejaV2005-Danielgusev.git
    cd idatt2003-mappeprosjekt-V2025-MatejaV2005-Danielgusev
    ```
2.  **Using Maven (Recommended):**
    Open a terminal in the root directory of the project and run:
    ```bash
    mvn clean compile javafx:run
    ```
    The main class configured in `pom.xml` should be `edu.ntnu.idi.idatt.Main` or `edu.ntnu.idi.idatt.MainApplication`.

3.  **Using an IDE (e.g., IntelliJ IDEA, Eclipse, VSCode):**
    * Import the project as a Maven project.
    * Ensure your IDE is configured to use JDK 21.
    * Locate and run the main class: `edu.ntnu.idi.idatt.Main.java` (which in turn calls `MainApplication.main`).
    * You can also automatically run it through the terminal with the command "mvn javafx:run"

## File Structure & Data
* **Player Data:** Saved players are stored in `Files/Players/players.csv`.
* **Custom Boards:** Custom board layouts for Snakes & Ladders are loaded from JSON files. These should be placed in a directory like `Files/Boards/`.

### Example of a Valid JSON Board File (`.json`)
A custom board file defines the number of rows, columns, and the properties of each tile, including any actions.

```json
{
  "tiles": {
    "1": {
      "id": 1,
      "row": 0,
      "column": 0,
      "nextTileId": 2,
      "action": {
        "actionType": "NoOp",
        "destinationTileId": -1,
        "description": "No operation performed"
      }
    },
    "2": {
      "id": 2,
      "row": 0,
      "column": 1,
      "nextTileId": 3,
      "action": {
        "actionType": "NoOp",
        "destinationTileId": -1,
        "description": "No operation performed"
      }
    },
    "3": {
      "id": 3,
      "row": 0,
      "column": 2,
      "nextTileId": 4,
      "action": {
        "actionType": "Ladder",
        "destinationTileId": 13,
        "description": "climbs a short ladder to tile 13"
      }
    },
    // ... more tiles ...
    "48": {
      "id": 48,
      "row": 9,
      "column": 2,
      "nextTileId": 49,
      "action": {
        "actionType": "NoOp",
        "destinationTileId": -1,
        "description": "No operation performed"
      }
    },
    "49": {
      "id": 49,
      "row": 9,
      "column": 3,
      "nextTileId": 50,
      "action": {
        "actionType": "NoOp",
        "destinationTileId": -1,
        "description": "No operation performed"
      }
    },
    "50": {
      "id": 50,
      "row": 9,
      "column": 4,
      "nextTileId": -1,
      "action": {
        "actionType": "NoOp",
        "destinationTileId": -1,
        "description": "No operation performed"
      }
    }
  },
  "rows": 10,
  "columns": 5
}
```

## Tips & Notes

* **Saving Players and Boards:**
    * Saved players are expected to be located in `Files/Players/players.csv` in the project's root directory.
    * Custom game boards (JSON files) should be placed in `Files/Boards/` in the project's root directory for the "Upload Board" function to easily find them. The application starts the file chooser in this directory.
* **Player Icons:**
    * To ensure player icons are displayed correctly in the player setup and on the board, make sure the image files (e.g., `hat.png`, `car.png`, `dragon.png`) are available in the resource directory: `src/main/resources/edu/ntnu/idi/idatt/view/resources/icons/`.
    * If an icon is missing, a default circle will be displayed.
* **Requirements for Custom Boards (JSON):**
    * When uploading a custom board for "Snakes & Ladders," the JSON file must be correctly formatted.
    * The board must contain between 50 and 150 tiles.
    * All tiles from 1 to the total number of tiles must be defined.
    * Ensure that `nextTileId` for all tiles (except the last one) points to a valid, existing tile ID. The last tile can have `nextTileId` set to `-1` or a non-positive value.
* **Player Setup:**
    * You can add between 2 and 4 players for a game session.
    * Player names must be between 2 and 15 characters long.
* **Fullscreen:**
    * The application is configured to start in fullscreen mode. You can press the `ESC` key to exit fullscreen.
* **Navigation After Game End:**
    * When a game is won, a dialog box will appear. From here, you can choose to go back to the game selection screen to start a new game, or return to the main menu (title screen).

