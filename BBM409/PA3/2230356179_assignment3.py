# %% [markdown]
# ## BBM 409 - Programming Assignment 3
# 
# * You can add as many cells as you want in-between each question.
# * Please add comments to your code to explain your work.  
# * Please add Markdown cells to answer the (non-coding) questions in the homework text. You can, however, refer to the outputs of code cells without adding them as images to the Markdown cell unless you are requested to do otherwise.
# * Please be careful about the order of runs of cells. Doing the homework, it is likely that you will be running the cells in different orders, however, they will be evaluated in the order they appear. Hence, please try running the cells in this order before submission to make sure they work.    
# * Please refer to the homework text for any implementation detail. Though you are somewhat expected to abide by the comments in the below cells, they are mainly just provided for guidance. That is, as long as you are not completely off this structure and your work pattern is understandable and traceable, it is fine. For instance, you do not have to implement a particular function within a cell just because the comment directs you to do so.
# * This document is also your report. Show your work.

# %% [markdown]
# ###  Mustafa Kemal Öz 2230356179

# %% [markdown]
# ## 1. Implementing a CNN from Scratch (40 points)

# %% [markdown]
# ### 1.1. Introduction
# 
# The objective of this assignment is to cultivate a comprehensive understanding of Convolutional Neural Networks (CNNs) and transfer learning techniques by applying them to an image classification task. We aim to first implement a basic CNN architecture from scratch using fundamental components like convolutional layers, pooling layers, and activation functions, and subsequently compare its performance against pre-trained models such as ResNet-18 and MobileNet. CNNs are specifically chosen for this task because, unlike traditional neural networks, they preserve the spatial structure of images and automatically learn hierarchical representations—ranging from simple edges to complex shapes—through convolution operations. The architecture typically consists of convolutional layers for feature extraction, pooling layers to reduce dimensionality, and fully connected layers to classify these features into final categories.
# 
# To facilitate this study, we utilize a subset of the Vegetable Image Dataset, which contains 4500 images representing 15 different vegetable species, such as Bean, Broccoli, and Tomato. The images are preprocessed by resizing them to a uniform dimension of 256x256 pixels and normalizing pixel values. To ensure a fair and robust evaluation, the dataset is stratified into 3000 training images (200 per class), 750 validation images (50 per class) for model selection, and 750 testing images (50 per class) to be evaluated in a Kaggle environment. This structure allows us to analyze the trade-offs between training a model from scratch versus fine-tuning pre-trained weights in terms of accuracy and computational efficiency.

# %% [markdown]
# ### 1.2. Data Loading and Preprocessing (3 points)

# %%
## Import necessary libraries

# PyTorch Core Libraries
import torch
import torch.nn as nn                # Neural network modules (layers, loss functions)
import torch.optim as optim          # Optimizers (SGD, Adam)
import torch.nn.functional as F      # Activation functions and other functional utilities

# Torchvision for Data Handling and Pre-trained Models
import torchvision
from torchvision import datasets, transforms  # For data loading and transformations
from torchvision import models                # For Part 2: Transfer Learning (ResNet, MobileNet)
from torch.utils.data import DataLoader       # To create mini-batches

# Numerical and System Libraries
import numpy as np                   # Matrix operations
import os                            # File path handling

# Visualization Libraries
import matplotlib.pyplot as plt      # For plotting training/validation loss
import seaborn as sns                # For visualizing the confusion matrix

# Evaluation Metrics
# To compute accuracy, precision, recall, and F1-score
from sklearn.metrics import confusion_matrix, classification_report, accuracy_score, precision_score, recall_score, f1_score

# Device Configuration
# Checks if GPU is available to speed up training, otherwise uses CPU
if torch.backends.mps.is_available():
    device = torch.device("mps")
    print("Apple Metal (MPS) GPU is being used!")
elif torch.cuda.is_available():
    device = torch.device("cuda")
    print("NVIDIA CUDA GPU is being used!")
else:
    device = torch.device("cpu")
    print("CPU is being used (may be slower).")

# %%
## Load the dataset using PyTorch's data loading utilities
## Apply necessary preprocessing such as resizing and normalization
## Divide the dataset into training, validation, and testing subsets

# 1. Define Data Transformations
# Resize to 256x256 and Normalize
data_transforms = transforms.Compose([
    transforms.Resize((256, 256)),        # Transform to 256x256 size
    transforms.ToTensor(),                # Convert image to PyTorch Tensor (scales to 0-1)
    # Normalization: mean and std deviation for RGB channels.
    # Usually ImageNet standards are used: mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
])

# 1. Define Paths
train_dir = './pa3_subset/train' 
val_dir = './pa3_subset/val' 
test_dir = './test_unlabeled_v2'

# 2. Load Datasets Separately

# Train Set
train_dataset = datasets.ImageFolder(root=train_dir, transform=data_transforms)

# Validation Set
val_dataset = datasets.ImageFolder(root=val_dir, transform=data_transforms)

# Test Set
'''if os.path.exists(test_dir):
    test_dataset = datasets.ImageFolder(root=test_dir, transform=data_transforms)
else:
    print("Test folder not found. Proceeding without test dataset.")'''

# Print dataset sizes and class names
print(f"Number of training samples: {len(train_dataset)}")
print(f"Number of validation samples: {len(val_dataset)}")
print(f"Classes: {train_dataset.classes}")

# 3. Create DataLoaders
batch_size = 32

train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True, num_workers=2)
val_loader = DataLoader(val_dataset, batch_size=batch_size, shuffle=False, num_workers=2)

'''# If test dataset exists, create its DataLoader
if 'test_dataset' in locals():
    test_loader = DataLoader(test_dataset, batch_size=batch_size, shuffle=False, num_workers=2)'''

print("DataLoaders are ready!")

# %% [markdown]
# ### 1.3. Define your CNN architecture (7 points)
# 
# **Reasoning Behind the Architecture:**
# The proposed CNN architecture is designed with a depth of five convolutional layers to enable robust hierarchical feature extraction, satisfying the assignment requirements. We adopt a "pyramid" structure where the number of filters doubles at each subsequent layer (32 $\rightarrow$ 64 $\rightarrow$ 128 $\rightarrow$ 256 $\rightarrow$ 512). This design allows the network to capture simple low-level features like edges and textures in the initial layers, while progressively learning more complex, high-level semantic patterns in the deeper layers. To manage computational efficiency and spatial invariance, Max Pooling layers are interspersed after each convolution, systematically reducing the spatial dimensions from $256\times256$ down to $8\times8$. A Dropout layer (0.5 probability) is incorporated before the final fully connected classification layer to act as a regularizer, preventing the model from overfitting to the training data.
# 
# **Choice of Activation Functions:**
# We utilize the **ReLU (Rectified Linear Unit)** activation function after every convolutional layer ReLU is chosen primarily for its ability to introduce non-linearity into the model, allowing it to approximate complex functions necessary for image recognition. Unlike Sigmoid or Tanh, ReLU mitigates the vanishing gradient problem, facilitating faster and more stable convergence during training. For the output layer, no explicit activation function is defined within the model class because the chosen loss function, `nn.CrossEntropyLoss`, automatically applies `LogSoftmax` internally to ensure numerical stability during multi-class classification.

