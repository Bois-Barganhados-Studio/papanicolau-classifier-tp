# PAI assigment - Papanicolau image viewer and classifier system

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
  # Na pasta Root do programa
  cd ui
  mvn clean jfx:run

```

Para compilar o código e buildar a aplicação execute o seguinte em um terminal
```bash
   mvn clean jfx:run
``` 

Para gerar um executavel (testado apenas com windows)
```bash
   mvn clean
   mvn compile
   mvn package
``` 

## Autores

- [Edmar](https://www.github.com/Lexizz7)
- [Leon](https://www.github.com/leon-junio)
- [Felipe](https://github.com/felagmoura)