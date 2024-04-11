# simple-score-board
Simple score board library

The main goal was to create a simple score board and show the cooperation between Java and Kotlin language, and how simple it is to start Kotlin development from tests.

General assumptions:
- team can play only one game at time, so you can actually identify a game by only one team name.
- home team means team, which is taking the home side of the stadium with the first color kit. It is not connected to the game location. It's World cup, we have two home teams here at most.
- away team means team, which is taking the away side of the stadium with the second color kit.
- team names are free and not validated against real country names.

There are two interfaces:
1. BoardManager - provides methods required by the spec 
2. Board - provides some extensions to the original API

Board has two implementations, java and kotlin ones. Both are meeting the same requirements and are testable via kotlin test suite, just comment out the chosen variable initialisation.

Project is "basic" buildable, just use mvn clean install -U and add dependency to your project.