# %%
## Design a CNN architecture with at least 3 and at most 6 convolutional layers
## Add activation functions (e.g., ReLU) after each convolutional layer
## Intersperse pooling layers (e.g., max pooling) to reduce spatial dimensions
## Add a fully connected layer at the end to map features to output classes

## Design a CNN architecture
class SimpleCNN(nn.Module):
    def __init__(self, num_classes=15):
        super(SimpleCNN, self).__init__()
        
        # Input image size: (3, 256, 256)
        
        # --- Layer 1 ---
        # Conv: 3 input channels (RGB) -> 32 output filters, 3x3 kernel, padding=1 (to keep size same)
        # Pool: Reduces spatial dimensions by half (256 -> 128)
        self.conv1 = nn.Conv2d(in_channels=3, out_channels=32, kernel_size=3, padding=1)
        
        # --- Layer 2 ---
        # Conv: 32 -> 64 filters
        # Pool: Reduces spatial dimensions by half (128 -> 64)
        self.conv2 = nn.Conv2d(in_channels=32, out_channels=64, kernel_size=3, padding=1)
        
        # --- Layer 3 ---
        # Conv: 64 -> 128 filters
        # Pool: Reduces spatial dimensions by half (64 -> 32)
        self.conv3 = nn.Conv2d(in_channels=64, out_channels=128, kernel_size=3, padding=1)
        
        # --- Layer 4 ---
        # Conv: 128 -> 256 filters
        # Pool: Reduces spatial dimensions by half (32 -> 16)
        self.conv4 = nn.Conv2d(in_channels=128, out_channels=256, kernel_size=3, padding=1)
        
        # --- Layer 5 ---
        # Conv: 256 -> 512 filters
        # Pool: Reduces spatial dimensions by half (16 -> 8)
        self.conv5 = nn.Conv2d(in_channels=256, out_channels=512, kernel_size=3, padding=1)
        
        # Define Max Pooling layer (2x2 kernel)
        self.pool = nn.MaxPool2d(kernel_size=2, stride=2)
        
        # --- Fully Connected Layers ---
        # Calculation for flattened size:
        # Initial Size: 256x256
        # After 5 poolings: 256 -> 128 -> 64 -> 32 -> 16 -> 8
        # Final Tensor Shape: (Batch_Size, 512 channels, 8 height, 8 width)
        self.flatten_dim = 512 * 8 * 8
        
        # Dense Layer 1
        self.fc1 = nn.Linear(self.flatten_dim, 1024)
        
        # Output Layer (Maps to num_classes)
        self.fc2 = nn.Linear(1024, num_classes)
        
        # Dropout to prevent overfitting
        self.dropout = nn.Dropout(0.5)

    def forward(self, x):
        # Apply Conv -> ReLU -> MaxPool for each block 
        x = self.pool(F.relu(self.conv1(x)))
        x = self.pool(F.relu(self.conv2(x)))
        x = self.pool(F.relu(self.conv3(x)))
        x = self.pool(F.relu(self.conv4(x)))
        x = self.pool(F.relu(self.conv5(x)))
        
        # Flatten the output for the fully connected layer 
        x = x.view(-1, self.flatten_dim)
        
        # Fully Connected layers 
        x = F.relu(self.fc1(x))
        x = self.dropout(x)
        x = self.fc2(x)
        
        return x

# Initialize the model and move it to the configured device (GPU/MPS/CPU)
num_classes = 15 # Vegetable dataset has 15 classes
model_scratch = SimpleCNN(num_classes=num_classes).to(device)

print("Model Architecture Created:")
print(model_scratch)

# %% [markdown]
# ### 1.4 Prepare the model for training (3 points)
# 
# **Choice of Loss Function:**
# We selected **Cross Entropy Loss** (`nn.CrossEntropyLoss`) as the objective function for this task. This is the standard loss function used for multi-class image classification problems. It measures the divergence between the predicted probability distribution and the actual class labels, heavily penalizing high-confidence incorrect predictions. By minimizing this loss, the model learns to assign the highest probability to the correct vegetable class among the 15 available categories. It is worth noting that PyTorch’s implementation of CrossEntropyLoss combines `nn.LogSoftmax` and `nn.NLLLoss` in a single class, which provides better numerical stability during training.
# 
# **Choice of Optimization Algorithm:**
# We chose the **Adam (Adaptive Moment Estimation)** optimizer for training the network. Adam is widely regarded as an effective default optimizer for deep learning because it computes adaptive learning rates for each parameter. By maintaining moving averages of both the gradients and the squared gradients (momentum), Adam combines the advantages of AdaGrad and RMSProp. This typically results in faster convergence compared to standard Stochastic Gradient Descent (SGD), especially in the early stages of training. We initialized Adam with a learning rate of **0.001**, which is a standard starting value that generally offers a good balance between training speed and stability.

# %%
## Define appropriate loss function for multi-class classification (e.g., cross-entropy loss)

criterion = nn.CrossEntropyLoss()

print(f"Loss function defined: {criterion}")

# %%
## Choose an optimizer (SGD or Adam) and set its parameters (e.g., learning rate)

# Hyperparameters:
# Learning Rate (lr): 0.001 is a standard starting point for Adam.
# Weight Decay: 1e-4 is applied for L2 Regularization to help prevent overfitting.
learning_rate = 0.001

optimizer = optim.Adam(model_scratch.parameters(), lr=learning_rate, weight_decay=1e-4)

print(f"Optimizer chosen: {type(optimizer).__name__}")
print(f"Learning Rate: {learning_rate}")

# %% [markdown]
# ### 1.5 Train and Validate the CNN model (10 points)

# %%
## Iterate over the training dataset in mini-batches
## Implement forward pass, compute loss, and backward pass for gradient computation
## Update model parameters using the optimizer based on computed gradients
## Validate the model on the validation set periodically and plot the validation loss
## Repeat the training process for a suitable number of epochs (at least 30epochs)

## Iterate over the training dataset, implement forward/backward pass, update parameters, and validate.
import copy # To copy weights
import time # To measure training time

# 1. Setup Configuration
num_epochs = 30 
best_val_acc = 0.0
best_model_wts = copy.deepcopy(model_scratch.state_dict()) # To store the best weights

# Lists to keep track of losses and accuracies for plotting
train_loss_history = []
train_acc_history = []
val_loss_history = []
val_acc_history = []

print(f"Training started on {device} for {num_epochs} epochs...")

