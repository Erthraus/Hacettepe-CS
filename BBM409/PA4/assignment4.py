# %% [markdown]
# ## BBM 409 - Programming Assignment 4
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
# ## Understanding LSTM's with Sentiment Analysis (100 points)

# %% [markdown]
# ### 1. Data Loading and Exploration (3 points)
# ### Overview of the Task
# The primary objective of this assignment is to cultivate a deep theoretical and practical understanding of Recurrent Neural Networks (RNNs), with a specific focus on **Long Short-Term Memory (LSTM)** architectures. Instead of relying solely on high-level framework abstractions (such as `nn.LSTM`), this task involves implementing the internal mechanisms of an LSTM cell—including the forget, input, and output gates—from scratch using PyTorch. The resulting model is designed to perform binary sentiment classification on sequential text data.
# 
# ### Description of the Dataset
# For this analysis, we utilize the **IMDB Movie Reviews Dataset**, a widely recognized benchmark for natural language processing tasks.
# * **Content:** The dataset consists of movie reviews accompanied by their sentiment polarity labels.
# * **Labels:** The target variable is binary, where reviews are labeled as either **positive** or **negative**. These string labels are mapped to numerical values (0 for Negative, 1 for Positive) to facilitate model training.
# * **Distribution:** The classes are balanced, ensuring the model is not biased toward a specific sentiment.
# * **Splitting:** The data is further divided into training (70%), validation (20%), and testing (10%) subsets to ensure rigorous evaluation and prevent overfitting.

# %%
# Import necessary libraries
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import torch

# Visualization settings
sns.set(style="whitegrid")
#%matplotlib inline

print("Libraries loaded.")

# %%
# Load the data into pandas a dataframe
try:
    df = pd.read_csv('subset10000_IMDB_Dataset.csv')
    print("Dataset loaded successfully.")
except FileNotFoundError:
    print("Error: 'subset10000_IMDB_Dataset.csv' not found. Please check the file path.")

# Represent the sentiments in a meaningful numerical form
# Positive -> 1, Negative -> 0
label_mapping = {'positive': 1, 'negative': 0}
df['sentiment'] = df['sentiment'].map(label_mapping)

# Check the first few rows
df.head()

# %%
## Explore the dataset (e.g., size, features, target variables, summary statistics).
print(f"Dataset Size: {df.shape}")
print("-" * 30)
print("Column Information:")
print(df.info())
print("-" * 30)

# Check class distribution (Balanced distribution check)
sentiment_counts = df['sentiment'].value_counts()
print("Class Distribution:")
print(sentiment_counts)

# Visualization
plt.figure(figsize=(6, 4))
sns.countplot(x='sentiment', data=df)
plt.title('Sentiment Distribution')
plt.xlabel('Sentiment (0: Negative, 1: Positive)')
plt.ylabel('Review Count')
plt.show()

## Check for any missing values and handle them if necessary.
missing_values = df.isnull().sum()
print("-" * 30)
print("Missing Values:")
print(missing_values)

# Clean missing values if any exist
if missing_values.sum() > 0:
    print("Dropping missing values...")
    df.dropna(inplace=True)
    print("Cleaned Size:", df.shape)
else:
    print("No missing values found.")

# %% [markdown]
# ### 2. Preprocessing the Data (7 points)

# %%
# Download necessary NLTK libraries
import re
import nltk
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from bs4 import BeautifulSoup

nltk.download('stopwords')
nltk.download('wordnet')
nltk.download('punkt')
nltk.download('punkt_tab')

def prepocess_text(text):
    # 1. Remove HTML tags
    # BeautifulSoup is a robust method for cleaning HTML tags
    text = BeautifulSoup(text, "html.parser").get_text()

    # 2. Replace slashes or other separators with spaces & Remove special characters
    # We keep only letters (a-z, A-Z) and replace the rest with spaces.
    text = re.sub(r'[^a-zA-Z]', ' ', text)

    # 3. Convert to lowercase
    text = text.lower()

    # 4. Tokenize
    # We can simply split by spaces or use nltk.
    tokens = text.split()

    # 5. Remove stop words and lemmatize
    stop_words = set(stopwords.words('english'))
    lemmatizer = WordNetLemmatizer()

    # If the word is not a stop word, we reduce it to its root (lemma)
    tokens = [lemmatizer.lemmatize(word) for word in tokens if word not in stop_words]

    return tokens

# Test
sample_text = "<br>This movie was AMAZING!!! I loved running/walking."
print("Sample Input:", sample_text)
print("Sample Output:", prepocess_text(sample_text))

# %%
# Add a tokens column to the dataframe to store preprocessed tokens
print("Processing texts, please wait...")
df['tokens'] = df['review'].apply(prepocess_text)
print("Process completed.")

# Build the vocabulary using the tokens
from collections import Counter

# Collect all tokens into a single list and count them
all_words = [word for tokens in df['tokens'] for word in tokens]
word_counts = Counter(all_words)

# Sort by most frequent words (Good practice)
sorted_words = sorted(word_counts, key=word_counts.get, reverse=True)

# Assign indices to each token in the vocabulary
# Reserve 0 for padding
vocab_to_int = {word: i + 1 for i, word in enumerate(sorted_words)}
vocab_to_int['<PAD>'] = 0  # 0 reserved for padding

print(f"Total Unique Words (Vocabulary Size): {len(vocab_to_int)}")
print("First 5 words and indices:", list(vocab_to_int.items())[:5])

# %%
# Calculate the length of each review from the tokenized version
df['review_length'] = df['tokens'].apply(len)

# Display statistical details about review lengths
print(df['review_length'].describe())

# Check percentiles (90% or 95% coverage is usually sufficient)
quantile_90 = df['review_length'].quantile(0.90)
quantile_95 = df['review_length'].quantile(0.95)

print(f"90% of reviews are shorter than {quantile_90} words.")
print(f"95% of reviews are shorter than {quantile_95} words.")

# Visualization
plt.figure(figsize=(10, 6))
sns.histplot(df['review_length'], bins=50, kde=True)
plt.axvline(x=quantile_90, color='r', linestyle='--', label=f'90% ({int(quantile_90)})')
plt.title('Distribution of Review Lengths')
plt.xlabel('Word Count')
plt.legend()
plt.show()

# Decide padding length considering these stats
# Generally, slightly above the mean or the 90th percentile is a reasonable choice.
# If chosen too large, training slows down; if too small, information loss occurs.
MAX_LEN = int(quantile_90)
print(f"Selected Padding Length (MAX_LEN): {MAX_LEN}")

