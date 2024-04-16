import os
import cv2
from torchvision import transforms,models
from Classifier import *

# dataset path
dataPath="test_data/v1"

# index filename
indexFilename="index.csv"

# classification model path
classifierPath=os.path.join("classification_models","mobilenet_v2","50_ep_v1_3_color_white.pt")

# classification model constructor
classifierConstructor=models.mobilenet_v2

# classification confidence threshold
minConfidence=0.5

# classes
classes=["white_square","black_square","white_man","black_man"]

# transformations for the images to predict
transform = transforms.Compose([
    transforms.ToTensor(),
    transforms.Grayscale(num_output_channels=3)
    
])

# classification model loading
classifier=Classifier(classes)
classifier.loadModel(modelConstructor=classifierConstructor,modelPath=classifierPath)

# 'write' or 'append' mode
mode=input("mode? ('w'|'a'):");

# starting point
startImageFilename=input("start image filename ('all' to do all):");

ok=(startImageFilename=="all")

# loading index file
indexFile=open(os.path.join(dataPath,indexFilename),mode)

# adding labels in the first row if 'write' mode
if(mode=="w"):
    indexFile.write("fileName;classIndex;label\n")

# adding an empty line in the first available row if 'append' mode  
else:
    indexFile.write("\n")

# getting and sorting filenames
imageFilenames=[f for f in os.listdir(dataPath) if f.endswith(('.jpg'))]
imageFilenames.sort()

for imageFilename in imageFilenames:
    ok=(ok or imageFilename==startImageFilename)
    if(ok):
        # getting and showing image
        image=cv2.imread(f"{dataPath}/{imageFilename}")
        cv2.imshow(imageFilename,image)
        classification=classifier.classifyCheckersPiece(image,transform)
        # printing user interface
        print(f"q->{classes[0]}\nw->{classes[1]}\na->{classes[2]}\ns->{classes[3]}\np->{classification.className}\n");

        # waiting for input
        type=cv2.waitKey(0)

        # choosing the class
        if(type==ord('q')):
            classIndex=0
        elif(type==ord('w')):
            classIndex=1
        elif(type==ord('a')):
            classIndex=2
        elif(type==ord('s')):
            classIndex=3;
        elif(type==ord('p')):
            classIndex=classification.classIndex;
        elif(type==ord('e')):
            break;
        
        # printing user interface
        print(f"{imageFilename}->{classes[classIndex]}\n")
        
        # closing window
        cv2.destroyWindow(imageFilename)

        # adding record to index file
        indexFile.write(f"{imageFilename};{classIndex};{classes[classIndex]}\n")

# closing windows and files
cv2.destroyAllWindows()
indexFile.close()