start_time = time.time()

# 2. Training Loop
for epoch in range(num_epochs):
    # --- Training Phase ---
    model_scratch.train()  # Set model to training mode (enables Dropout)
    
    running_loss = 0.0
    running_corrects = 0
    
    for inputs, labels in train_loader:
        inputs = inputs.to(device)
        labels = labels.to(device)
        
        # Zero the parameter gradients
        optimizer.zero_grad()
        
        # Forward pass
        outputs = model_scratch(inputs)
        loss = criterion(outputs, labels)
        
        # Backward pass and optimize
        loss.backward()
        optimizer.step()
        
        # Statistics
        _, preds = torch.max(outputs, 1)
        running_loss += loss.item() * inputs.size(0)
        running_corrects += torch.sum(preds == labels.data)
        
    # Calculate epoch metrics for training
    epoch_train_loss = running_loss / len(train_loader.dataset)
    epoch_train_acc = running_corrects.float() / len(train_loader.dataset)
    
    # --- Validation Phase ---
    model_scratch.eval()   # Set model to evaluate mode (disables Dropout)
    
    val_running_loss = 0.0
    val_running_corrects = 0
    
    with torch.no_grad():  # Do not calculate gradients during validation to save memory
        for inputs, labels in val_loader:
            inputs = inputs.to(device)
            labels = labels.to(device)
            
            outputs = model_scratch(inputs)
            loss = criterion(outputs, labels)
            
            _, preds = torch.max(outputs, 1)
            val_running_loss += loss.item() * inputs.size(0)
            val_running_corrects += torch.sum(preds == labels.data)
            
    # Calculate epoch metrics for validation
    epoch_val_loss = val_running_loss / len(val_loader.dataset)
    epoch_val_acc = val_running_corrects.float() / len(val_loader.dataset)
    
    # Store history
    train_loss_history.append(epoch_train_loss)
    train_acc_history.append(epoch_train_acc.item())
    val_loss_history.append(epoch_val_loss)
    val_acc_history.append(epoch_val_acc.item())
    
    # Print progress
    print(f'Epoch {epoch+1}/{num_epochs} | '
          f'Train Loss: {epoch_train_loss:.4f} Acc: {epoch_train_acc:.4f} | '
          f'Val Loss: {epoch_val_loss:.4f} Acc: {epoch_val_acc:.4f}')
    
    # Deep copy the model if it has the best validation accuracy so far
    if epoch_val_acc > best_val_acc:
        best_val_acc = epoch_val_acc
        best_model_wts = copy.deepcopy(model_scratch.state_dict())
        torch.save(model_scratch.state_dict(), 'best_model_scratch.pth') # Save to file

time_elapsed = time.time() - start_time
print(f'\nTraining complete in {time_elapsed // 60:.0f}m {time_elapsed % 60:.0f}s')
print(f'Best Validation Accuracy: {best_val_acc:.4f}')

# Load best model weights
model_scratch.load_state_dict(best_model_wts)

# 3. Plotting Results
plt.figure(figsize=(12, 5))

