import os
import cv2
from torchvision import transforms

# raw dataset path
rawDataPath=os.path.join("raw_train_data","v1")

# output dataset path
outDataPath=os.path.join("train_data","v1")

# getting filenames
imageFilenames = [f for f in os.listdir(rawDataPath) if f.endswith(('.jpg'))]

for i in range(len(imageFilenames)):
    # getting image
    imageFilename = imageFilenames[i]
    imageName=imageFilename.replace(".jpg","")
    imagePath = os.path.join(rawDataPath,imageFilename)
    image = cv2.imread(imagePath)

    # applying transformations
    #----------------------v1------------------------
    image=image[1100:3000,600:2500]
    image=cv2.resize(image,(410,410))
    # cv2.imshow("image resized",image)
    # cv2.waitKey(0)
    # cv2.destroyAllWindows()
    #------------------------------------------------

    #----------------------v-2-----------------------
    # image=image[500:1100,300:900]
    # image=cv2.resize(image,(410,410))
    # cv2.imshow("image resized",image)
    # cv2.waitKey(0)
    # cv2.destroyAllWindows()
    #------------------------------------------------

    #----------------------v3------------------------
    # image=image[925:3115,0:2230]
    # image=cv2.resize(image,(410,410))
    # cv2.imshow("image resized",image)
    # cv2.waitKey(0)
    # cv2.destroyAllWindows()
    #------------------------------------------------



    for j in range(8):
        for k in range(8):
            # getting squares images
            squareImage=image[5+50*j:55+50*j,5+50*k:55+50*k]

            # resizing image to match the previous ones
            squareImageResized=cv2.resize(squareImage,(65,65))
            
            # saving images
            squareImagePath=os.path.join(outDataPath,f"{imageName}_{j}_{k}.jpg")
            cv2.imwrite(squareImagePath,squareImageResized)