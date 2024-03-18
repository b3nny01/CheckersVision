import os
import numpy as np
from Classifier import *
from torchvision import transforms,models

# webcam url
cameraUrl = 'http://192.168.1.19:8080/video'

# classification model path
classifierPath=os.path.join("classification_models","mobilenet_v2","50_ep_v1.pt")

# classification model constructor
classifierConstructor=models.mobilenet_v2

# classification confidence threshold
# minConfidence=0.5

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



# connection to webcam
cap = cv2.VideoCapture(cameraUrl)

if not cap.isOpened():
    print("camera closed...")
    cap.release()
    exit()

while True:
    done,frame=cap.read()

    # frame transformation
    frame=cv2.resize(frame,(960,540))
    frame=frame[0:540,100:655]
    frame=cv2.resize(frame,(550,550))
    frameCopy=frame.copy()
    frame[35:515,35:515]=cv2.addWeighted(frame[35:515,35:515],0.4,chessboardImage,0.3,0)

    cv2.imshow("camera",frame)

    key=cv2.waitKey(10)

    # if input is 'p' predict
    if(key==ord("p")):
        # showing the frame to predict
        cv2.imshow("to_predict",frameCopy)

        # building prediction image 
        counter=0;
        for j in range(8):
            counter=(counter+1)%2
            for k in range(8):
                # predict only if it is a black square
                if((counter+k)%2!=0):
                    squareImage=frameCopy[45+55*j:110+55*j,45+55*k:110+55*k]
                    classification=classifier.classifyCheckersPiece(squareImage,transform,{0})
                    predictionImage[55*j:55+55*j,55*k:55+55*k,0:3]=icons[classification.classIndex]
                    print(f"{j}_{k}->{classification.className}:{classification.confidence*100:4.2f}%")
                # else put a white square
                else:
                    predictionImage[55*j:55+55*j,55*k:55+55*k,0:3]=icons[0]

        # showing prediction
        cv2.imshow("prediction",predictionImage)

    # else if input is 'q' quit
    elif(key==ord("q")):
        break
        
cv2.destroyAllWindows()