# Plot Loss
plt.subplot(1, 2, 1)
plt.plot(train_loss_history, label='Train Loss')
plt.plot(val_loss_history, label='Validation Loss')
plt.title('Training and Validation Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()

# Plot Accuracy
plt.subplot(1, 2, 2)
plt.plot(train_acc_history, label='Train Accuracy')
plt.plot(val_acc_history, label='Validation Accuracy')
plt.title('Training and Validation Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()

plt.show()

# %% [markdown]
# ### Hyperparameter Selection
# 
# **Learning Rate (0.001):**
# We selected a learning rate of **0.001** because it is the empirical standard and a robust starting point for the **Adam** optimizer. Unlike SGD, which often requires higher learning rates (e.g., 0.01), Adam is an adaptive method that works efficiently with smaller initial rates. During training, this value proved effective as the training loss decreased steadily without exhibiting signs of divergence (which would suggest the rate was too high) or stagnation (which would suggest the rate was too low).
# 
# **Batch Size (32):**
# A batch size of **32** was chosen to strike a balance between computational efficiency and model generalization.
# * **Generalization:** Smaller batch sizes (like 32 or 64) introduce a certain amount of noise into the gradient estimation. This noise acts as a form of regularization, helping the model escape sharp local minima and settle into flatter minimas that generalize better to unseen data.
# * **Memory Efficiency:** Processing $256\times256$ color images consumes significant GPU memory. A batch size of 32 ensures that we stay well within the memory limits of the hardware while maximizing throughput.

# %%
## Select your best model with respect to validation accuracy
## Visualize the accuracy and loss change of the best performing model across training and validation datasets.

## Compute metrics such as accuracy, precision, recall, and F1-score
## Visualize confusion matrix to understand the model's behavior across different classes

# 1. Load the Best Model Weights
model_scratch.load_state_dict(torch.load('best_model_scratch.pth'))
model_scratch.eval() # Set to evaluation mode

all_preds = []
all_labels = []

# 2. Iterate over Validation Loader
print("Evaluating on Validation set...")
with torch.no_grad():
    for inputs, labels in val_loader:
        inputs = inputs.to(device)
        labels = labels.to(device)
        
        outputs = model_scratch(inputs)
        _, preds = torch.max(outputs, 1)
        
        all_preds.extend(preds.cpu().numpy())
        all_labels.extend(labels.cpu().numpy())

# 3. Compute Metrics
# Calculate Accuracy, Precision, Recall, and F1-Score 
accuracy = accuracy_score(all_labels, all_preds)
precision = precision_score(all_labels, all_preds, average='weighted')
recall = recall_score(all_labels, all_preds, average='weighted')
f1 = f1_score(all_labels, all_preds, average='weighted')

print(f"\nPerformance Metrics on Validation Set:")
print(f"Accuracy:  {accuracy:.4f}")
print(f"Precision: {precision:.4f}")
print(f"Recall:    {recall:.4f}")
print(f"F1-Score:  {f1:.4f}")

# 4. Classification Report (Detailed per-class metrics)
class_names = train_dataset.classes
print("\nDetailed Classification Report:")
print(classification_report(all_labels, all_preds, target_names=class_names))

# 5. Visualize Confusion Matrix 
cm = confusion_matrix(all_labels, all_preds)

plt.figure(figsize=(12, 10))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=class_names, yticklabels=class_names)
plt.title('Confusion Matrix (Simple CNN)')
plt.xlabel('Predicted Label')
plt.ylabel('True Label')
plt.xticks(rotation=45)
plt.show()

# %% [markdown]
# ### 1.6 Evaluate the trained model on the test set (5 points)

# %%
## Test the trained model on the test set to evaluate its performance
## Comment on the results

from PIL import Image
from torch.utils.data import Dataset, DataLoader
import pandas as pd

# --- 1. Custom Dataset Class (No subfolders required) ---
class UnlabeledDataset(Dataset):
    def __init__(self, root_dir, transform=None):
        self.root_dir = root_dir
        self.transform = transform
        # Get only image files (.jpg, .png, .jpeg)
        # Check to ignore hidden files on MacOS (e.g., .DS_Store)
        self.image_files = [f for f in os.listdir(root_dir) if f.lower().endswith(('.png', '.jpg', '.jpeg'))]
    
    def __len__(self):
        return len(self.image_files)
    
    def __getitem__(self, idx):
        img_name = self.image_files[idx]
        img_path = os.path.join(self.root_dir, img_name)
        
        # Open image and convert to RGB
        image = Image.open(img_path).convert('RGB')
        
        if self.transform:
            image = self.transform(image)
            
        # Return both image and filename (required for Kaggle ID)
        return image, img_name

# --- 2. Load Test Set and Generate Predictions ---
print("Loading Test Set...")

if os.path.exists(test_dir):
    # Using custom UnlabeledDataset instead of ImageFolder
    test_dataset_custom = UnlabeledDataset(root_dir=test_dir, transform=data_transforms)
    
    test_loader = DataLoader(test_dataset_custom, batch_size=32, shuffle=False, num_workers=0)
    print(f"Found {len(test_dataset_custom)} images in the test set.")
else:
    print("ERROR: Test directory not found!")

# --- 3. Prediction Loop ---
model_scratch.eval()
test_preds = []
test_filenames = []

print("Generating predictions for Kaggle...")

with torch.no_grad():
    for inputs, filenames in test_loader:
        inputs = inputs.to(device)
        
        # Forward pass
        outputs = model_scratch(inputs)
        _, preds = torch.max(outputs, 1)
        
        test_preds.extend(preds.cpu().numpy())
        test_filenames.extend(filenames)

# --- 4. Create Submission File ---
# Map class indices (0, 1, 2...) to actual class names ('Bean', 'Tomato'...)
idx_to_class = {v: k for k, v in train_dataset.class_to_idx.items()}
predicted_labels = [idx_to_class[p] for p in test_preds]

submission_df = pd.DataFrame({
    'Id': test_filenames,
    'Label': predicted_labels
})

# Save the file
submission_file = 'submission_cnn_scratch.csv'
submission_df.to_csv(submission_file, index=False)

print(f"✅ Success! Submission file saved as: '{submission_file}'")
print(submission_df.head())

# %% [markdown]
# ### 1.7 Conclusion and interpretation (7 points)
# 
# **Performance Summary:**
# The custom CNN model, constructed with 5 convolutional layers, demonstrated strong learning capabilities. Upon completion of 30 epochs, the model achieved a **Validation Accuracy of 93.33%**. This indicates that the architecture successfully learned to distinguish between the 15 vegetable classes with high reliability. The model was also evaluated on the hidden Test Set, and the predictions were generated and formatted for Kaggle submission to assess generalization on unseen data.
# 
# **Challenges and Areas for Improvement:**
# * **Overfitting:** Towards the final epochs, we observed a divergence where Training Accuracy approached near 99% while Validation Accuracy stabilized around 93%. This indicates mild overfitting. To improve this, we could implement **Data Augmentation** (random rotations, horizontal flips) to artificially increase dataset diversity or increase the **Dropout** rate.
# * **Technical Constraints:** We encountered specific challenges related to the macOS environment, such as `RuntimeError` with `num_workers > 0` and MPS (Metal Performance Shaders) incompatibility with `float64` types. These were resolved by setting `num_workers=0` and ensuring all tensors were cast to `float32`, though this slightly increased data loading times.
# * **Computational Cost:** Training the model from scratch took approximately 21 minutes. This highlights the high computational cost of training deep networks without transfer learning.
# 
# **Reflection on Architecture:**
# The chosen architecture (5-layer "Pyramid" structure doubling filters from 32 to 512) proved to be highly effective. The depth allowed for sufficient hierarchical feature extraction, capturing both low-level edges and high-level object shapes. The use of the **Adam optimizer** with a learning rate of 0.001 provided stable convergence, preventing the loss from oscillating. Overall, this custom implementation serves as a strong baseline before exploring Transfer Learning methods.

# %% [markdown]
# ### 1.8 Kaggle Test Result (5 points)
# ***0.926***

# %% [markdown]
# ## 2. Exploring Transfer Learning with ResNet50 and MobileNet (60 points)

# %% [markdown]
# ### 2.1. Introduction
# 
# In this section, we explore **Transfer Learning**, a technique where a model developed for a task is reused as the starting point for a model on a second task. Specifically, we utilize **ResNet-18**, a convolutional neural network pre-trained on the ImageNet dataset.
# 
# **What is Fine-tuning?**
# Fine-tuning involves taking a pre-trained network and retraining some or all of its layers on a new dataset. This allows the model to adjust its pre-learned features (like edge detection or texture recognition) to the specific characteristics of our Vegetable dataset.
# 
# **Why Transfer Learning?**
# 1.  **Efficiency:** Training deep networks from scratch requires massive amounts of data and computational power. Pre-trained models have already learned rich feature representations.
# 2.  **Performance:** Even with limited data, transfer learning often achieves higher accuracy than models trained from scratch because the starting weights are already optimized for visual recognition.
# 
# **Freezing Layers:**
# We freeze the initial layers because they learn generic features (lines, curves) that are applicable to almost any image. By training only the last layers (Fully Connected), we adapt the model to classify our specific 15 vegetable classes without destroying the useful features learned from ImageNet.

# %% [markdown]
# ### 2.2. Load the pre-trained ResNet18 model (3 points)
# 

# %%
## Utilize torchvision library to load the pre-trained ResNet50 model
## Ensure that the model's architecture matches ResNet50, by checking the model summary.

# Helper Function: Generic Training Loop for Transfer Learning

def train_transfer_model(model, criterion, optimizer, num_epochs=15, model_name="Model"):
    
    since = time.time()

    best_model_wts = copy.deepcopy(model.state_dict())
    best_acc = 0.0
    
    # Store history
    history = {'train_loss': [], 'train_acc': [], 'val_loss': [], 'val_acc': []}

    print(f"Training {model_name} on {device} for {num_epochs} epochs...")

    for epoch in range(num_epochs):
        # Each epoch has a training and validation phase
        for phase in ['train', 'val']:
            if phase == 'train':
                model.train()  # Set model to training mode
                dataloader = train_loader
            else:
                model.eval()   # Set model to evaluate mode
                dataloader = val_loader

            running_loss = 0.0
            running_corrects = 0

            # Iterate over data
            for inputs, labels in dataloader:
                inputs = inputs.to(device)
                labels = labels.to(device)

                # Zero the parameter gradients
                optimizer.zero_grad()

                # Forward
                # Track history if only in train
                with torch.set_grad_enabled(phase == 'train'):
                    outputs = model(inputs)
                    loss = criterion(outputs, labels)
                    _, preds = torch.max(outputs, 1)

                    # Backward + optimize only if in training phase
                    if phase == 'train':
                        loss.backward()
                        optimizer.step()

                # Statistics
                running_loss += loss.item() * inputs.size(0)
                running_corrects += torch.sum(preds == labels.data)

            epoch_loss = running_loss / len(dataloader.dataset)
            epoch_acc = running_corrects.float() / len(dataloader.dataset)

            print(f'Epoch {epoch+1}/{num_epochs} | {phase.upper()} Loss: {epoch_loss:.4f} Acc: {epoch_acc:.4f}')

            # Save history
            if phase == 'train':
                history['train_loss'].append(epoch_loss)
                history['train_acc'].append(epoch_acc.item())
            else:
                history['val_loss'].append(epoch_loss)
                history['val_acc'].append(epoch_acc.item())

                # Deep copy the model
                if epoch_acc > best_acc:
                    best_acc = epoch_acc
                    best_model_wts = copy.deepcopy(model.state_dict())

    time_elapsed = time.time() - since
    print(f'\n{model_name} training complete in {time_elapsed // 60:.0f}m {time_elapsed % 60:.0f}s')
    print(f'Best val Acc: {best_acc:.4f}')

    # Load best model weights
    model.load_state_dict(best_model_wts)
    return model, history

# %%
## Model 1: Base ResNet-18 (Train Only FC Layer)

# 1. Load Pre-trained ResNet18
# Using 'DEFAULT' weights automatically loads the best available pre-trained weights from ImageNet
resnet_base = models.resnet18(weights=models.ResNet18_Weights.DEFAULT)

# %% [markdown]
# ### 2.3 Modify the ResNet18 model for transfer learning (15 points)

# %%
## Freeze all layers of the ResNet18 model.
## Replace the final fully connected layer with a new FC layer matching the number of classes
## Unfreeze the final FC layer
## Define appropriate loss function and optimizer for training
## Train the modified ResNet18 model on the vegetable image dataset. (base model)

# 2. Freeze All Layers
# We set requires_grad = False to prevent weights from being updated during backprop
for param in resnet_base.parameters():
    param.requires_grad = False

# 3. Replace Final FC Layer
# ResNet18's last layer is named 'fc'. We replace it with a new Linear layer.
# This new layer automatically has requires_grad = True.
num_ftrs = resnet_base.fc.in_features
resnet_base.fc = nn.Linear(num_ftrs, 15) # 15 Classes

# Move to device (GPU/MPS)
resnet_base = resnet_base.to(device)

# 4. Define Loss and Optimizer
# We only optimize the parameters of the final layer (resnet_base.fc.parameters())
criterion = nn.CrossEntropyLoss()
optimizer_base = optim.Adam(resnet_base.fc.parameters(), lr=0.001)

# 5. Train Model 1
resnet_base, history_base = train_transfer_model(
    resnet_base, criterion, optimizer_base, num_epochs=15, model_name="ResNet18_Base"
)

# %%
## Define another ResNet18 model
## Freeze all layers of the ResNet18 model.
## Replace the final fully connected layer with a new FC layer matching the number of classes
## Unfreeze the final FC layer
## Unfreeze convolutional layers 3 and 4 of the ResNet18 model and again proceed with training. (second model)

## Model 2: ResNet-18 (Fine-tune Layer 3, 4 and FC) 

# 1. Load Fresh Pre-trained Model
resnet_model2 = models.resnet18(weights=models.ResNet18_Weights.DEFAULT)

# 2. Setup Freezing
# First freeze everything
for param in resnet_model2.parameters():
    param.requires_grad = False

# Unfreeze Layer 3 and Layer 4
for param in resnet_model2.layer3.parameters():
    param.requires_grad = True
for param in resnet_model2.layer4.parameters():
    param.requires_grad = True

# 3. Replace FC Layer
num_ftrs = resnet_model2.fc.in_features
resnet_model2.fc = nn.Linear(num_ftrs, 15)

resnet_model2 = resnet_model2.to(device)

# 4. Optimizer
# Important: Since we are training more layers, we usually lower the learning rate 
optimizer_model2 = optim.Adam(filter(lambda p: p.requires_grad, resnet_model2.parameters()), lr=0.0001)

# 5. Train
resnet_model2, history_model2 = train_transfer_model(
    resnet_model2, criterion, optimizer_model2, num_epochs=15, model_name="ResNet18_Model2"
)

# %%
## Define another ResNet18 model
## Replace the final fully connected layer with a new FC layer matching the number of classes proceed with training. (third model)

## Model 3: ResNet-18 (Fine-tune All Layers) 

# 1. Load Fresh Model
resnet_model3 = models.resnet18(weights=models.ResNet18_Weights.DEFAULT)

# 2. No Freezing (All params requires_grad = True by default)
# Just replace the FC layer
num_ftrs = resnet_model3.fc.in_features
resnet_model3.fc = nn.Linear(num_ftrs, 15)

resnet_model3 = resnet_model3.to(device)

# 3. Optimizer
# Very low learning rate for full network fine-tuning to prevent destroying pre-learned features
optimizer_model3 = optim.Adam(resnet_model3.parameters(), lr=1e-5)

# 4. Train
resnet_model3, history_model3 = train_transfer_model(
    resnet_model3, criterion, optimizer_model3, num_epochs=15, model_name="ResNet18_Full"
)

# %%
## Define appropriate loss function and optimizer for training
## Iterate over the training dataset in mini-batches, compute the loss, and update model parameters.
## Monitor the training process and evaluate all model's performance on the validation set periodically.
## Visualize the accuracy and loss changes of the models across training and validation datasets.

## Compute metrics such as accuracy, precision, recall, and F1-score to assess classification performance.
## Visualize confusion matrix to understand the model's behavior across different classes

# ==========================================
# Visualize Comparison (Accuracy & Loss)
# ==========================================

plt.figure(figsize=(12, 10))

# 1. Validation Accuracy Comparison
plt.subplot(2, 1, 1)
plt.plot(history_base['val_acc'], label='Base (FC Only)')
plt.plot(history_model2['val_acc'], label='Model 2 (L3+L4+FC)')
plt.plot(history_model3['val_acc'], label='Model 3 (Full)')
plt.title('Validation Accuracy Comparison: ResNet18 Models')
plt.ylabel('Accuracy')
plt.xlabel('Epochs')
plt.legend()
plt.grid(True)

# 2. Validation Loss Comparison
plt.subplot(2, 1, 2)
plt.plot(history_base['val_loss'], label='Base (FC Only)', linestyle='--')
plt.plot(history_model2['val_loss'], label='Model 2 (L3+L4+FC)', linestyle='--')
plt.plot(history_model3['val_loss'], label='Model 3 (Full)', linestyle='--')
plt.title('Validation Loss Comparison: ResNet18 Models')
plt.ylabel('Loss')
plt.xlabel('Epochs')
plt.legend()
plt.grid(True)

plt.tight_layout()
plt.show()

# %%
# ==================================================================
# Visualizing Confusion Matrices for ALL ResNet Models (Vertical Layout)
# ==================================================================

from sklearn.metrics import confusion_matrix
import seaborn as sns
import matplotlib.pyplot as plt

# List of models to analyze
models_list = [
    (resnet_base, "ResNet Base (FC Only)"),
    (resnet_model2, "ResNet Model 2 (L3+L4)"),
    (resnet_model3, "ResNet Full")
]

# Create 3 subplots arranged vertically (3 rows, 1 column)
# Increased height (24) to accommodate stacked plots
fig, axes = plt.subplots(3, 1, figsize=(12, 24)) 

print("Computing confusion matrices...")

for idx, (model, model_name) in enumerate(models_list):
    model.eval()
    all_preds = []
    all_labels = []
    
    # Get predictions
    with torch.no_grad():
        for inputs, labels in val_loader:
            inputs = inputs.to(device)
            labels = labels.to(device)
            outputs = model(inputs)
            _, preds = torch.max(outputs, 1)
            all_preds.extend(preds.cpu().numpy())
            all_labels.extend(labels.cpu().numpy())
    
    # Compute the matrix
    cm = confusion_matrix(all_labels, all_preds)
    
    # Plot (inside subplot)
    # axes[idx] refers to the specific row
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', ax=axes[idx],
                xticklabels=train_dataset.classes,
                yticklabels=train_dataset.classes)
    
    # Set titles and labels
    axes[idx].set_title(f'{model_name}\nAcc: {accuracy_score(all_labels, all_preds):.4f}')
    axes[idx].set_xlabel('Predicted Label')
    axes[idx].set_ylabel('True Label')
    axes[idx].tick_params(axis='x', rotation=45)

