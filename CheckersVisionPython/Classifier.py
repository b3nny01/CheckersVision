import numpy as np
import cv2
import torch
from torch.utils.mobile_optimizer import optimize_for_mobile
from pathlib import Path
from PIL import Image
from torchvision import transforms as T
from Classification import *

class Classifier:
    def __init__(self, classes, device=("cuda" if torch.cuda.is_available() else "cpu")):
        self.device = torch.device(device)
        #self.device = torch.device("cpu")
        self.model = None
        self.classes= classes
        self.numClasses=len(self.classes)
        self.colors = np.random.uniform(
            low=0, high=255, size=len(self.classes))
        

    def loadModel(self,modelConstructor,modelPath):
        print(f"loading classifier at {modelPath}...")

        # building model
        self.model = modelConstructor(weights=None,num_classes=self.numClasses)
        self.model.classifier[-1] = torch.nn.Linear(self.model.last_channel, self.numClasses)

        # loading model
        self.model.load_state_dict(torch.load(modelPath, map_location=torch.device('cpu')))
        
        # evaluating
        self.model.to(self.device)
        self.model.eval()
    
        print("classifier loaded")


    def predictImage(self, image, transform):
        if isinstance(image, np.ndarray):
            # image transormation
            im_tensor = transform(image).unsqueeze(0).to(self.device)

            # detecting objects
            with torch.no_grad():
                predictions = self.model(im_tensor)

            confidences = torch.nn.functional.softmax(predictions[0], dim=0)
            return confidences.cpu()
        else:
            print("invalid input format, please provide an image in np array format.")
            exit()

    def classifyCheckersPiece(self,image, transform, excludedIndexes=None):
        confidences=self.predictImage(image, transform)

        if excludedIndexes:
            for excludedIndex in excludedIndexes:
                confidences[excludedIndex]=-1;

        maxIndex=confidences.argmax()
        classification=Classification(self.classes[maxIndex],maxIndex,confidences[maxIndex])
        return classification
    
    def getOptimizedModel(self,quantization=False):
        if(quantization):
            optimizedModel=torch.quantization.quantize_dynamic(self.model, dtype=torch.qint8).to(self.device)
        else:
            optimizedModel=self.model
        optimizedModel=torch.jit.script(optimizedModel).to(self.device)
        optimizedModel=optimize_for_mobile(optimizedModel).to(self.device)
        return optimizedModel

        