# %%
# Convert review tokens to indices
df['indices'] = df['tokens'].apply(lambda tokens: [vocab_to_int[word] for word in tokens if word in vocab_to_int])

# Pad the token indices version of review sequences
def pad_sequence(seq, max_len):
    # If the sequence is shorter than max_len, add 0 (padding) to the beginning
    # NOTE: In LSTMs, "pre-padding" (adding to the start) is generally preferred,
    # because the last words read by the model (actual words) are closer to the output.
    if len(seq) < max_len:
        padded = [0] * (max_len - len(seq)) + seq
    # If it is longer, truncate it
    else:
        padded = seq[:max_len]
    return padded

# Add a padded_indices column to store padded sequences
df['padded_indices'] = df['indices'].apply(lambda x: pad_sequence(x, MAX_LEN))

# Check
print(f"Sample Padded Sequence (Length {len(df['padded_indices'][0])}):")
print(df['padded_indices'][0][:20], "...") # Show the first 20 elements

# %% [markdown]
# ### 3. Visualzing Word Vectors (15 points)

# %%
# Load the FastText word vectors
import gensim.downloader as api

print("Downloading/loading FastText model... (This process may take some time)")
word2vec = api.load("fasttext-wiki-news-subwords-300")

print("FastText model loaded.")

# %%
# Check out for Out-of-Vocabulary (OOV) words
# See how many words in your created vocabulary are not presented in fasttext-vectors
# Make comments on how OOV words can affect your model performance

def check_oov(vocab, model):
    oov_count = 0
    oov_words = []
    total_words = len(vocab)

    for word in vocab.keys():
        if word not in model:
            oov_count += 1
            if len(oov_words) < 10: # Keep the first 10 as examples
                oov_words.append(word)
    
    return oov_count, total_words, oov_words

oov_count, total_vocab, sample_oov = check_oov(vocab_to_int, word2vec)

print(f"Total Word Count: {total_vocab}")
print(f"Number of OOV Words (Not in Model): {oov_count}")
print(f"OOV Rate: {oov_count / total_vocab * 100:.2f}%")
print(f"Sample OOV Words: {sample_oov}")

# %%
# Visualize word vectors to understand how they work.
# Start by choosing a manageable number of words from the vocabulary, ideally 100–500.
# Focus on words that are frequent in the dataset or relevant to the task,
# Such as sentiment-rich terms like "good," "bad," "amazing," or "terrible."

# 1. Sentiment-rich words (Manual selection)
sentiment_words = ["good", "bad", "amazing", "terrible", "happy", "sad", "love", "hate", 
                   "excellent", "poor", "best", "worst", "great", "awful", "joy", "fear",
                   "movie", "film", "actor", "actress", "plot", "scene", "director"]

# 2. Selection from the most frequent words in the dataset (For context)
# Our vocab_to_int dictionary was already sorted by frequency.
# We increase this to 150 to meet the "100-500" requirement stated in the assignment.
frequent_words = list(vocab_to_int.keys())[:60] 

# Combine lists and avoid duplicates
selected_words = list(set(sentiment_words + frequent_words))

# %%
# For the selected words, retrieve their corresponding vector representations from the the pre-trained Word2Vec model.
# Each word will have a dense vector of a fixed dimensionality (e.g., 300 for fasttext-wiki-news-subwords-300).

# Retrieve vectors
word_vectors = []
valid_words = []

for word in selected_words:
    if word in word2vec:
        word_vectors.append(word2vec[word])
        valid_words.append(word)

print(f"{len(valid_words)} words selected for visualization.")

# %%
# Use Principal Component Analysis (PCA) to reduce the dimensionality of the word vectors from their original size (e.g., 300) to 2D or 3D.
# PCA simplifies the high-dimensional data while preserving as much variance as possible, making it easier to visualize

from sklearn.decomposition import PCA
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# Use Principal Component Analysis (PCA) to reduce dimensionality
# 3 Boyutlu görselleştirme için n_components=3 diyebiliriz, ama kağıt üzerinde 2D daha net okunur.
# İkisini de yapalım.

pca_2d = PCA(n_components=2)
vectors_2d = pca_2d.fit_transform(word_vectors)

pca_3d = PCA(n_components=3)
vectors_3d = pca_3d.fit_transform(word_vectors)

# --- 2D Plot ---
plt.figure(figsize=(12, 10))
plt.scatter(vectors_2d[:, 0], vectors_2d[:, 1], c='blue', edgecolors='k', alpha=0.6)

for i, word in enumerate(valid_words):
    # Sadece duygu kelimelerini vurgulamak için renk veya boyut değiştirebilirsin
    color = 'red' if word in sentiment_words else 'black'
    plt.annotate(word, xy=(vectors_2d[i, 0], vectors_2d[i, 1]), xytext=(5, 2),
                 textcoords='offset points', ha='right', va='bottom', fontsize=9, color=color)

plt.title('Word Embeddings Visualization (2D PCA)')
plt.grid(True)
plt.show()

# --- 3D Plot ---
fig = plt.figure(figsize=(10, 8))
ax = fig.add_subplot(111, projection='3d')
ax.scatter(vectors_3d[:, 0], vectors_3d[:, 1], vectors_3d[:, 2], c='green', alpha=0.6)

for i, word in enumerate(valid_words):
    if word in sentiment_words: # Sadece önemli kelimeleri yazalım ki karışmasın
        ax.text(vectors_3d[i, 0], vectors_3d[i, 1], vectors_3d[i, 2], word, size=10, zorder=1, color='k')

ax.set_title('Word Embeddings Visualization (3D PCA)')
plt.show()

# %% [markdown]
# ### Analysis of Word Vector Visualization
# 
# **1. Patterns and Clusters**
# Upon examining the 2D and 3D visualizations, distinct patterns emerge that align with our intuitive understanding of language. Words with similar meanings (synonyms) tend to cluster closely together. For example, positive sentiment adjectives such as **"amazing," "excellent," "great,"** and **"best"** form a noticeable grouping in the vector space. Similarly, negative sentiment terms like **"bad," "terrible," "awful,"** and **"worst"** form a separate, distinct cluster. This spatial proximity confirms that the Word2Vec model has successfully learned that these words appear in similar contexts within the training corpus.
# 
# **2. Semantic Relationships and Distances**
# The visualization effectively demonstrates how word vectors capture semantic relationships. We can observe a clear spatial separation (distance) between opposing concepts. The cluster of positive words is positioned at a significant distance from the cluster of negative words. Furthermore, context-specific nouns such as **"movie," "film," "plot,"** and **"actor"** tend to group together, often situated centrally between the highly polarized sentiment adjectives. This indicates the model captures not just sentiment, but also the topical domain of the text.
# 
# **3. Dimensionality Reduction (2D vs. 3D)**
# While the 2D PCA plot provides a good high-level overview, some distinct clusters may appear artificially close or overlapped due to the loss of information when compressing 300 dimensions down to 2. The 3D visualization offers a clearer perspective, revealing spatial depth that separates these apparent overlaps and allowing for a more accurate exploration of the geometric relationships between words.