# Adjust layout to prevent overlap
plt.tight_layout()
plt.show()

# %% [markdown]
# ### Comparative Analysis of ResNet-18 Models
# 
# **Performance Overview and Model Selection:**
# We conducted a comparative study of three transfer learning strategies using the ResNet-18 architecture.
# * **Model 1 (Base - FC Only):** Achieved validation accuracy of **99.60%**. By training only the final classification layer, the model successfully leveraged generic ImageNet features.
# * **Model 2 (Fine-tune Blocks 3, 4 & FC):** Achieved a perfect validation accuracy of **100.00%**. Unfreezing the deeper convolutional layers allowed the model to adapt high-level features specifically for vegetable classification.
# * **Model 3 (Full Fine-tuning):** Also achieved **100.00%** accuracy. While effective, training the entire network did not yield a performance gain over Model 2.
# 
# **Selection:** **ResNet Model 2** is selected as the best model. It achieves the theoretical maximum accuracy (100%) while optimizing fewer parameters than Model 3, representing the optimal balance between performance and efficiency.
# 
# **Why did Model 2 perform better?**
# Model 2 performed better than the Base Model because of **domain adaptation**. While the early layers of a CNN capture universal features like edges and curves (which do not need retraining), the deeper layers (Blocks 3 and 4 in ResNet) capture high-level semantic information like shapes and object parts. By unfreezing these specific layers, we allowed the model to adjust its pre-learned "generic object" representations to learn the specific nuances of the 15 vegetable classes (e.g., distinguishing the texture of a *Bitter Gourd* from a *Cucumber*). This provided the necessary flexibility to correct the minor errors made by the Base Model.
# 
# **Practical Considerations:**
# * **Computational Efficiency:** Model 1 is the most computationally efficient as it requires updating the fewest parameters, resulting in the fastest training times. Model 3 is the most expensive, requiring gradient computation for the entire deep network. Model 2 strikes a balance, offering perfect accuracy without the full computational overhead of Model 3.
# * **Risk of Overfitting:** In scenarios with limited data (like our 3000 training images), full fine-tuning (Model 3) carries a higher risk of overfitting or "catastrophic forgetting," where the model loses useful pre-trained features. Freezing the early layers (as in Models 1 and 2) acts as a regularizer, stabilizing training.
# * **Resource Usage:** For deployment on resource-constrained devices, Model 1 would be preferred for its training speed, but for maximum accuracy in a production environment, Model 2 is the superior choice.

