import numpy as np
import pandas as pd
import sys
from skimage.feature import graycomatrix, graycoprops
from PIL import Image
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
sys.stdout.reconfigure(encoding='utf-8')

def quantize_image(image, levels = 16):
    grayscale_image = image.convert("L")
    quantized_image = np.array(grayscale_image) // (256 // levels)
    return quantized_image

def compute_cooccurrence_matrices(q_img, distances=[1, 2, 4, 8, 16, 32], angles=[0]):
    cooccurrence_matrices = graycomatrix(q_img, distances, angles, levels=16, symmetric=True, normed=True)
    return cooccurrence_matrices

def compute_glcm(q_img, dists=[1, 2 ,4, 8, 16, 32], angles=[0]):
    glcm = graycomatrix(q_img, distances=dists, angles=angles, levels=16, symmetric=True, normed=True)
    return glcm

def compute_haralick_features(glcm):
    contrast = graycoprops(glcm, 'contrast').flatten()
    homogeneity = graycoprops(glcm, 'homogeneity').flatten()
    
    # Entropy calculation
    glcm_sum = glcm.sum(axis=(0, 1))
    norm_glcm = glcm / glcm_sum
    entropy = -np.sum(norm_glcm * np.log(norm_glcm + 1e-10), axis=(0, 1)).flatten()
    
    features = {
        'contrast': contrast,
        'homogeneity': homogeneity,
        'entropy': entropy
    }
    return features

def main(image_path):
    image = Image.open(image_path)
    q_img = quantize_image(image)
    glcm = compute_glcm(q_img)
    features = compute_haralick_features(glcm)
    distances = [1, 2, 4, 8, 16, 32]
    # Print the cooccurrence matrices to [1, 2 ,4, 8, 16, 32]
    haralick_str = 'Haralick para [1, 2 ,4, 8, 16, 32]\n'
    keys = list(features.keys())
    qtd = len(features[keys[0]])
    for i in range(qtd):
        haralick_str += f'  Distancia {distances[i]}:\n'
        for key in keys:
            haralick_str += f'    {key}: {features[key][i]}\n'
        haralick_str += '\n'
    
    print(haralick_str)
    
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Error in arguments. Usage: python haralick.py <image_path>")
        sys.exit(1)

    image_path = sys.argv[1]
    main(image_path)