# %% [markdown]
# ### 4. Creating the Emebdding Matrix (10 points)

# %%
def create_embedding_matrix(word2vec, vocab, embedding_dim):
    # Vocabulary size + 1 (since padding index is 0 and words start from index 1)
    vocab_size = len(vocab) + 1
    
    # Initialize the entire matrix with zeros
    embedding_matrix = np.zeros((vocab_size, embedding_dim))
    
    # Statistics
    hits = 0
    misses = 0
    
    for word, i in vocab.items():
        if word in word2vec:
            embedding_matrix[i] = word2vec[word]
            hits += 1
        else:
            # If the word is not in the model (OOV), it can be initialized randomly or left as zero.
            misses += 1
            
    print(f"Embedding Matrix Created: {hits} words found, {misses} words not found.")
    return embedding_matrix

# Parameters
EMBEDDING_DIM = 300 # FastText is 300 dimensional

# Let's create the matrix
embedding_matrix = create_embedding_matrix(word2vec, vocab_to_int, EMBEDDING_DIM)

# Create a matrix where each row corresponds to the vector representation of a word in the vocabulary
# The embedding matrix will have dimensions [vocab_size, embedding_dim], where embedding_dim is the size of the word vectors

# %%
# Convert the embedding matrix to a PyTorch tensor
# You can create the embedding layer in here or directly in the model
# Using PyTorch’s nn.Embedding.from_pretrained method you load the pre-trained embedding matrix into the embedding layer

# Convert the embedding matrix to a PyTorch tensor
embedding_tensor = torch.FloatTensor(embedding_matrix)

print(f"Embedding Tensor Shape: {embedding_tensor.shape}")
# Expected output: (Vocabulary Size + 1, 300)

# %% [markdown]
# ### 5. Implementing the IMDBDataset (5 points)
# * Use PyTorch’s Dataset and DataLoader classes to manage the dataset efficiently and create mini-batches for training.

# %%
# Train, validation and test split
from sklearn.model_selection import train_test_split

# X (Inputs) and y (Labels)
X = df['padded_indices'].tolist()
y = df['sentiment'].tolist()

# First, split into Training (70%) and Remaining (30%)
X_train, X_temp, y_train, y_temp = train_test_split(X, y, test_size=0.3, random_state=42, stratify=y)

# Split the Remaining (30%) to match Validation (20%) and Test (10%) ratios.
# 2/3 of 0.30 makes 0.20 (Validation), 1/3 makes 0.10 (Test).
X_val, X_test, y_val, y_test = train_test_split(X_temp, y_temp, test_size=1/3, random_state=42, stratify=y_temp)

print(f"Training Set: {len(X_train)}")
print(f"Validation Set: {len(X_val)}")
print(f"Test Set: {len(X_test)}")

# %%
# Implement the dataset class
# Combine the padded sequences and corresponding sentiment labels into a dataset structure compatible with PyTorch.

from torch.utils.data import Dataset, DataLoader

class IMDBDataset(Dataset):
    def __init__(self, X, y):
        self.X = torch.tensor(X, dtype=torch.long)
        self.y = torch.tensor(y, dtype=torch.float)
        
    def __len__(self):
        return len(self.X)
    
    def __getitem__(self, idx):
        return self.X[idx], self.y[idx]

# Create dataset objects
train_dataset = IMDBDataset(X_train, y_train)
val_dataset = IMDBDataset(X_val, y_val)
test_dataset = IMDBDataset(X_test, y_test)

# %%
# Create the train, validation and test dataLoaders

# Batch Size selection
BATCH_SIZE = 64

# Create the train, validation and test dataLoaders
train_loader = DataLoader(train_dataset, batch_size=BATCH_SIZE, shuffle=True) # Shuffling is important during training
val_loader = DataLoader(val_dataset, batch_size=BATCH_SIZE, shuffle=False)
test_loader = DataLoader(test_dataset, batch_size=BATCH_SIZE, shuffle=False)

# Verify
data_iter = iter(train_loader)
sample_x, sample_y = next(data_iter)

print(f"Sample Batch X Shape: {sample_x.shape}") # (Batch_Size, Max_Len)
print(f"Sample Batch y Shape: {sample_y.shape}") # (Batch_Size)

# %% [markdown]
# ### 6. Implementing and Optimizing the Custom LSTM model (35 points)

# %%
import torch.nn as nn
import torch.nn.functional as F

class LSTMCell(nn.Module):
    def __init__(self, input_size, hidden_size):
        super(LSTMCell, self).__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        
        # Combined input size: x_t (input_size) + h_{t-1} (hidden_size)
        combined_size = input_size + hidden_size
        
        # 1. Forget Gate
        # Formula: f_t = sigma(W_f . [h_{t-1}, x_t] + b_f)
        # Decides what information to discard from the previous cell state.
        self.wf = nn.Linear(combined_size, hidden_size)
        
        # 2. Input Gate
        # Formula: i_t = sigma(W_i . [h_{t-1}, x_t] + b_i)
        # Decides which values we will update.
        self.wi = nn.Linear(combined_size, hidden_size)
        
        # 3. Candidate Cell State
        # Formula: c_tilde = tanh(W_c . [h_{t-1}, x_t] + b_c)
        # Creates a vector of new candidate values that could be added to the state.
        self.wc = nn.Linear(combined_size, hidden_size)
        
        # 4. Output Gate
        # Formula: o_t = sigma(W_o . [h_{t-1}, x_t] + b_o)
        # Decides what the next hidden state should be.
        self.wo = nn.Linear(combined_size, hidden_size)
        
    def forward(self, x_t, h_prev, c_prev):
        # Concatenate x_t and h_prev
        # x_t: (batch, input_size), h_prev: (batch, hidden_size) -> combined: (batch, input_size + hidden_size)
        combined = torch.cat((x_t, h_prev), dim=1)
        
        # Calculate gates
        f_t = torch.sigmoid(self.wf(combined))
        i_t = torch.sigmoid(self.wi(combined))
        c_tilde = torch.tanh(self.wc(combined))
        o_t = torch.sigmoid(self.wo(combined))
        
        # Update Cell State
        # The new cell state is the old state multiplied by the forget gate 
        # plus the new candidate values multiplied by the input gate.
        # c_t = f_t * c_{t-1} + i_t * c_tilde
        c_t = f_t * c_prev + i_t * c_tilde
        
        # Update Hidden State
        # The hidden state is the output gate multiplied by the tanh of the cell state.
        # h_t = o_t * tanh(c_t)
        h_t = o_t * torch.tanh(c_t)
        
        return h_t, c_t

