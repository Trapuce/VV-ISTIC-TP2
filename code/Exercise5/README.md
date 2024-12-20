# Code of your exercise

Le code de cet exercice ainsi que les captures d'écran nécessaires sont fournis ci-dessous.

![Description de l'image](./Capture%20d’écran%20du%202024-12-20%2017-16-34.png)

```java

package fr.istic.vv.Exercise5;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CyclomaticComplexityCalculator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java CyclomaticComplexityCalculator <path-to-source> [path-to-source2 ...]");
            return;
        }

        try {
            Map<String, Map<String, Integer>> allProjectComplexities = new HashMap<>();

            for (String sourcePath : args) {
                CyclomaticComplexityCalculator calculator = new CyclomaticComplexityCalculator();
                Map<String, Integer> methodComplexities = calculator.analyzeProject(sourcePath);
                allProjectComplexities.put(sourcePath, methodComplexities);
                
                System.out.println("Method complexities for project: " + sourcePath);
                for (Map.Entry<String, Integer> entry : methodComplexities.entrySet()) {
                    System.out.println(String.format("%s : Cyclomatic Complexity = %d", entry.getKey(), entry.getValue()));
                }
                System.out.println();
            }

            generateHistogram(allProjectComplexities);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Analyse le projet source et calcule la complexité cyclomatique pour chaque
     * méthode trouvée.
     *
     * @param sourcePath Le chemin du projet source à analyser.
     * @return Une carte contenant la signature de la méthode comme clé et sa
     *         complexité cyclomatique comme valeur.
     * @throws Exception Si une erreur se produit lors de l'analyse du projet.
     */
    public Map<String, Integer> analyzeProject(String sourcePath) throws Exception {
        Map<String, Integer> methodComplexities = new HashMap<>();

        SourceRoot sourceRoot = new SourceRoot(Paths.get(sourcePath));
        sourceRoot.tryToParseParallelized();

        for (CompilationUnit cu : sourceRoot.getCompilationUnits()) {
            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                int complexity = calculateComplexity(method);
                String methodSignature = getMethodSignature(cu, method);
                methodComplexities.put(methodSignature, complexity);
            }
        }

        return methodComplexities;
    }

    /**
     * Calcule la complexité cyclomatique d'une méthode en comptant les différentes
     * structures de contrôle
     * dans le corps de la méthode (if, for, while, etc.).
     * La complexité cyclomatique de base est 1, et chaque structure de contrôle
     * ajoute 1 à cette complexité.
     *
     * @param method La méthode pour laquelle la complexité doit être calculée.
     * @return La complexité cyclomatique de la méthode.
     */
    public int calculateComplexity(MethodDeclaration method) {
        int complexity = 1;
        complexity += method.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.DoStmt.class).size();
        complexity += method.findAll(com.github.javaparser.ast.stmt.SwitchEntry.class).size();
        complexity += method.findAll(com.github.javaparser.ast.expr.ConditionalExpr.class).size();
        return complexity;
    }

    /**
     * Obtient la signature complète d'une méthode, incluant le nom de la classe, le
     * nom de la méthode
     * et les paramètres. La signature permet d'identifier de manière unique chaque
     * méthode.
     *
     * @param cu     Le fichier source contenant la méthode.
     * @param method La méthode pour laquelle la signature doit être générée.
     * @return La signature complète de la méthode sous forme de chaîne de
     *         caractères.
     */
    private String getMethodSignature(CompilationUnit cu, MethodDeclaration method) {
        String className = "UnknownClass";
        Optional<ClassOrInterfaceDeclaration> classDeclaration = cu.findFirst(ClassOrInterfaceDeclaration.class);
        if (classDeclaration.isPresent()) {
            className = classDeclaration.get().getNameAsString();
        }

        String methodName = method.getNameAsString();

        StringBuilder parameters = new StringBuilder();
        for (int i = 0; i < method.getParameters().size(); i++) {
            if (i > 0) {
                parameters.append(", ");
            }
            parameters.append(method.getParameters().get(i).getType())
                    .append(" ")
                    .append(method.getParameters().get(i).getName());
        }

        return className + "." + methodName + "(" + parameters + ")";
    }

    private static void generateHistogram(Map<String, Map<String, Integer>> allProjectComplexities) {
        for (Map.Entry<String, Map<String, Integer>> project : allProjectComplexities.entrySet()) {
            String projectPath = project.getKey();
            Map<String, Integer> methodComplexities = project.getValue();

            Map<Integer, Integer> complexityFrequency = new HashMap<>();
            for (Integer complexity : methodComplexities.values()) {
                complexityFrequency.put(complexity, complexityFrequency.getOrDefault(complexity, 0) + 1);
            }

            System.out.println("Cyclomatic Complexity Histogram for project: " + projectPath);
            for (Map.Entry<Integer, Integer> entry : complexityFrequency.entrySet()) {
                System.out.println("CC " + entry.getKey() + ": " + entry.getValue() + " method(s)");
            }
            System.out.println();

            saveHistogramToCSV(projectPath, complexityFrequency);
        }
    }

    /**
     * Save the histogram data to a CSV file for further analysis or visualization.
     */
    private static void saveHistogramToCSV(String projectPath, Map<Integer, Integer> complexityFrequency) {
        try (FileWriter writer = new FileWriter(projectPath + "_complexity_histogram.csv")) {
            writer.append("Cyclomatic Complexity, Frequency\n");
            for (Map.Entry<Integer, Integer> entry : complexityFrequency.entrySet()) {
                writer.append(entry.getKey().toString()).append(", ").append(entry.getValue().toString()).append("\n");
            }
            System.out.println("Histogram saved to: " + projectPath + "_complexity_histogram.csv");
        } catch (Exception e) {
            System.err.println("Error saving histogram to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

}



```
FilterHelpAppendable : Cohesion = 0,00
Options : Cohesion = 0,00
PatternOptionBuilder : Cohesion = 0,20
AmbiguousOptionException : Cohesion = 0,50
Builder : Cohesion = 0,60
PosixParser : Cohesion = 0,17
AlreadySelectedException : Cohesion = 0,00
GnuParser : Cohesion = 1,00
AbstractHelpFormatter : Cohesion = 0,05
UnrecognizedOptionException : Cohesion = 0,00
HelpAppendable : Cohesion = 0,00
HelpFormatter : Cohesion = 0,28
OptionGroup : Cohesion = 0,22
TextStyle : Cohesion = 0,11
TextHelpAppendable : Cohesion = 0,22
OptionBuilder : Cohesion = 0,25
TableDefinition : Cohesion = 0,00
Converter : Cohesion = 0,00
CommandLineParser : Cohesion = 0,00
TypeHandler : Cohesion = 0,13
MissingOptionException : Cohesion = 0,00
DefaultParser : Cohesion = 0,11
MissingArgumentException : Cohesion = 0,00
OptionFormatter : Cohesion = 0,00
DeprecatedAttributes : Cohesion = 0,00
Parser : Cohesion = 0,08
BasicParser : Cohesion = 0,00
CommandLine : Cohesion = 0,08
Option : Cohesion = 0,27
ParseException : Cohesion = 0,00
Cohesion Histogram for Project: /home/daouda-traore/Téléchargements/commons-cli-master/src/main/java/org/apache/commons/cli
Cohesion 0.5: 1 classes
Cohesion 0.0: 14 classes
Cohesion 0.21739130434782608: 1 classes
Cohesion 0.2222222222222222: 1 classes
Cohesion 0.07692307692307693: 1 classes
Cohesion 0.2765957446808511: 1 classes
Cohesion 0.13333333333333333: 1 classes
Cohesion 0.25: 1 classes
Cohesion 1.0: 1 classes
Cohesion 0.16666666666666666: 1 classes
Cohesion 0.6: 1 classes
Cohesion 0.0847457627118644: 1 classes
Cohesion 0.2: 1 classes
Cohesion 0.1111111111111111: 1 classes
Cohesion 0.047619047619047616: 1 classes
Cohesion 0.10714285714285714: 1 classes
Cohesion 0.2653061224489796: 1 classes
Dependency graph saved to: /home/daouda-traore/Téléchargements/commons-cli-master/src/main/java/org/apache/commons/cli_dependency_graph.dot
Method complexities for project: /home/daouda-traore/Téléchargements/commons-cli-master/src/main/java/org/apache/commons/cli
