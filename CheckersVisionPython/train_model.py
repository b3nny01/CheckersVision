import torch
from CustomDataset import *
from torchvision import models
from torch.utils.data import DataLoader
from torchvision import transforms

# raw dataset version
rawDataVersion="1_2_3"

# dataset path
dataPath=os.path.join("train_data","v1_2_3")

# index filename
indexFilename="index.csv"

# epochs
numEpochs = 100

# output model path 
outModelPath=os.path.join("classification_models","mobilenet_v2",f"{numEpochs}_ep_v{rawDataVersion}_color.pt")

# loading torchvision pretrained model
model = models.mobilenet_v2(weights=models.mobilenet.MobileNet_V2_Weights.DEFAULT)

# transformations for the images
transform = transforms.Compose([
    transforms.ToTensor(),
    #transforms.Grayscale(num_output_channels=3)
])

# custom dataset with transformations
custom_dataset = CustomDataset(dataPath,indexFilename,transform)

# changing class number
num_classes = len(custom_dataset.classes)
model.classifier[-1] = torch.nn.Linear(model.last_channel, num_classes)

# check GPU availability
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# moving the model to GPU if possible
model.to(device)

# loss function and optimizer
criterion = torch.nn.CrossEntropyLoss(ignore_index=0)
optimizer = torch.optim.Adam(model.parameters(), lr=0.001)

# loader
train_loader = DataLoader(
    custom_dataset,
    batch_size=64,
    shuffle=True,
    num_workers=8,
)

# training loop
print("starting training...")
for epoch in range(numEpochs):
    model.train()
    for images, labels in train_loader:
        images,labels=images.to(device),labels.to(device)
        optimizer.zero_grad()
        outputs = model(images)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()
    print(f"epoch {epoch+1}/{numEpochs}, Loss: {loss.item():.4f}")

# saving trained model
torch.save(model.state_dict(), outModelPath)