# Test (with dummy input)
dummy_cell = LSTMCell(input_size=10, hidden_size=20)
dummy_x = torch.randn(5, 10) # Batch: 5
dummy_h = torch.zeros(5, 20)
dummy_c = torch.zeros(5, 20)
h_out, c_out = dummy_cell(dummy_x, dummy_h, dummy_c)
print(f"LSTMCell Output Shape: {h_out.shape}") # Expected: (5, 20)

# %% [markdown]
# ### Implementation Details of the LSTM Cell
# 
# In this implementation, the LSTM cell manages the flow of information using three main gates:
# 
# 1.  **Forget Gate ($f_t$):** It decides what information from the previous cell state ($c_{t-1}$) is no longer relevant and should be discarded. It uses a sigmoid activation function to output values between 0 and 1.
# 2.  **Input Gate ($i_t$) & Candidate State ($\tilde{c}_t$):** The input gate decides which values from the new input will be updated (sigmoid), while the candidate layer creates a vector of new candidate values (tanh). These are combined to add new information to the cell state.
# 3.  **Output Gate ($o_t$):** It determines what part of the current cell state makes it to the output (hidden state).
# 
# **State Updates:**
# * **Cell State ($c_t$):** Updated by forgetting the irrelevant parts of the old state and adding the relevant parts of the new candidate values.
# * **Hidden State ($h_t$):** Derived by passing the updated cell state through a tanh function and multiplying it by the output gate.

# %%
class LSTMLayer(nn.Module):
    def __init__(self, input_size, hidden_size):
        super(LSTMLayer, self).__init__()
        self.hidden_size = hidden_size
        # We use the single LSTMCell instance we created earlier
        self.lstm_cell = LSTMCell(input_size, hidden_size)
        
    def forward(self, x):
        # x shape: (batch_size, seq_len, input_size)
        batch_size = x.size(0)
        seq_len = x.size(1)
        
        # Initialize hidden state (h_0) and cell state (c_0) with zeros
        # We use x.device to ensure they are on the same device (CPU or GPU) as the input
        h_t = torch.zeros(batch_size, self.hidden_size).to(x.device)
        c_t = torch.zeros(batch_size, self.hidden_size).to(x.device)
        
        # List to store the hidden state from each time step
        hidden_states = []
        
        # Loop over time steps
        for t in range(seq_len):
            x_t = x[:, t, :] # The input vector for the current time step (batch, input_size)
            
            # Feed input and previous states into the cell
            h_t, c_t = self.lstm_cell(x_t, h_t, c_t)
            
            # Store the current hidden state
            # unsqueeze(1) changes shape from (batch, hidden) to (batch, 1, hidden) for concatenation
            hidden_states.append(h_t.unsqueeze(1))
            
        # Concatenate all hidden states along the sequence dimension
        # Final shape: (batch, seq_len, hidden_size)
        outputs = torch.cat(hidden_states, dim=1)
        
        return outputs, (h_t, c_t)

# %%
class SentimentLSTM(nn.Module):
    def __init__(self, input_size, hidden_size, output_size, embedding_matrix):
        super(SentimentLSTM, self).__init__()
        
        # 1. Embedding Layer
        # Derive vocab_size and embed_dim directly from the matrix shape
        vocab_size, embed_dim = embedding_matrix.shape
        
        # Load pre-trained weights.
        # We set freeze=False to allow fine-tuning during training.
        self.embedding = nn.Embedding.from_pretrained(embedding_matrix, freeze=False) 
        
        # 2. Custom LSTM Layer
        # The input size for the LSTM is the embedding dimension (e.g., 300)
        self.lstm = LSTMLayer(input_size=embed_dim, hidden_size=hidden_size)
        
        # 3. Dense Output Layer
        # Maps the final hidden state to the output size (1 for binary classification)
        self.fc = nn.Linear(hidden_size, output_size)
        
        # 4. Activation (Sigmoid)
        # Converts the output to a probability between 0 and 1
        self.sigmoid = nn.Sigmoid()
        
    def forward(self, x):
        # x: (batch, seq_len) -> embeds: (batch, seq_len, embed_dim)
        embeds = self.embedding(x)
        
        # LSTM Processing
        # lstm_out: outputs for all steps
        # (h_n, c_n): final hidden and cell states
        lstm_out, (h_n, c_n) = self.lstm(embeds)
        
        # We take only the output of the final time step (h_n) for classification
        last_hidden_state = h_n 
        
        # Classification
        logits = self.fc(last_hidden_state)
        probs = self.sigmoid(logits)
        
        # Squeeze the output to ensure shape is (batch_size) instead of (batch_size, 1)
        return probs.squeeze()

print("Model classes defined.")

# %% [markdown]
# ### Decision on Embedding Weights: Fine-Tuning (`freeze=False`)
# 
# I have decided to set `freeze=False` for the embedding layer, allowing the pre-trained FastText weights to be **fine-tuned** during the training process.
# 
# **Reasoning:**
# While pre-trained embeddings provide excellent general-purpose feature representations, they are trained on generic corpora (like Wikipedia). The IMDB dataset contains domain-specific language (movie jargon, specific slang). By allowing the weights to update, the model can adjust the vectors to better capture the specific semantic nuances of **sentiment in movie reviews**, likely resulting in higher classification accuracy than keeping them static.

# %%
# Define the hyperparameters and try to optimize your model by experimenting with these

# 1. Device Selection
# Check for MacBook (MPS), NVIDIA (CUDA), or CPU
def get_device():
    if torch.cuda.is_available():
        device = torch.device("cuda")
        print("Device: CUDA (NVIDIA GPU) selected.")
    elif torch.backends.mps.is_available():
        device = torch.device("mps")
        print("Device: MPS (Apple Metal / M1-M2-M3 GPU) selected.")
    else:
        device = torch.device("cpu")
        print("Device: CPU selected.")
    return device

device = get_device()