# %% [markdown]
# ### 2.4 Evaluate the fine-tuned ResNet18 model (5 points)

# %%
## Test the best model on the test set to evaluate its performance.
## Comment on the results
## Compare the fine-tuned ResNet18 model performance with the CNN model implemented from scratch

# ==========================================
# Evaluation & Kaggle Submission for Best ResNet Model
# ==========================================

# 1. Automatically Select the Best Model
# We compare the final validation accuracy of all 3 models
models_dict = {
    "ResNet Base": (resnet_base, history_base['val_acc'][-1]),
    "ResNet Model 2": (resnet_model2, history_model2['val_acc'][-1]),
    "ResNet Full": (resnet_model3, history_model3['val_acc'][-1])
}

# Find the model with the highest validation accuracy
best_model_name, (best_resnet_model, best_acc_val) = max(models_dict.items(), key=lambda item: item[1][1])

print(f"Best Model Selected: {best_model_name} with Validation Accuracy: {best_acc_val:.4f}")

# 2. Compute Metrics on VALIDATION Set (Since we have labels here)
best_resnet_model.eval()
all_preds = []
all_labels = []

print(f"Calculating detailed metrics on Validation Set...")
with torch.no_grad():
    for inputs, labels in val_loader:
        inputs = inputs.to(device)
        labels = labels.to(device)
        outputs = best_resnet_model(inputs)
        _, preds = torch.max(outputs, 1)
        all_preds.extend(preds.cpu().numpy())
        all_labels.extend(labels.cpu().numpy())

# Calculate Metrics
acc = accuracy_score(all_labels, all_preds)
prec = precision_score(all_labels, all_preds, average='weighted')
rec = recall_score(all_labels, all_preds, average='weighted')
f1 = f1_score(all_labels, all_preds, average='weighted')

print(f"\n--- Validation Metrics for {best_model_name} ---")
print(f"Accuracy:  {acc:.4f}")
print(f"Precision: {prec:.4f}")
print(f"Recall:    {rec:.4f}")
print(f"F1-Score:  {f1:.4f}")

# 3. Generate Predictions on TEST Set (For Kaggle)
print(f"\nGenerating Kaggle predictions on Test Set...")
test_preds = []
test_filenames = []

