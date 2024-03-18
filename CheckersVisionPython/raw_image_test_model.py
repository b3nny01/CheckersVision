import os
import numpy as np
from Classifier import *
from torchvision import transforms,models

# raw image path
rawImagePath = os.path.join("raw_test_data","Image_1705043924629.jpg")

# classification model path
classifierPath=os.path.join("classification_models","mobilenet_v2","50_ep_v1_3.pt")

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

# resources path
resPath="resources"

# icons loading and resizing
chessboardImage=cv2.imread(os.path.join(resPath,"chessboard.png"))
icons=[]
for i in range(4):
    icons.append(cv2.imread(os.path.join(resPath,f"{classes[i]}.png")))
    icons[i]=cv2.resize(icons[i],(55,55))

# prediction image creation
predictionImage=np.zeros((55*8,55*8,3), np.uint8)

# classification model loading
classifier=Classifier(classes)
classifier.loadModel(modelConstructor=classifierConstructor,modelPath=classifierPath)

# loading raw image
rawImage=cv2.imread(rawImagePath)
# cv2.imshow("testo",rawImage)
# cv2.waitKey(0)
# cv2.destroyAllWindows()

# raw image transformations
rawImage=cv2.resize(rawImage,(765,1020))
rawImage=rawImage[250:860,75:685]

# showing image to predict
cv2.imshow("to_predict",rawImage)

# building prediction
counter=0;
for j in range(8):
    counter=(counter+1)%2
    for k in range(8):
        # predict only if it is a black square
        if((counter+k)%2!=0):
            squareImage=rawImage[45+55*j:110+55*j,45+55*k:110+55*k]
            # squareImage=cv2.resize(squareImage,(65,65))
            classification=classifier.classifyCheckersPiece(squareImage,transform)
            predictionImage[55*j:55+55*j,55*k:55+55*k,0:3]=icons[classification.classIndex]
            print(f"{j}_{k}->{classification.className}:{classification.confidence*100:4.2f}%")
        # or else put a white square
        else:
            predictionImage[55*j:55+55*j,55*k:55+55*k,0:3]=icons[0]

# showing prediction
cv2.imshow("prediction",predictionImage)

cv2.waitKey(0)
cv2.destroyAllWindows()


