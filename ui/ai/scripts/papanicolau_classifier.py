import sys
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
sys.stdout.reconfigure(encoding='utf-8')

import numpy as np
import torch
from torchvision import transforms
from PIL import Image
from skimage.feature import graycomatrix, graycoprops
import joblib

efficient_net_binary_path = 'efficientNet_cpu_bin.pth'
efficient_net_multiclass_path = 'efficientNet_cpu_multiclass.pth'
svm_binary_path = 'binary_svm_classifier.pkl'
svm_multi_path = 'multiclass_svm_classifier.pkl'
binary_class_names = ['Negative', 'Positive']
multiclass_class_names = ['ASC-H', 'ASC-US', 'HSIL', 'LSIL', 'Negative_for_intraepithelial_lesion', 'SCC']

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

def get_haralick_features(image_path):
    image = Image.open(image_path)
    q_img = quantize_image(image)
    glcm = compute_glcm(q_img)
    features = compute_haralick_features(glcm)
    return features


def binary_predict(image_path, models_path):
    ef_model_bin = torch.load(os.path.join(models_path, efficient_net_binary_path))
    #ef_model_multi = load_model(os.path.join(models_path, efficient_net_multiclass_path))
    
    # Verificar se o arquivo existe
    if not os.path.isfile(image_path):
        print(f"File {image_path} not found.")
        sys.exit(1)

    # Carregar e pré-processar a imagem
    data_transforms = transforms.Compose([
                transforms.Resize(256),  # Redimensiona para 256x256 antes do recorte central
                transforms.CenterCrop(224),  # Recorta centralmente para 224x224
                transforms.ToTensor(),
                transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
    ])
    ef_model_bin.eval()
    image = Image.open(image_path)
    image = image.convert('RGB')
    image = data_transforms(image).unsqueeze(0)
    with torch.no_grad():
        outputs = ef_model_bin(image)
        print("Torch outputs:",outputs)
        _, preds = torch.max(outputs, 1)
        print("Torch preds:",preds)
    return binary_class_names[preds.item()]


def multiclass_predict(image_path, models_path, is_negative):
    if is_negative == 'Negative':
        return 'Negative'
    ef_model_multi = torch.load(os.path.join(models_path, efficient_net_multiclass_path))
     # Verificar se o arquivo existe
    if not os.path.isfile(image_path):
        print(f"File {image_path} not found.")
        sys.exit(1)

    # Carregar e pré-processar a imagem
    data_transforms = transforms.Compose([
                transforms.Resize(256),  # Redimensiona para 256x256 antes do recorte central
                transforms.CenterCrop(224),  # Recorta centralmente para 224x224
                transforms.ToTensor(),
                transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
    ])
    ef_model_multi.eval()
    image = Image.open(image_path)
    image = image.convert('RGB')
    image = data_transforms(image).unsqueeze(0)
    with torch.no_grad():
        outputs = ef_model_multi(image)
        print("Torch outputs:",outputs)
        _, preds = torch.max(outputs, 1)
        print("Torch preds:",preds)
    return multiclass_class_names[preds.item()]

    
def svm_binary_predict(img_path, models_path):
    # Load and preprocess the image
    image = Image.open(img_path)
    q_img = quantize_image(image)
    glcm = compute_glcm(q_img)
    features = compute_haralick_features(glcm)
    
    # Ensure features are in a NumPy array format
    features_array = np.array(list(features.values())).reshape(1, -1)

    # Load the SVM model
    svm_model = joblib.load(os.path.join(models_path, svm_binary_path))

    # Predict the class
    prediction = svm_model.predict(features_array)
    
    return prediction.item()

def svm_multiclass_predict(img_path, models_path):
    # Load and preprocess the image
    image = Image.open(img_path)
    q_img = quantize_image(image)
    glcm = compute_glcm(q_img)
    features = compute_haralick_features(glcm)
    
    # Ensure features are in a NumPy array format
    features_array = np.array(list(features.values())).reshape(1, -1)

    # Load the SVM model
    svm_model = joblib.load(os.path.join(models_path, svm_multi_path))

    # Predict the class
    prediction = svm_model.predict(features_array)
    
    return prediction.item()

def main(image_path, models_path):
    
    predEf = binary_predict(image_path, models_path)
    
    predMultiEf = multiclass_predict(image_path,models_path, predEf)
    
    svmPred = svm_binary_predict(image_path, models_path)
    
    svmMultiPred = svm_multiclass_predict(image_path, models_path)

    # Process binary predictions
    print("Binary Classifier Results:\n")
    print("Multiclass Classifier Results:\n")
    print("----------------------------------\n");
    print("EfficientNet Torch:\n")
    print("EfficientNet Torch Binary Prediction:")
    print(f"{predEf} \n")
    print("EfficientNet Torch Multiclass Prediction: ")
    print(f"{predMultiEf} \n")
    print("SVM:\n")
    print("SVM Binary Prediction: ")
    print(f"{svmPred}\n")
    print("SVM Multiclass Prediction: ")
    print(f"{svmMultiPred}\n")
            
    # # Process multiclass predictions
    # print("\nMulticlass Classifier Results:")
    # for i, score in enumerate(predictions_multi[0]):
    #     print(f"{multiclass_class_names[i]}: {score * 100:.2f}%")

def test_many_images():
    # Test many images
    images_folder = './imgs_temp/'
    for img_name in os.listdir(images_folder):
        if img_name.endswith('.jpg') or img_name.endswith('.png') or img_name.endswith('.jpeg'):
            main(images_folder + img_name, './models/')


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python predict.py <image_path> <classifier_path>")
        sys.exit(1)

    image_path = sys.argv[1]
    models_path = sys.argv[2]
    main(image_path,models_path)
    # test_many_images()