# Ensure we use the test_loader created with UnlabeledDataset
# (If test_loader isn't ready, verify Part 1 steps)
with torch.no_grad():
    for inputs, filenames in test_loader:
        inputs = inputs.to(device)
        outputs = best_resnet_model(inputs)
        _, preds = torch.max(outputs, 1)
        
        test_preds.extend(preds.cpu().numpy())
        test_filenames.extend(filenames)

# Create Submission DataFrame
idx_to_class = {v: k for k, v in train_dataset.class_to_idx.items()}
predicted_labels = [idx_to_class[p] for p in test_preds]

submission_df = pd.DataFrame({
    'Id': test_filenames,  # Capital 'Id' for Kaggle
    'Label': predicted_labels
})

# Save to CSV
csv_filename = 'submission_resnet_best.csv'
submission_df.to_csv(csv_filename, index=False)
print(f"✅ Submission file saved: '{csv_filename}'")
print(submission_df.head())

# %% [markdown]
# ### Comparison: Fine-tuned ResNet-18 vs. Custom CNN
# 
# **Performance Comparison:**
# * **Custom CNN (from scratch):** Achieved ~94% validation accuracy. While effective, it required longer training (30 epochs, ~21 mins) and showed signs of overfitting (gap between train/val accuracy).
# * **ResNet-18 (Transfer Learning):** The fine-tuned ResNet model (Model 2) achieved a perfect **100%** validation accuracy. This massive improvement (approx. +7%) highlights the power of transfer learning.
# 
# **Why the difference?**
# The Custom CNN had to learn how to identify edges, textures, and shapes from scratch using only 3,000 images. In contrast, ResNet-18 started with "knowledge" (weights) gained from 1.2 million images (ImageNet). The pre-trained filters in ResNet were already excellent at feature extraction, requiring only minor adjustments (fine-tuning) to classify our specific vegetables perfectly.
# 
# **Training Efficiency:**
# ResNet converged much faster. Even within the first few epochs, it surpassed the best accuracy of the Custom CNN. This makes transfer learning not only more accurate but also more computationally efficient for this task.

# %% [markdown]
# ### 2.5 Kaggle Test Result (5 points)
# ***0.993***

# %% [markdown]
# ### 2.7. Load the pre-trained MobileNet model (3 points)
# 

# %%
## Utilize torchvision library to load the pre-trained MobileNetV2 model
## Ensure that the model's architecture matches MobileNetV2, by checking the model summary.

# --- Model 1: MobileNet V2 (Base - Train Only FC) ---

# Load Pre-trained MobileNet
mobilenet_base = models.mobilenet_v2(weights=models.MobileNet_V2_Weights.DEFAULT)

# Print the architecture to verify
print(mobilenet_base)

# %% [markdown]
# ### 2.8 Modify the MobileNet model for transfer learning (12 points)

# %%
## Freeze all layers of the MobileNet model.
## Replace the final fully connected layer with a new FC layer matching the number of classes
## Unfreeze the final FC layer
## Define appropriate loss function and optimizer for training
## Train the modified MobileNet model on the vegetable image dataset. (base model)

# 2. Freeze All Layers
for param in mobilenet_base.parameters():
    param.requires_grad = False

# 3. Replace Classifier
# MobileNet's classifier is a Sequential block. The last layer is at index 1.
# Check structure: print(mobilenet_base.classifier) -> (0): Dropout, (1): Linear
num_ftrs = mobilenet_base.classifier[1].in_features
mobilenet_base.classifier[1] = nn.Linear(num_ftrs, 15)

mobilenet_base = mobilenet_base.to(device)

# 4. Optimizer & Loss
criterion = nn.CrossEntropyLoss()
optimizer_mobile_base = optim.Adam(mobilenet_base.classifier.parameters(), lr=0.001)

# 5. Train
print("\n--- Training MobileNet V2 (Base) ---")
mobilenet_base, history_mobile_base = train_transfer_model(
    mobilenet_base, criterion, optimizer_mobile_base, num_epochs=15, model_name="MobileNet_Base"
)

# %%
## Define another MobileNet model
## Replace the final fully connected layer with a new FC layer matching the number of classes proceed with training. (second model)

# --- Model 2: MobileNet V2 (Full Fine-tuning) ---

# 1. Load Fresh Model
mobilenet_full = models.mobilenet_v2(weights=models.MobileNet_V2_Weights.DEFAULT)

# 2. Replace Classifier (No freezing)
num_ftrs = mobilenet_full.classifier[1].in_features
mobilenet_full.classifier[1] = nn.Linear(num_ftrs, 15)

mobilenet_full = mobilenet_full.to(device)

# %%
## Define appropriate loss function and optimizer for training
## Iterate over the training dataset in mini-batches, compute the loss, and update model parameters.
## Monitor the training process and evaluate all model's performance on the validation set periodically.
## Visualize the accuracy and loss changes of the models across training and validation datasets.

## Compute metrics such as accuracy, precision, recall, and F1-score to assess classification performance.
## Visualize confusion matrix to understand the model's behavior across different classes

# 3. Optimizer (Lower learning rate for full training)
optimizer_mobile_full = optim.Adam(mobilenet_full.parameters(), lr=1e-5)

# 4. Train
print("\n--- Training MobileNet V2 (Full) ---")
mobilenet_full, history_mobile_full = train_transfer_model(
    mobilenet_full, criterion, optimizer_mobile_full, num_epochs=15, model_name="MobileNet_Full"
)

# %%
# Visualize Comparison for MobileNet (Accuracy & Loss)
plt.figure(figsize=(12, 10))

# 1. Validation Accuracy Comparison
plt.subplot(2, 1, 1) # 2 satır, 1 sütun, 1. grafik
plt.plot(history_mobile_base['val_acc'], label='MobileNet Base (FC Only)')
plt.plot(history_mobile_full['val_acc'], label='MobileNet Full (All Layers)')
plt.title('Validation Accuracy: MobileNet Models')
plt.ylabel('Accuracy')
plt.xlabel('Epochs')
plt.legend()
plt.grid(True)

# 2. Validation Loss Comparison
plt.subplot(2, 1, 2) # 2 satır, 1 sütun, 2. grafik
plt.plot(history_mobile_base['val_loss'], label='MobileNet Base (FC Only)', linestyle='--')
plt.plot(history_mobile_full['val_loss'], label='MobileNet Full (All Layers)', linestyle='--')
plt.title('Validation Loss: MobileNet Models')
plt.ylabel('Loss')
plt.xlabel('Epochs')
plt.legend()
plt.grid(True)

plt.tight_layout()
plt.show()

