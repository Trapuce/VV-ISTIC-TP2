package fr.istic.vv.Exercise6;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.*;

public class ClassCohesionCalculator {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ClassCohesionCalculator <path-to-source>");
            return;
        }
    
        String sourcePath = args[0];
    
        try {
            ClassCohesionCalculator calculator = new ClassCohesionCalculator();
            Map<String, Double> classCohesion = calculator.analyzeProject(sourcePath);
    
            for (Map.Entry<String, Double> entry : classCohesion.entrySet()) {
                String className = entry.getKey();
                Double cohesion = entry.getValue();
                System.out.println(String.format("%s : Cohesion = %.2f", className, cohesion));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

     /**
     * Analyse le projet source et calcule la cohésion pour chaque classe publique dans le projet.
     *
     * @param sourcePath Le chemin du projet source à analyser.
     * @return Une carte contenant le nom de la classe comme clé et sa cohésion comme valeur.
     * @throws Exception Si une erreur se produit lors de l'analyse du projet.
     */
    public Map<String, Double> analyzeProject(String sourcePath) throws Exception {
        Map<String, Double> classCohesion = new HashMap<>();
    
        SourceRoot sourceRoot = new SourceRoot(Paths.get(sourcePath));
        sourceRoot.tryToParseParallelized();
    
        for (CompilationUnit cu : sourceRoot.getCompilationUnits()) {
            for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                if (cls.isPublic()) {
                    double cohesion = calculateCohesion(cls);
                    classCohesion.put(cls.getNameAsString(), cohesion);
                }
            }
        }
    
        return classCohesion;
    }
    


    /**
     * Calcule la cohésion d'une classe en fonction du nombre de méthodes qui accèdent aux champs de la classe.
     * La cohésion est définie comme le ratio entre le nombre de méthodes accédant aux champs et le nombre total de méthodes.
     *
     * @param cls La classe ou interface dont la cohésion doit être calculée.
     * @return La cohésion de la classe, un nombre entre 0 et 1, où 1 signifie que toutes les méthodes accèdent aux champs.
     */
   private double calculateCohesion(ClassOrInterfaceDeclaration cls) {
    long fieldAccessingMethods = 0;
    for (MethodDeclaration method : cls.getMethods()) {
        if (method.findAll(com.github.javaparser.ast.expr.FieldAccessExpr.class).size() > 0) {
            fieldAccessingMethods++;
        }
    }
    
    return (double) fieldAccessingMethods / cls.getMethods().size();
}


/**
     * Generate a histogram for class cohesion values.
     */
    private static void generateCohesionHistogram(Map<String, Map<String, Double>> allProjectCohesion) {
        for (Map.Entry<String, Map<String, Double>> project : allProjectCohesion.entrySet()) {
            Map<String, Double> cohesionValues = project.getValue();
            Map<Double, Integer> cohesionFrequency = new HashMap<>();
            
            for (Double cohesion : cohesionValues.values()) {
                cohesionFrequency.put(cohesion, cohesionFrequency.getOrDefault(cohesion, 0) + 1);
            }

            System.out.println("Cohesion Histogram for Project: " + project.getKey());
            cohesionFrequency.forEach((cohesion, count) -> 
                System.out.println("Cohesion " + cohesion + ": " + count + " classes")
            );
        }
    }

    /**
     * Generate and save the dependency graph in DOT format.
     */
    private static void generateDependencyGraphs(Map<String, Map<String, Set<String>>> allProjectDependencies) {
        for (Map.Entry<String, Map<String, Set<String>>> project : allProjectDependencies.entrySet()) {
            String projectName = project.getKey();
            Map<String, Set<String>> dependencies = project.getValue();
            StringBuilder dotGraph = new StringBuilder();
            dotGraph.append("digraph G {\n");

            for (Map.Entry<String, Set<String>> classDependencies : dependencies.entrySet()) {
                String className = classDependencies.getKey();
                Set<String> dependentClasses = classDependencies.getValue();
                for (String dependentClass : dependentClasses) {
                    dotGraph.append("\"").append(className).append("\" -> \"").append(dependentClass).append("\";\n");
                }
            }

            dotGraph.append("}\n");

            try (FileWriter writer = new FileWriter(projectName + "_dependency_graph.dot")) {
                writer.write(dotGraph.toString());
                System.out.println("Dependency graph saved to: " + projectName + "_dependency_graph.dot");
            } catch (Exception e) {
                System.err.println("Error saving dependency graph: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
