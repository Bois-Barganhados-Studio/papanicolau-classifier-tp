import subprocess
import sys
import importlib
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'  # Para suprimir logs informativos e de aviso, use '3' para suprimir logs de erro também

def install_requirements(requirements_file):
    try:
        subprocess.check_call([sys.executable, "-m", "pip", "install", "-r", requirements_file])
    except subprocess.CalledProcessError as e:
        print(f"Failed to install dependencies: {e}")
        sys.exit(1)

def install_and_import(package):
    try:
        importlib.import_module(package)
    except ImportError:
        subprocess.check_call([sys.executable, "-m", "pip", "install", package])
    finally:
        globals()[package] = importlib.import_module(package)

if __name__ == "__main__":
    # Install requirements
    install_requirements(sys.argv[1])

    # Import installed packages
    install_and_import('numpy')
    install_and_import('tensorflow')
    install_and_import('keras')
    install_and_import('torch')
    install_and_import('torchvision')
    install_and_import('skimage.feature')

