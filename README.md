# CheckersVision

CheckersVision is an Android application for the recognition of checkers games.
It allows users to create a digital representation of their on-board match by recording it with their mobile camera.
Recorded games are then stored inside the application so that users may access them whenever they want.

### Development

Its development followed 3 main phases:

* **Model Development**: during this first phase we used computer vision techniques to create a model capable of recognizing checkers position pictures, more details about how the model works can be found inside the CheckersVisionPython folder.
* **Application Development**:  this phase revolved around developing a working infrastructure for the Android application,  more details about it can be found inside the CheckersVisionApp folder.
* **Shift Clock Extension Development**: once the first working prototype was ready, it was expanded with an Arduino shift clock capable of connecting to the application by an OTG cable, more details about it can be found inside the CheckersClockArduino folder.

### Final Result

<table border="0px">

  <tr>
    <td colspan="4" >
      <p align="center">
      <img src="_readmeImgs_/external_view.jpeg" width="240px" height="auto">
      <h4>Use Case Example</h4>
      <p>In this picture we can see the typical use case of the application: the device is positioned over the board, connected to the shift clock, and every time a player makes a move and presses the button, a picture of the current board is taken. Once the game ends, the user moves on to another activity, where the images are analyzed and predicted. </p>
      <p>Alternatively, the user can review past games saved and stored by the application. </p>
      </p>
    </td>

  </tr>
  <tr rowspan="0"></tr>
  <tr>
    <td rowspan="3"><p align="center"><img src="_readmeImgs_/main_activity_screen.jpeg" width="150px" height="auto"></p></td>
    <td><p align="center"><img src="_readmeImgs_/new_game_activity_screen_0.jpeg" width="150px" height="auto"></p></td>
    <td><p align="center"><img src="_readmeImgs_/new_game_activity_screen_1.jpeg" width="150px" height="auto"></p></td>
    <td rowspan="3"><p align="center"><img src="_readmeImgs_/prediction_activity_screen.jpeg" width="150px" height="auto"></p></td>
  </tr>
  <tr rowspan="0"></tr>
  <tr>
    <td><p align="center"><img src="_readmeImgs_/old_games_activity_screen.jpeg" width="150px" height="auto"></p></td>
    <td></td>
  </tr>

<table>

### Credits

The project has been developed by:
* [b3nny01](https://github.com/b3nny01/)
* [jjulespop](https://github.com/jjulespop/)

The code uses a modified version of the [Arduino-Library](https://github.com/omaraflak/Arduino-Library) by omaraflak:

