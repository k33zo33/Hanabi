# Hanabi Multiplayer Game Simulation

## Overview
This application is a Java-based simulation of the popular cooperative card game Hanabi. It supports both single-player and multiplayer modes, with options for hosting a game or joining an existing game. The application is built using JavaFX for the graphical interface and implements various advanced Java features.

## Features


  - Implemented a game engine with basic game logic, data structures using a class hierarchy, and game management classes.
  - Created a responsive JavaFX graphical interface.
  - Enhanced the GUI to be responsive using threads and synchronization.
  - Enabled asynchronous resource retrieval.
  - Implemented game state serialization for saving and loading game progress.
  - Used Reflection API for dynamic generation of program documentation.
  - Enabled network communication for multiplayer games using TCP/UDP protocols and socket communication with threading.
  - Implemented JNDI and RMI technologies for teammate selection and in-game chat.
  - Used SAX/DOM parsers to save game configurations and moves in XML format.
  - Implemented the ability to replay saved game moves from XML files.

## Setup Instructions


### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Apache Maven
- Git

### Installation

1. **Clone the repository:**
    ```sh
    git clone https://github.com/yourusername/hanabi-game-simulation.git
    cd hanabi-game-simulation
    ```

2. **Install Dependencies:**
    ```sh
    mvn clean install
    ```

3. **Run the Application:**
   
    - Single-player mode:
    Run the app with the argument: "SINGLE_PLAYER"

    - Host a multiplayer game:
    Run the app with the argument: "HOST"

    - Join a multiplayer game:
   Run the app with the argument: "CLIENT"
    

## Usage

### Game Modes
- **Single-Player**: Play the game with yourself controling both players.
- **Host**: Start a new multiplayer game and wait for other players to join.
- **Client**: Join an existing multiplayer game hosted by another player.

### Game Features
- **Responsive GUI**: Intuitive and responsive graphical interface built with JavaFX.
- **Save/Load Game**: Save your game progress and load it later using serialization.
- **Documentation Generation**: Automatically generate documentation using the Reflection API.
- **Network Play**: Play with others over a network using TCP/UDP protocols.
- **In-game Chat**: Communicate with other players using JNDI and RMI technologies.
- **XML Configuration**: Save and load game configurations and moves in XML format.
- **Replay**: Replay saved game moves from XML files.


## Contributing
Contributions are welcome! Please fork the repository and submit pull requests.


