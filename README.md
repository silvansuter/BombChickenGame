# BOMBS 'N' CHICKS Game

## Introduction

BOMBS 'N' CHICKS is a Java-based game where players must defuse bombs while being careful to not squashing any chicken. The game features random spawns, visuals, audio, a main menu, and tracking of the player's highscore.

## Gameplay

- Click on Bombs before they detonate to gain points.
- Do not squash any chickens while detonating the bombs. But be careful, they run around!
- The game's difficulty steadily increases. 
- Survive as long as possible to beat your highscore.

## Visual Demonstration

<div style="display: flex; justify-content: space-between; align-items: center;">
  <img src="resources/readmeAssets/TitleScreen.png" alt="Title Screen" width="45%">
  <img src="resources/readmeAssets/IngameFootage.png" alt="Ingame Footage" width="45%">
</div>


## Requirements to Run the Application

To successfully run this application, the following requirements must be met:

### Software Requirements

- **Java Runtime Environment (JRE)**: Java 8 (1.8) or higher.
  - The application uses features and APIs available in Java 8, including Swing and AWT libraries for the graphical user interface. It is compatible with Java 8 and newer versions.
  - Ensure that Java is installed on your system. You can check your Java version by running `java -version` in your command line or terminal.
  - If you do not have Java installed, or if your version is older than Java 8, you can download the latest version of Java from [Oracle's official website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or use an open-source alternative like [AdoptOpenJDK](https://adoptopenjdk.net/).

### Running the Application

1. Download the application's project directory.
2. Compile the project using a Java compiler.
3. Execute main method from the "BombsNChicksMain.java"-file.

## Features

- Separated GUI and Game logic entirely.
- Dynamic random spawning of bombs and chickens. Bombs continuously turn redder until they explode. The chickens run around.
- Sound and images, simple animations for the bombs.
- Main menu, game over and help screens.
- Score tracking. Saving the highscore even after closing the game.
- Increased difficulty over time by speeding up the spawning rate, the chickens, and the time until bombs explode.

## Development

Written in Java, using Java Swing for the GUI and Java Sound API for the sound handling.

## Planned Features

- Animations for chickens.
- ~~Restructuring project for better readibility.~~
- Sounds for ingame and for the main menu.
- AI that learns to play the game.

## Sources

The images were created with [DALL·E 3](https://openai.com/dall-e-3).

The sounds were created by me.

## Licensing

This project is licensed under the MIT License. This allows others to use, modify, and distribute this software without restriction.

For full details, please see the [LICENSE](./LICENSE) file in the repository.
