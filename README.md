# Validation and Verification: Practical Session #2

The goal of this practical session is to use and implement static analysis tools to compute metrics such as Cyclomatic Complexity(CC) or Tight Class Cohesion (TCC).
After this session you should be able to use existing static analysis tools, and to extend them to implement your own analysis. 

## Exercises

You can access the exercises [here](sujet.md)

## Lab implementation

You can realize this lab by group of 1 or 2. 

## Deliverable and evaluation

We will mark this lab. You need to fork this repository and to submit a pull request with the answers directly here (deadline December 20th 2024, 23:59).

Note : To be taken into account for grading, the title of the pull request must be formatted as follows {LAST_NAME1}{FIRST_NAME1}&{LAST_NAME2}_{FIRST_NAME2}  
If you want to remain anonymous on github, you can use a pseudonym if you communicate it to your lab teacher {PSEUDO1}&{PSEUDO2}.



# JavaParser Starter

Ce projet utilise la bibliothèque JavaParser pour analyser du code source Java et générer diverses métriques telles que la complexité cyclomatique et la cohésion des classes et no getter.

## Prérequis

- Java 8 ou supérieur
- Maven (pour la construction du projet)
- Un projet à analyser (fournir le chemin vers le code source)

## Comment construire le projet

Pour construire le projet, vous devez le compiler en utilisant Maven.

1. Clonez le dépôt :
    ```bash
    git clone https://votre-url-de-repository.git
    cd javaparser-starter
    ```

2. Construisez le projet avec Maven :
    ```bash
    mvn clean install
    ```

3. Cela générera un fichier `jar` dans le répertoire `target` : `javaparser-starter-1.0-jar-with-dependencies.jar`.

## Comment exécuter le programme

Pour exécuter le programme, utilisez la commande suivante :

```bash
java -jar target/javaparser-starter-1.0-jar-with-dependencies.jar <chemin-vers-le-code-source>
