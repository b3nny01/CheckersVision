# CheckersVision App

This folder contains the code and materials necessary for implementing the Android Application. 

### Controller

This subfolder contains the various activities of our application, and consequently most business logic.
There are four main activities:

* MainActivity: home page, allows the user to start a new game or view old ones.
* NewGameActivity: where pictures of the board are taken.
* PredictionActivity: recreates the virtual game image.
* OldGamesActivity: log for old games.

Each one is associated with an activity layout, that defines the structure of the user interface of the respective activity.

The following picture shows the activity flow.

<img src="../_readmeImgs_/activitiy_flow.png">

### Model

This contains all the classes that make up our domain model. 
Specifically, we decided to split the checkers game representation into two branches: on one side there is the real game, on the other the predicted, virtual game, each with its own classes that mirror one another.

In the first version implemented, 


### Persistence

It contains our Storage Manager, whose job is to access the application's local storage to save, retrieve, or delete the pictures taken by the user.

On top of that, it contains the function used to load the prediction model before starting the prediction process.
