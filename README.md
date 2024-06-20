# PAI assigment - Papanicolau image viewer and classifier system


Este estudo aborda o reconhecimento de células em exames de Papanicolau, uma tarefa crucial para o diagnóstico precoce do câncer cervical. Tradicionalmente, essa análise é realizada manualmente por patologistas, o que pode ser demorado e sujeito a erros humanos. Para melhorar a precisão e a eficiência, implementamos métodos automatizados utilizando dois classificadores principais: uma Máquina de Vetores de Suporte (SVM) como classificador raso e EfficientNet como classificador profundo. O SVM demonstrou eficácia ao lidar com dados de menor dimensionalidade e classes balanceadas, enquanto o EfficientNet se destacou em lidar com dados de imagem complexos e aprender caracterı́sticas de alto nı́vel diretamente dos dados. Os classificadores foram desenvolvidos utilizando técnicas abrangentes de pré-processamento e análise de imagem, sendo avaliados através de várias métricas para determinar sua eficácia. Nossos resultados indicam que o EfficientNet supera significativamente o SVM em termos de precisão, embora exija maiores recursos computacionais. 
A implementação do aplicativo para reconhecimento de células em exames de Papanicolau foi dividida em duas partes principais: a interface gráfica do usuário (GUI), desenvol- vida em Java, e os classificadores de imagem, implementados em Python. A comunicação entre os modelos classificadores e interface é feita através de um sistema de camada de processamento com comandos feitos em Java executando partes de códigos escritos em Python, isso foi definido como camada de interface entre Java e Python.


# AI Folder

Folder with classification models, image processing scripts and train data.

- Python 3.8 or higher installed on the machine. [Download Python](https://www.python.org/downloads/)

## Execution

Jupyter Notebook is required to run the scripts. To run the scripts, open the Jupyter Notebook and run the scripts.

Only the scripts in the AI folder need to be executed.


# UI Folder

Folder with the user interface scripts and the main script to run the system.

- JDK 17 ou Java SE 17 instalado na máquina. [Baixar JDK 17](https://www.oracle.com/br/java/technologies/downloads/#jdk17-windows)

## Execução

### Na pasta Root do programa
```bash
   cd ui
```

Para executar a aplicação apenas clique no executável ou execute em um terminal:

```bash
  ./mvnw clean javafx:run
```

Para compilar o código e buildar a aplicação execute o seguinte em um terminal
```bash
   ./mvnw clean javafx:run
``` 

Para gerar um executavel (testado apenas com windows)
```bash
   ./mvnw clean
   ./mvnw compile
   ./mvnw package
``` 

## Autores

- [Edmar](https://www.github.com/Lexizz7)
- [Leon](https://www.github.com/leon-junio)
- [Felipe](https://github.com/felagmoura)
