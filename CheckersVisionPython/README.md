# CheckersVision Python

This folder contains the Python code developed for creating, training, and testing the model. During this phase, the main goal was the creation of a classifier capable of recognizing an image of a checkers square as:

* WHITE_SQUARE
* BLACK_SQUARE
* WHITE_MAN
* BLACK_MAN

For the realization of the first prototype of the model, we decided to avoid the recognition of WHITE_KING and BLACK_KING. This feature will be added in following releases.
W

### Development

The development followed 3 main phases:

* **Dataset Creation:** The dataset has been created from scratch. Firstly, we took pictures of checkers games and inserted them inside the raw_train_data folder. Then, we used the create_dataset.py script to split every checkers' position image into single square pictures and the create_index_file.py script to classify every square image, obtaining an index.csv for the dataset. The results were then saved inside the train_data folder. This process was repeated various times producing different versions of the dataset. During this phase, we mainly used CV2 and Pandas respectively for handling images and index files.
* **Model Creation and Testing:** Once we had a dataset, we used the train_model.py script to generate various classifiers having different base models, number of epochs, etc., saved inside the classification_models folder. Created models were then tested using the raw_image_test_model.py and the camera_test_model.py scripts. In the end, also considering the embedded nature of the application, we opted for the MobileNetV2 network. For this phase, PyTorch was chosen for the gentler learning curve and its focus on custom models development.
* **Model Optimization:** During this phase, we used the optimize_model script to create an exportable version of the model capable of running on mobile devices. This was possible thanks to the PyTorch Mobile library. Since our model was quite simple and didn't need to run in real-time, we hadn't performed weights quantization, opting for a more precise result.

<img src="_readmeImgs_/python_dev_flow.png">



