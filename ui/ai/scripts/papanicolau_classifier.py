import sys
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
sys.stdout.reconfigure(encoding='utf-8')

import numpy as np
import torch
from torchvision import transforms
from PIL import Image


efficient_net_binary_path = 'efficientNet_cpu_bin.pth'
efficient_net_multiclass_path = 'efficientNet_cpu_multi.pth'
binary_class_names = ['Negative', 'Positive']
multiclass_class_names = ['ASC-H', 'ASC-US', 'HSIL', 'LSIL', 'Negative_for_intraepithelial_lesion', 'SCC']


def binary_predict(img_path):
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


def main(image_path, models_path):
    
    pred = binary_predict(image_path)

    # Process binary predictions
    print("Binary Classifier Results:\n")
    print("SVM Binary Prediction: ")
    print(f"\n")
    print("EfficientNet Torch Binary Prediction:")
    print(f"{pred} \n")
    
    print("Multiclass Classifier Results:\n")
    print("SVM Multiclass Prediction: ")
    print(f"\n")
    print("EfficientNet Torch Multiclass Prediction: ")
    print("\n")
            
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
