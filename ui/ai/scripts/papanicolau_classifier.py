import subprocess
import sys
import importlib
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'  # Para suprimir logs informativos e de aviso, use '3' para suprimir logs de erro também
# Configurar o encoding padrão para UTF-8 para evitar problemas de encoding
sys.stdout.reconfigure(encoding='utf-8')

from keras.models import load_model
from keras.preprocessing import image
from keras.applications.efficientnet import preprocess_input, decode_predictions
import numpy as np

efficient_net_binary_path = 'efficientnet_binary_classifier.keras'
efficient_net_multiclass_path = 'efficientnet_multiclass_classifier.keras'
binary_class_names = ['Negative', 'Positive']
multiclass_class_names = ['ASC-H', 'ASC-US', 'HSIL', 'LSIL', 'Negative_for_intraepithelial_lesion', 'SCC']

def main(image_path, models_path):
    ef_model_bin = load_model(models_path + efficient_net_binary_path)
    ef_model_multi = load_model(models_path + efficient_net_multiclass_path)

    # Check if file exists
    if not os.path.isfile(image_path):
        print(f"File {image_path} not found.")
        sys.exit(1)

    # Load the image file
    img = image.load_img(image_path, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = preprocess_input(img_array)

    # Make predictions
    predictions_bin = ef_model_bin.predict(img_array)
    predictions_multi = ef_model_multi.predict(img_array)

    # Process binary predictions
    print("Binary Classifier Results:")
    binary_index = int(predictions_bin[0][0] > 0.5)
    binary_result = binary_class_names[binary_index]
    print(f"Prediction: {binary_result} ({predictions_bin[0][0] * 100:.2f}%)")
            
    # Process multiclass predictions
    print("\nMulticlass Classifier Results:")
    for i, score in enumerate(predictions_multi[0]):
        print(f"{multiclass_class_names[i]}: {score * 100:.2f}%")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python predict.py <image_path> <classifier_path>")
        sys.exit(1)

    image_path = sys.argv[1]
    models_path = sys.argv[2]
    main(image_path,models_path)
