import os
from Classifier import *
from torchvision import transforms,models

# epochs
numEpochs = 50

# raw dataset version
rawDataVersion="1_3"

# classification model path
classifierPath=os.path.join("classification_models","mobilenet_v2",f"{numEpochs}_ep_v{rawDataVersion}_color.pt")

# optimized model path
optimizedModelPath=os.path.join("optimized_models","mobilenet_v2",f"{numEpochs}_ep_v{rawDataVersion}_color.ptl")

# classification model constructor
classifierConstructor=models.mobilenet_v2

# classification confidence threshold
minConfidence=0.5

# classes
classes=["white_square","black_square","white_man","black_man"]


# transformations for the images to predict
transform = transforms.Compose([
    transforms.ToTensor(),
    #transforms.Grayscale(num_output_channels=3)
    
])

# classification model loading
classifier=Classifier(classes,"cpu")
classifier.loadModel(modelConstructor=classifierConstructor,modelPath=classifierPath)

optimizedModel=classifier.getOptimizedModel(False)
optimizedModel._save_for_lite_interpreter(optimizedModelPath)