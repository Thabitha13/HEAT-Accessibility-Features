# QuickDraw Image Classification with a 2D CNN

COMP7017 Deep Learning — Project A

A Convolutional Neural Network that classifies hand-drawn doodles from Google's QuickDraw dataset into 10 categories at 94% test accuracy.

## Categories

Apple, Bicycle, Cat, Dog, Fish, House, Star, Tree, Umbrella, Sun

## Model Architecture

Two convolutional blocks following the LeNet-5 pattern:

| Layer | Configuration | Output Shape |
|-------|--------------|-------------|
| Normalization | Adapted to training data | 28×28×1 |
| Conv2D + ReLU | 32 filters, 3×3, stride 1 | 26×26×32 |
| MaxPooling2D | 2×2, stride 2 | 13×13×32 |
| Conv2D + ReLU | 64 filters, 3×3, stride 1 | 11×11×64 |
| MaxPooling2D | 2×2, stride 2 | 5×5×64 |
| Dropout | 0.3 | 5×5×64 |
| Flatten | — | 1600 |
| Dense + ReLU | 128 neurons | 128 |
| Dropout | 0.5 | 128 |
| Dense + ReLU | 64 neurons | 64 |
| Dropout | 0.5 | 64 |
| Dense + Softmax | 10 classes | 10 |

## Results

- **Test accuracy**: 94.12%
- **Weighted F1-score**: 0.94
- **Best classes**: Bicycle (98.0%), House (97.6%), Apple (96.7%)
- **Hardest classes**: Cat (85.3%), Dog (86.0%) — mutual misclassification due to visual similarity at 28×28 resolution

## Experiments

| | Experiment 1 | Experiment 2 (Final) | Experiment 3 |
|---|---|---|---|
| Samples/class | 5,000 | 10,000 | 10,000 |
| Conv blocks | 2 | 2 | 3 |
| Epochs | 100 | 17 (early stop) | 15 (early stop) |
| Overfitting | Yes | No | No |
| Test accuracy | ~94% | 94% | 94% |

Experiment 3 proved that the accuracy ceiling is a data limitation at 28×28 resolution, not a model capacity issue.

## Setup

### Requirements

- Python 3.8+
- TensorFlow 2.x
- Keras
- NumPy
- Matplotlib
- scikit-learn

### Install dependencies

```bash
pip install tensorflow numpy matplotlib scikit-learn
```

### Run

1. Open `QuickDraw_CNN_Project.ipynb` in Jupyter Notebook or Google Colab
2. Run all cells — the notebook downloads the QuickDraw data automatically
3. Training takes 10-30 minutes on GPU

## Project Structure

```
├── QuickDraw_CNN_Project.ipynb    # Main notebook with all code and outputs
├── COMP7017_Report.pdf            # Project report
├── README.md                      # This file
└── quickdraw_data/                # Downloaded automatically on first run
    ├── apple.npy
    ├── bicycle.npy
    ├── cat.npy
    ├── dog.npy
    ├── fish.npy
    ├── house.npy
    ├── star.npy
    ├── tree.npy
    ├── umbrella.npy
    └── sun.npy
```

## Built With

- [Keras](https://keras.io/) / [TensorFlow](https://www.tensorflow.org/)
- [Google QuickDraw Dataset](https://quickdraw.withgoogle.com/data)
- Techniques from COMP7017 Lectures 8 and 9 (CNNs, LeNet-5)
