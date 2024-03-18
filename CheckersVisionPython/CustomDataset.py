import os
import torch
import pandas as pd
from torch.utils.data import Dataset
from PIL import Image

class CustomDataset(Dataset):
    def __init__(self, dataPath, indexFilename, transform=None,transform_target=None):
        # dataset path
        self.dataPath = dataPath
        
        # index filename
        self.indexFilename=indexFilename
        
        # transformations for dataset images
        self.transform = transform
        
        # transformations for dataset indexes
        self.transform_target=transform_target
        
        # classes
        self.classes=["white-square","black-square","white-man","black-man"]
        
        # index file
        self.indexFile=pd.read_csv(os.path.join(self.dataPath,self.indexFilename),sep=';')

    def __len__(self):
        return len(self.indexFile['fileName'])

    def __getitem__(self, idx): 
        # image loading
        imageFilename = self.indexFile['fileName'][idx]
        imageFilePath = os.path.join(self.dataPath, imageFilename)
        image = Image.open(imageFilePath).convert("RGB")

        # getting the image class index
        classIndex= torch.tensor(int(self.indexFile['classIndex'][idx]))

        # applying transformations 
        if self.transform:
            image=self.transform(image)
        if self.transform_target:
            classIndex=self.transform_target(classIndex)

        # returning image and class    
        return image,classIndex

