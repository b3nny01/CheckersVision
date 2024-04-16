import torch
from CustomDataset import *
from torchvision import models
from torch.utils.data import DataLoader
from torchvision import transforms
from Classifier import *
from sklearn.metrics import classification_report

# raw data version
rawDataVersion="1"

# classification model path
classifierPath=os.path.join("optimized_models","mobilenet_v2","50_ep_v1_3_color.ptl")

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


# dataset path
dataPath=os.path.join("train_data",f"v{rawDataVersion}")

# index filename
indexFilename="index.csv"

# custom dataset with transformations
custom_dataset = CustomDataset(dataPath,indexFilename,transform)

# loader
train_loader = DataLoader(
    custom_dataset,
    batch_size=1,
    shuffle=True,
    num_workers=8,
)

# check GPU availability
# device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
device="cpu"

# classification model loading
classifier=Classifier(classes,device=device)
# classifier.loadModel(modelConstructor=classifierConstructor,modelPath=classifierPath)
classifier.loadOptimizedModel(modelPath=classifierPath)

# prepare to count predictions for each class
correct_pred = {classname: 0 for classname in classes}
total_pred = {classname: 0 for classname in classes}

# training loop
print("testing...")
v_labels=[]
v_predictions=[]
with torch.no_grad():
    for images, labels in train_loader:
        images,labels=images.to(device),labels.to(device)
        outputs = classifier.model(images)
        _, predictions = torch.max(outputs, 1)
        
        if(labels[0].item()!=0):
            v_labels.extend(labels.cpu().numpy())
            v_predictions.extend(predictions.cpu().numpy())
    cr=classification_report(np.array(v_labels),np.array(v_predictions),target_names=["black_square","white_man","black_man"], zero_division=1)
    print(cr)