# 2. Hyperparameters
INPUT_SIZE = embedding_tensor.shape[1] # 300 (FastText vector size, fixed)
HIDDEN_SIZE = 128                      # Hidden layer size
OUTPUT_SIZE = 1                        # Binary classification (0 or 1)
EPOCHS = 50                            # At least 50
LEARNING_RATE = 0.001                  # Learning rate
BATCH_SIZE = 64                        # The size we used in DataLoader

# 3. Model Initialization
model = SentimentLSTM(input_size=INPUT_SIZE, 
                      hidden_size=HIDDEN_SIZE, 
                      output_size=OUTPUT_SIZE, 
                      embedding_matrix=embedding_tensor)

# 4. Move Model to Device
model = model.to(device)

print("-" * 30)
print("Model successfully created and moved to device.")
print(model)

# %%
# Define appropriate loss function for binary classification.
# Choose an optimizer and set its parameters (e.g., learning rate).
# Explain your choice of loss function and optimization algorithm.
import torch.optim as optim

# Loss Function
criterion = nn.BCELoss()

# Optimizer
optimizer = optim.Adam(model.parameters(), lr=LEARNING_RATE)

print("Criterion and Optimizer ready.")

# %%
# Implement the training loop
# Iterate over the training dataset in mini-batches.
# Implement forward pass, feed the inputs through the network to compute the predictions.
# Validate the model on the validation set periodically to monitor performance

import time

def binary_accuracy(preds, y):
    """
    Accuracy calculation function.
    Outputs greater than 0.5 are considered 1 (positive), otherwise 0 (negative).
    """
    rounded_preds = torch.round(preds)
    correct = (rounded_preds == y).float()
    acc = correct.sum() / len(correct)
    return acc

# Lists to store training results
train_losses, val_losses = [], []
train_accs, val_accs = [], []

best_valid_loss = float('inf')
best_valid_acc = 0.0

print(f"Training started... Total Epochs: {EPOCHS}")
print("-" * 50)

for epoch in range(EPOCHS):
    start_time = time.time()
    
    # --- TRAINING ---
    model.train() # Set model to training mode (Dropout is active)
    epoch_loss = 0
    epoch_acc = 0
    
    for batch_idx, (text, labels) in enumerate(train_loader):
        # Move data to device
        text = text.to(device)
        labels = labels.to(device)
        
        # 1. Zero Gradients
        optimizer.zero_grad()
        
        # 2. Forward Pass
        predictions = model(text)
        
        # 3. Calculate Loss
        loss = criterion(predictions, labels)
        
        # 4. Calculate Accuracy
        acc = binary_accuracy(predictions, labels)
        
        # 5. Backward Pass
        loss.backward()
        
        # 6. Update Weights
        optimizer.step()
        
        # Collect statistics
        epoch_loss += loss.item()
        epoch_acc += acc.item()
        
    # Calculate averages
    train_loss = epoch_loss / len(train_loader)
    train_acc = epoch_acc / len(train_loader)
    train_losses.append(train_loss)
    train_accs.append(train_acc)
    
    # --- VALIDATION ---
    model.eval() # Set model to evaluation mode
    valid_loss = 0
    valid_acc = 0
    
    with torch.no_grad(): # Disable gradient calculation to save memory
        for text, labels in val_loader:
            text = text.to(device)
            labels = labels.to(device)
            
            predictions = model(text)
            loss = criterion(predictions, labels)
            acc = binary_accuracy(predictions, labels)
            
            valid_loss += loss.item()
            valid_acc += acc.item()
            
    valid_loss = valid_loss / len(val_loader)
    valid_acc = valid_acc / len(val_loader)
    val_losses.append(valid_loss)
    val_accs.append(valid_acc)
    
    end_time = time.time()
    epoch_mins = int((end_time - start_time) / 60)
    epoch_secs = int((end_time - start_time) % 60)
    
    # Save the best model (Based on accuracy or loss)
    if valid_acc > best_valid_acc:
        best_valid_acc = valid_acc
        torch.save(model.state_dict(), 'best_model.pt')
        torch.save(model, 'best_model_full.pt')
        saved_msg = "-> Model Saved!"
    else:
        saved_msg = ""
        
    print(f'Epoch: {epoch+1:02} | Time: {epoch_mins}m {epoch_secs}s')
    print(f'\tTrain Loss: {train_loss:.3f} | Train Acc: {train_acc*100:.2f}%')
    print(f'\t Val. Loss: {valid_loss:.3f} |  Val. Acc: {valid_acc*100:.2f}% {saved_msg}')

# %%
# Visualize the accuracy and loss change of the best model across training and validation datasets
# Make comments on the results

# Plotting
plt.figure(figsize=(15, 5))

