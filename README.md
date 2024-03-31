# CheckersVision

CheckersVision is an android application for the recognition of checkers games.

It allows users to create a digital representation of their on-board match by recording it with their mobile camera.

Recorded games are then stored inside the application so that users are able to access them whenever they want.

### Development

Its development followed 3 main phases:

* **model development** : during this first phase we used computer vision techniques to create a model capable of recognizing checkers position pictures, more details about how the model works can be found inside CheckersVisionPython folder
* **application development** : after that we developed the infrastructure of the android application, that was our final goal, more detail about it can be found inside CheckersVisionApp folder
* **shift clock extension development** : once we had a first working prototype of the application we decided to extend it developing an arduino shift clock capable of connecting to the application by an OTG cable, more detail about it inside CheckersClockArduino folder

### **Application Screenshots**


<img src="resources/external_view.jpeg" width="240px" height="auto">

<img src="resources/main_activity_screen.jpeg" width="240px" height="auto">

<img src="resources/new_game_activity_screen_0.jpeg" width="240px" height="auto">

<img src="resources/new_game_activity_screen_1.jpeg" width="240px" height="auto">

<img src="resources/prediction_activity_screen.jpeg" width="240px" height="auto">

<img src="resources/old_games_activity_screen.jpeg" width="240px" height="auto">
