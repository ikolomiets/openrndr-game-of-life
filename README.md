# Fast rendering of Conway's "Game Of Life" using OPENRNDR

This project is based on [openrndr-template](https://github.com/openrndr/openrndr-template)

![Demo Video](demo.gif)
_FPS has been reduced to 30 for animated GIF; actual FPS is much higher_

Application consists of two classes:

* `GameOfLife.kt` implements abstract grid and game's rules 
* `Main.kt` renders evolution of game's initial state using [OPENRNDR](https://openrndr.org) API

## Gradle tasks

 - `./gradlew run` runs the program (Use `gradlew.bat run` under Windows)
 - `./gradlew shadowJar` creates an executable platform specific jar file with all dependencies. Run the resulting program by typing `java -jar build/libs/openrndr-game-of-life-1.0.0-all.jar` in a terminal from the project root (add `-XstartOnFirstThread` for Mac OS).
 - `./gradlew jpackageZip` creates a zip with a stand-alone executable for the current platform (works with Java 14 only)