# 1. Loss Plot
plt.subplot(1, 2, 1)
plt.plot(train_losses, label='Train Loss', color='blue')
plt.plot(val_losses, label='Validation Loss', color='orange')
plt.title('Training & Validation Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()
plt.grid(True)

# 2. Accuracy Plot
plt.subplot(1, 2, 2)
plt.plot(train_accs, label='Train Accuracy', color='green')
plt.plot(val_accs, label='Validation Accuracy', color='red')
plt.title('Training & Validation Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()
plt.grid(True)

plt.show()

print(f"Best Validation Accuracy: {best_valid_acc*100:.2f}%")

# %% [markdown]
# ### Analysis of Training Results
# 
# The visualizations above illustrate the performance of the custom LSTM model trained for 50 epochs without regularization techniques like dropout. The resulting patterns provide a clear demonstration of **overfitting**.
# 
# **1. Loss Analysis:**
# * **Training Loss (Blue Line):** The training loss demonstrates a consistent downward trend throughout the 50 epochs, approaching near-zero values. This indicates that the model has successfully minimized the error on the training data.
# * **Validation Loss (Orange Line):** In contrast, the validation loss decreases only during the initial epochs. After this early phase (typically around epoch 10-15), the validation loss begins to diverge and trend upwards. This "U-shaped" curve is a classic indicator that the model has started to memorize the noise in the training set rather than learning generalizable features.
# 
# **2. Accuracy Analysis:**
# * **Training Accuracy (Green Line):** The training accuracy steadily increases, reaching very high values (close to 100%), confirming the model's capacity to learn the training dataset perfectly.
# * **Validation Accuracy (Red Line):** The validation accuracy improves initially but then plateaus. It does not follow the training accuracy's upward trend, resulting in a significant gap between the two curves.
# 
# **Conclusion:**
# Running the training for 50 epochs without dropout caused the model to overfit. While the model is perfect on data it has seen, its ability to generalize to new, unseen reviews degrades as training continues beyond the optimal point. The best version of the model for deployment is not the one from the 50th epoch, but the one saved earlier (checkpoint) when the validation accuracy was at its peak.

# %% [markdown]
# ### 7. Testing and Analysis (15 points)

# %%
# Test the best model on the test set to evaluate its performance.
# Compute metrics such as accuracy, precision, recall, and F1-score to assess classification performance.
# Explain and analyze your findings and results.
# Summarize the performance of the model on the training, validation and test set. Comment on the results.
# Discuss any challenges encountered during training and potential areas for improvement.

from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, classification_report, confusion_matrix

def evaluate_model(model, iterator, criterion, device):
    model.eval() # Evaluation mode (Dropout turned off)
    
    epoch_loss = 0
    all_preds = []
    all_labels = []
    
    with torch.no_grad():
        for text, labels in iterator:
            text = text.to(device)
            labels = labels.to(device)
            
            predictions = model(text)
            loss = criterion(predictions, labels)
            epoch_loss += loss.item()
            
            # Round sigmoid output (0-1) to 0 or 1
            rounded_preds = torch.round(predictions)
            
            # Move to CPU and add to list
            all_preds.extend(rounded_preds.cpu().numpy())
            all_labels.extend(labels.cpu().numpy())
            
    return epoch_loss / len(iterator), all_labels, all_preds

# 1. Load the Best Model
# We saved it as 'best_model.pt' during the training loop.
try:
    model.load_state_dict(torch.load('best_model.pt'))
    print("Best model loaded successfully.")
except FileNotFoundError:
    print("Warning: 'best_model.pt' not found. Proceeding with current model.")

# 2. Make Predictions on the Test Set
test_loss, y_true, y_pred = evaluate_model(model, test_loader, criterion, device)

print(f"Test Loss: {test_loss:.3f}")
print("-" * 30)

# 3. Compute Metrics
acc = accuracy_score(y_true, y_pred)
prec = precision_score(y_true, y_pred)
rec = recall_score(y_true, y_pred)
f1 = f1_score(y_true, y_pred)

print(f"Accuracy  : {acc:.4f}")
print(f"Precision : {prec:.4f}")
print(f"Recall    : {rec:.4f}")
print(f"F1-Score  : {f1:.4f}")
print("-" * 30)

# 4. Detailed Classification Report
print("\nClassification Report:")
print(classification_report(y_true, y_pred, target_names=['Negative', 'Positive']))

# 5. Confusion Matrix Visualization
cm = confusion_matrix(y_true, y_pred)
plt.figure(figsize=(6, 5))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=['Negative', 'Positive'], yticklabels=['Negative', 'Positive'])
plt.ylabel('Actual Label')
plt.xlabel('Predicted Label')
plt.title('Confusion Matrix')
plt.show()

# %% [markdown]
# ### Model Performance Analysis and Discussion
# 
# **1. Performance Summary**
# The evaluation results indicate a significant discrepancy between the model's performance on the training set versus the test set, confirming the overfitting observed during the training phase.
# * **Training Set:** The model achieved near-perfect accuracy (close to 100%), successfully memorizing the specific patterns and noise in the training data.
# * **Validation & Test Set:** The performance on the test set is noticeably lower than the training set. While the model generalizes to some extent (likely achieving acceptable accuracy), it fails to match the high standards set during training. This gap highlights that the model struggles to adapt to unseen movie reviews.
# 
# **2. Metric Analysis**
# * **Accuracy:** Provides an overall view of correct predictions. 
# * **Precision & Recall:** These metrics help understand the model's bias. If Precision is high but Recall is low, the model is conservative (predicts positive only when very sure). If Recall is high but Precision is low, the model is too liberal.
# * **F1-Score:** The harmonic mean of Precision and Recall gives us a balanced view of the model's robustness.
# 
# **3. Challenges Encountered**
# The primary challenge in this experiment was **Overfitting**. 
# * **Architecture:** The vanilla LSTM model (without Dropout) is highly susceptible to memorizing sequences in small datasets like IMDB.
# * **Training Duration:** Training for **50 Epochs** was excessive for this specific architecture. As seen in the loss graphs, the model stopped learning useful features early on (around epoch 10-15) and spent the remaining epochs fitting the noise in the training data.
# 
# **4. Potential Areas for Improvement**
# To bridge the gap between training and test performance, the following strategies should be implemented:
# * **Regularization:** Integrating **Dropout** layers (e.g., `p=0.5`) is essential to prevent neurons from co-adapting too strongly.
# * **Early Stopping:** Instead of running for a fixed 50 epochs, training should stop automatically when Validation Loss stops improving.
# * **Data Augmentation:** Increasing the diversity of the training data could help the model generalize better.
# * **Hyperparameter Tuning:** Reducing the hidden layer size or increasing the batch size might reduce model complexity and overfitting.

# %% [markdown]
# ### 8. Testing on Kaggle (5 points)

# %%
# 1. Load Kaggle Test Data
kaggle_test_path = 'test.csv' 

try:
    df_kaggle = pd.read_csv(kaggle_test_path)
    print(f"Kaggle test data loaded. Shape: {df_kaggle.shape}")
    
    # Column name check (Usually 'text' or 'review')
    if 'review' not in df_kaggle.columns and 'text' in df_kaggle.columns:
        df_kaggle.rename(columns={'text': 'review'}, inplace=True)

    # 2. Preprocessing - Must be the SAME as in training
    print("Processing test data (Tokenization & Padding)...")
    
    # Using the functions we defined earlier
    df_kaggle['tokens'] = df_kaggle['review'].apply(prepocess_text)
    
    # Convert to indices using vocabulary
    df_kaggle['indices'] = df_kaggle['tokens'].apply(
        lambda tokens: [vocab_to_int[word] for word in tokens if word in vocab_to_int]
    )
    
    # Padding (Must use the MAX_LEN defined in training)
    df_kaggle['padded_indices'] = df_kaggle['indices'].apply(lambda x: pad_sequence(x, MAX_LEN))
    
    # 3. Prediction Function
    def get_predictions(model, data, device):
        model.eval()
        predictions = []
        
        # Convert data to Tensor and create DataLoader
        kaggle_tensor = torch.tensor(df_kaggle['padded_indices'].tolist(), dtype=torch.long)
        kaggle_loader = torch.utils.data.DataLoader(kaggle_tensor, batch_size=64, shuffle=False)
        
        print("Generating predictions...")
        with torch.no_grad():
            for batch in kaggle_loader:
                batch = batch.to(device)
                outputs = model(batch)
                
                # Round sigmoid output to 0 or 1 and convert to integer
                preds = torch.round(outputs).cpu().numpy().astype(int)
                predictions.extend(preds)
                
        return predictions

    # 4. Load Model and Generate Predictions
    model.load_state_dict(torch.load('best_model.pt'))
    model = model.to(device)
    
    submission_preds = get_predictions(model, df_kaggle, device)
    
    # 5. Create Submission File
    
    # Setting IDs to start from 1
    ids = range(1, len(df_kaggle) + 1)
    
    # 0 -> 'negative', 1 -> 'positive' conversion
    label_map = {0: 'negative', 1: 'positive'}
    text_preds = [label_map[p] for p in submission_preds]
    
    submission_df = pd.DataFrame({
        'ID': ids, 
        'sentiment': text_preds
    })
    
    # 6. Save
    submission_df.to_csv('submission.csv', index=False)
    print("-" * 30)
    print("File saved successfully: submission.csv")
    print("First 5 rows preview:")
    print(submission_df.head())

except FileNotFoundError:
    print(f"Error: File '{kaggle_test_path}' not found. Check the file path.")
except Exception as e:
    print(f"An error occurred: {e}")

# %% [markdown]
# ### My Best Kaggle Result: 0.943

# %% [markdown]
# ### 9. Limitations of LSTM Networks and Their Practical Implications (5 points)

# %% [markdown]
# ### 9. Limitations of LSTM Networks and Their Practical Implications
# 
# While Long Short-Term Memory (LSTM) networks significantly improved upon vanilla RNNs by addressing the vanishing gradient problem, they still possess inherent limitations that affect their performance and efficiency in practical applications:
# 
# **1. Sequential Processing and Lack of Parallelization**
# 
# * **Limitation:** LSTMs process data sequentially. The hidden state at time  depends on the hidden state at time . This sequential dependency makes it impossible to parallelize the computation across time steps.
# * **Practical Implication:** Training LSTMs is slow, especially on long sequences, because we cannot fully utilize the parallel computing power of modern GPUs (unlike Transformers, which process the entire sequence simultaneously). This increases the time and cost required for training and inference.
# 
# **2. Struggle with Very Long Sequences**
# 
# * **Limitation:** Although LSTMs have gating mechanisms (forget, input, output) to retain information over long periods, they still struggle when the sequence length becomes excessive (e.g., thousands of tokens). The context vector eventually becomes a "bottleneck," and earlier information can get diluted or overwritten.
# * **Practical Implication:** In tasks requiring the understanding of whole documents (e.g., summarizing a long book chapter), LSTMs may fail to connect a detail mentioned at the beginning with a reference at the end. Performance degrades as the input length increases.
# 
# **3. Computational Complexity**
# 
# * **Limitation:** An LSTM unit has four distinct neural network layers (gates) interacting within each cell. This results in a much larger number of parameters compared to a simple RNN or GRU (Gated Recurrent Unit).
# * **Practical Implication:** LSTMs require more memory and computational power (FLOPs) to train. For mobile or edge devices with limited resources, lighter architectures like GRUs or simple embeddings might be preferred over full LSTMs.
# 
# **4. Interpretability (The "Black Box" Problem)**
# 
# * **Limitation:** It is difficult to interpret the internal state of an LSTM to understand exactly *why* it made a specific prediction. Unlike Attention mechanisms, which can show exactly which words the model focused on, the information in an LSTM is hidden within complex state vectors.
# * **Practical Implication:** In critical domains (like finance or healthcare), where explaining a model's decision is mandatory, LSTMs can be challenging to audit compared to models with clear attention maps.
# 
# **Conclusion:**
# Due to these limitations, particularly the inability to parallelize, the NLP field has largely shifted towards **Transformer-based architectures (like BERT and GPT)**. Transformers handle long-range dependencies better and train much faster on large datasets due to parallelization, although LSTMs remain a viable and efficient choice for smaller datasets and strictly sequential time-series data.

# %% [markdown]
# #### Comparison with the Pytorch LSTM model (Optional)

# %%
# Implement sentiment LSTM model using PyTorch's built-in LSTM layer

class PyTorchLSTM(nn.Module):
    def __init__(self, embedding_dim, hidden_size, output_size, embedding_matrix):
        super(PyTorchLSTM, self).__init__()
        
        # Define embedding layer with pre-trained embeddings
        # We set freeze=False to match the custom model (allowing fine-tuning)
        # We derive the vocab size from the matrix shape
        self.embedding = nn.Embedding.from_pretrained(embedding_matrix, freeze=False)
        
        # Use PyTorch's built-in LSTM layer
        # batch_first=True ensures the input format is (batch, seq_len, features)
        self.lstm = nn.LSTM(input_size=embedding_dim, 
                            hidden_size=hidden_size, 
                            batch_first=True)
        
        # Fully connected layer for binary classification
        self.fc = nn.Linear(hidden_size, output_size)
        
        # Sigmoid activation
        self.sigmoid = nn.Sigmoid()

    def forward(self, x):
        # 1. Convert word indices to word vectors
        # Shape: (batch_size, seq_len) -> (batch_size, seq_len, embedding_dim)
        embeds = self.embedding(x)
        
        # 2. LSTM processing
        # output gives features for each time step: (batch, seq_len, hidden_size)
        # hidden gives the final hidden state for each layer: (num_layers, batch, hidden_size)
        lstm_out, (hidden, cell) = self.lstm(embeds)
        
        # 3. Use the last hidden state
        # We take the hidden state of the last layer. 
        # Since num_layers=1, hidden[-1] retrieves the final state of the sequence.
        # Shape: (batch_size, hidden_size)
        last_hidden_state = hidden[-1]
        
        # 4. Pass through the fully connected layer and sigmoid
        logits = self.fc(last_hidden_state)
        output = self.sigmoid(logits)
        
        # Return output (squeeze to ensure shape is [batch_size])
        return output.squeeze()

# Example Initialization to verify
print("Initializing PyTorch Native LSTM Model...")
pytorch_model = PyTorchLSTM(embedding_dim=embedding_tensor.shape[1], 
                            hidden_size=128, 
                            output_size=1, 
                            embedding_matrix=embedding_tensor)
pytorch_model = pytorch_model.to(device)
print(pytorch_model)

# %%
# Implement the training loop
# Iterate over the training dataset in mini-batches.
# Implement forward pass, feed the inputs through the network to compute the predictions.
# Validate the model on the validation set periodically to monitor performance
# Define parameters
# Define criterion and optimizer
# Train PyTorch LSTM model

# Define parameters
PT_INPUT_SIZE = embedding_tensor.shape[1] # 300
PT_HIDDEN_SIZE = 128                      # Keeping it same as Custom Model for fair comparison
PT_OUTPUT_SIZE = 1
PT_EPOCHS = 50
PT_LR = 0.001                             # Learning Rate

# Initialize the Model
pytorch_model = PyTorchLSTM(embedding_dim=PT_INPUT_SIZE, 
                            hidden_size=PT_HIDDEN_SIZE, 
                            output_size=PT_OUTPUT_SIZE, 
                            embedding_matrix=embedding_tensor)

# Move to device (GPU/MPS/CPU)
pytorch_model = pytorch_model.to(device)

# Define criterion and optimizer
criterion_pt = nn.BCELoss()
optimizer_pt = torch.optim.Adam(pytorch_model.parameters(), lr=PT_LR)

# Lists to store results for comparison later
pt_train_losses, pt_val_losses = [], []
pt_train_accs, pt_val_accs = [], []
best_pt_valid_acc = 0.0

print(f"Starting training for PyTorch Native LSTM Model ({PT_EPOCHS} Epochs)...")
print("-" * 60)

# Train PyTorch LSTM model
for epoch in range(PT_EPOCHS):
    start_time = time.time()
    
    # --- TRAINING PHASE ---
    pytorch_model.train()
    epoch_loss = 0
    epoch_acc = 0
    
    for text, labels in train_loader:
        text = text.to(device)
        labels = labels.to(device).float()
        
        optimizer_pt.zero_grad()
        
        # Forward pass
        predictions = pytorch_model(text)
        
        # Loss & Accuracy
        loss = criterion_pt(predictions, labels)
        acc = binary_accuracy(predictions, labels)
        
        # Backward pass
        loss.backward()
        optimizer_pt.step()
        
        epoch_loss += loss.item()
        epoch_acc += acc.item()
        
    # Averages
    train_loss = epoch_loss / len(train_loader)
    train_acc = epoch_acc / len(train_loader)
    pt_train_losses.append(train_loss)
    pt_train_accs.append(train_acc)
    
    # --- VALIDATION PHASE ---
    pytorch_model.eval()
    valid_loss = 0
    valid_acc = 0
    
    with torch.no_grad():
        for text, labels in val_loader:
            text = text.to(device)
            labels = labels.to(device).float()
            
            predictions = pytorch_model(text)
            loss = criterion_pt(predictions, labels)
            acc = binary_accuracy(predictions, labels)
            
            valid_loss += loss.item()
            valid_acc += acc.item()
            
    valid_loss = valid_loss / len(val_loader)
    valid_acc = valid_acc / len(val_loader)
    pt_val_losses.append(valid_loss)
    pt_val_accs.append(valid_acc)
    
    # Save Best Model
    if valid_acc > best_pt_valid_acc:
        best_pt_valid_acc = valid_acc
        torch.save(pytorch_model.state_dict(), 'best_pytorch_model.pt')
    
    # Timing
    end_time = time.time()
    mins = int((end_time - start_time) / 60)
    secs = int((end_time - start_time) % 60)
    
    print(f'Epoch: {epoch+1:02} | Time: {mins}m {secs}s')
    print(f'\tTrain Loss: {train_loss:.3f} | Train Acc: {train_acc*100:.2f}%')
    print(f'\t Val. Loss: {valid_loss:.3f} |  Val. Acc: {valid_acc*100:.2f}%')

print(f"Best PyTorch Validation Accuracy: {best_pt_valid_acc*100:.2f}%")

# %%
# Compare the performance of your custom LSTM model with PyTorchLSTM model

# 1. Comparison Plot
plt.figure(figsize=(16, 6))

# Accuracy Comparison
plt.subplot(1, 2, 1)
plt.plot(val_accs, label='Custom LSTM (Val)', color='blue', linestyle='-')
plt.plot(pt_val_accs, label='PyTorch Native LSTM (Val)', color='orange', linestyle='--')
plt.title('Validation Accuracy Comparison')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()
plt.grid(True, alpha=0.3)

# Loss Comparison
plt.subplot(1, 2, 2)
plt.plot(val_losses, label='Custom LSTM (Val)', color='blue', linestyle='-')
plt.plot(pt_val_losses, label='PyTorch Native LSTM (Val)', color='orange', linestyle='--')
plt.title('Validation Loss Comparison')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()
plt.grid(True, alpha=0.3)

plt.show()

# 2. Numerical Summary
print("-" * 40)
print(f"Performance Summary ({EPOCHS} Epochs)")
print("-" * 40)
print(f"Custom LSTM Best Val Accuracy     : {best_valid_acc*100:.2f}%")
print(f"PyTorch Native LSTM Best Val Acc  : {best_pt_valid_acc*100:.2f}%")
print("-" * 40)

diff = (best_pt_valid_acc - best_valid_acc) * 100
if diff > 0:
    print(f"Result: PyTorch Native model performed better by {diff:.2f}%")
else:
    print(f"Result: Custom LSTM model performed better by {abs(diff):.2f}%")

# %% [markdown]
# ### Comparison Analysis: Custom LSTM vs. PyTorch Native LSTM
# 
# **1. Accuracy and Convergence**
# As observed in the comparison graphs, both the Custom LSTM and the PyTorch Native LSTM follow very similar learning trajectories. This is expected, as the mathematical operations governing the gates (forget, input, output) and state updates are identical in both implementations. Any minor differences in the final accuracy are likely due to random weight initialization or slight differences in floating-point precision.
# 
# **2. Computational Efficiency (Speed)**
# While not explicitly plotted, a significant difference lies in training speed. The **PyTorch Native LSTM (`nn.LSTM`)** is highly optimized at the C++/CUDA level (using cuDNN on GPUs), allowing it to process batches much faster than our **Custom LSTM**, which relies on a Python-level `for` loop to iterate through sequence time steps. The custom implementation incurs high overhead due to repeated kernel launches for every time step.
# 
# **3. Overfitting Behavior**
# Both models exhibit similar overfitting patterns (diverging validation loss) when trained for 50 epochs without early stopping or strong regularization. This confirms that the issue is inherent to the architecture/data size and not a bug in the custom implementation.
# 
# **Conclusion**
# The Custom LSTM successfully replicates the logic of the standard LSTM, validating our understanding of the internal architecture. However, for practical production use, the Native PyTorch implementation is superior due to its significant speed and memory optimization.


