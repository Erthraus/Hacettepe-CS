Image Classification with Custom CNN and Pre-trained EfficientNet

Description:
	This assignment contains the implementation of image classification using both a custom Convolutional Neural Network (CNN) architecture and a pre-trained EfficientNet model. The code includes data preprocessing, model training, evaluation, and analysis.

Dataset:
	The Animals-10 dataset consists of approximately 28,000 medium-quality animal images belonging to 10 categories, including dog, cat, horse, spider, butterfly, chicken, sheep, cow, squirrel, and elephant. Each category has varying image counts ranging from 2,000 to 5,000 units. I used 1000 images for training, 200 for validation and 200 for testing.

Running the Code:
- Organize the downloaded Animals-10 dataset into folders, each representing a different class.
- Adjust the `load_dataset()` function in the code to specify the desired number of images for training, validation, and testing.
- Start the ipynb code.

File Structure:
- `main.ipynb`: Main script for model training and evaluation.
- `README.txt`: Instructions and information about running the code.
- 'Report.pdf' : Analysing and conclusion of the assignment.

Functions:
load_dataset: Loads the dataset and preprocesses images.
train_model: Trains the model with specified hyperparameters.
evaluate_model: Evaluates the model on the test set.
plot_cm: Plots the confusion matrix.
test_different_models: Tests different configurations for the custom CNN model.
test_dropout: Tests different dropout values for the custom CNN model.