# %% [markdown]
# ### Comparative Analysis of MobileNetV2 Models
# 
# **Performance Overview and Model Selection:**
# We conducted a comparative study of two transfer learning strategies using the MobileNetV2 architecture:
# * **Model 1 (Base - FC Only):** Utilized MobileNetV2 as a fixed feature extractor, training only the final classification layer. This model achieved superior (or highly competitive) validation accuracy compared to the fully fine-tuned version.
# * **Model 2 (Full Fine-tuning):** Involved retraining all layers of the network. Despite the increased computational effort, this model did not outperform the Base model and likely suffered from overfitting due to the limited dataset size.
# 
# **Selection:** **MobileNetV2 (Base Model)** is selected as the best model. It achieved the highest validation accuracy while maintaining the original robustness of the pre-trained weights.
# 
# **Why did the Base Model perform better?**
# The Base Model performed better primarily due to the **size of our dataset** relative to the model's capacity.
# 1. **Prevention of Overfitting:** Our training set consists of only 3,000 images. When we unfreeze the entire network (Model 2), the model has millions of parameters to update. With limited data, the model tends to "memorize" the training examples rather than learning generalizable features (Overfitting), leading to lower validation performance.
# 2. **Robust Feature Extraction:** MobileNetV2 pre-trained on ImageNet has already learned excellent feature extractors (edges, textures, shapes). These features are generic enough to classify vegetables effectively without needing modification. Freezing these layers preserved this "knowledge," while the Full Fine-tuning likely distorted these well-learned weights ("Catastrophic Forgetting").
# 
# **Practical Considerations:**
# * **Training Efficiency:** The Base Model is drastically more efficient. It converges much faster because gradients are calculated for only one layer (the classifier) rather than the entire deep network. This saves significant GPU time and energy.
# * **Resource Usage:** For practical applications, the Base Model is preferred not just for its accuracy but for its stability. It requires less memory during the backward pass of training, making it feasible to train even on lower-end hardware.
# * **Conclusion:** For this specific task and dataset size, utilizing Transfer Learning as a **Feature Extraction** mechanism (Base Model) proved to be the optimal strategy over Full Fine-tuning.

# %% [markdown]
# ### 2.9 Evaluate the fine-tuned MobileNet model (5 points)

# %%
## Test the best model on the test set to evaluate its performance.
## Comment on the results

# ==========================================
# Test Evaluation & Kaggle Submission for MobileNet
# ==========================================

# 1. Select the Best Model
best_mobile_model = mobilenet_base
best_mobile_model.eval()

print(f"Selected Model: MobileNet V2 (Base)")

# 2. Generate Predictions on TEST Set
print(f"Generating Kaggle predictions on Test Set...")
test_preds = []
test_filenames = []

# Ensure test_loader is ready (from previous steps)
with torch.no_grad():
    for inputs, filenames in test_loader:
        inputs = inputs.to(device)
        outputs = best_mobile_model(inputs)
        _, preds = torch.max(outputs, 1)
        
        test_preds.extend(preds.cpu().numpy())
        test_filenames.extend(filenames)

# 3. Create Submission DataFrame
# Map indices back to class names
idx_to_class = {v: k for k, v in train_dataset.class_to_idx.items()}
predicted_labels = [idx_to_class[p] for p in test_preds]

submission_df = pd.DataFrame({
    'Id': test_filenames,
    'Label': predicted_labels
})

# Save to CSV
csv_filename = 'submission_mobilenet_best.csv'
submission_df.to_csv(csv_filename, index=False)
print(f"✅ Submission file saved: '{csv_filename}'")
print(submission_df.head())

# %% [markdown]
# ### Evaluation of MobileNetV2 Performance on Test Set
# 
# **Comparison with Other Models:**
# * **Vs. Custom CNN:** MobileNet (Base) drastically outperformed the Custom CNN (~93%) both in accuracy and training speed (~9 mins vs ~21 mins).
# * **Vs. ResNet-18:** MobileNet achieved similar high accuracy to ResNet-18 but with a much lighter architecture. While ResNet is powerful, MobileNet offers a better trade-off for resource-constrained environments (like mobile apps) without sacrificing significant accuracy on this specific dataset.

# %% [markdown]
# ### 2.10 Kaggle Test Result (5 points)
# ***0.988***

# %% [markdown]
# ## 3 Analyze advantages and disadvantages (7 points)
# 
# * Provide insights on the advantages and disadvantages of transfer learning vs. training from scratch
# * Put a table for comparison of the best 3 models that you have obtained in this assignment (CNN from scratch, ResNet18, MobileNet)
# * Discuss practical considerations when choosing between these approaches and models.
# 
# **1. Comparison of Transfer Learning vs. Training from Scratch:**
# 
# * **Training from Scratch:**
#     * **Advantages:** It provides complete control over the architecture, allowing the design to be tailored specifically to the input dimensions and nature of the data. It is beneficial when the target domain is drastically different from natural images (e.g., medical or spectral imagery) where pre-trained features might not apply.
#     * **Disadvantages:** It requires a significantly larger amount of labeled data to learn robust features. As observed in our experiment, it is computationally expensive and took the longest time (~21 mins) to reach a lower accuracy (93.3%) compared to pre-trained models. It is also highly prone to overfitting on small datasets like ours (3000 images).
# 
# * **Transfer Learning:**
#     * **Advantages:** This technique is extremely powerful when working with limited labeled data. By leveraging models pre-trained on ImageNet (millions of images), we utilize "generic features" (edges, textures) that are already learned. This results in much faster convergence and significantly higher accuracy.
#     * **Disadvantages:** The model architecture is constrained by the pre-trained network's design (e.g., input size requirements).
# 
# **2. Performance Comparison Table:**
# 
# | Model | Best Configuration | Validation Accuracy | Training Time (Approx.) | Computational Load |
# | :--- | :--- | :--- | :--- | :--- |
# | **Custom CNN** | 5-Layer Pyramid | 94.27% | ~21 mins | High (Full Backprop) |
# | **ResNet-18** | Fine-Tuning (L3+L4) | 99.87% | ~15 mins | Medium |
# | **MobileNetV2** | Base (Feature Ext.) | **100.00%** | **~9 mins** | **Low (Fastest)** |
# 
# **3. Practical Considerations for Model Selection:**
# 
# * **Dataset Size:** For small datasets (like this assignment's 4500 images), **Transfer Learning** is the clear winner. Training from scratch struggled to generalize perfectly, while pre-trained models achieved perfect accuracy effortlessly.
# * **Computational Resources & Deployment:**
#     * If deploying to **mobile or edge devices** (resource-constrained environments), **MobileNetV2** is the optimal choice. It achieved the same perfect accuracy as ResNet but with a lightweight architecture and faster training time.
#     * If computational power is available and the task requires adapting to new complex features, **ResNet-18 (Fine-tuned)** is a robust choice.
# * **Task Complexity:** For simple classification tasks where the domain resembles ImageNet (like vegetables), using a model as a **Fixed Feature Extractor** (MobileNet Base) is often sufficient and most efficient. Full Fine-tuning (ResNet Model 3) is computationally more expensive and may not yield better results if the data is scarce.


