package fr.istic.vv.Exercise4;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrivateFieldAnalyzer {


     /**
     * Cette classe représente l'information concernant un champ privé sans getter.
     * Elle contient le nom du champ, le nom de la classe qui le contient et le nom du package.
     */
    private static class PrivateFieldInfo {
        String fieldName;
        String className;
        String packageName;


         /**
         * Constructeur pour initialiser un objet PrivateFieldInfo.
         * 
         * @param fieldName  Le nom du champ privé.
         * @param className  Le nom de la classe contenant le champ.
         * @param packageName Le nom du package contenant la classe.
         */
        PrivateFieldInfo(String fieldName, String className, String packageName) {
            this.fieldName = fieldName;
            this.className = className;
            this.packageName = packageName;
        }
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java PrivateFieldAnalyzer <project_source_path>");
            System.exit(1);
        }
    
        String sourcePath = args[0];
    
        try {
            List<PrivateFieldInfo> privateFieldsWithoutGetters = findPrivateFieldsWithoutGetters(sourcePath);
    
            for (PrivateFieldInfo field : privateFieldsWithoutGetters) {
                System.out.println(String.format("Field: %s, Class: %s, Package: %s", 
                    field.fieldName, field.className, field.packageName));
            }
            System.out.println("Analysis complete.");
        } catch (IOException e) {
            System.err.println("Error analyzing project: " + e.getMessage());
            e.printStackTrace();
        }
    }
    



    /**
     * Cette méthode parcourt tous les fichiers source dans le répertoire donné et recherche
     * les champs privés qui n'ont pas de getters publics.
     * 
     * @param sourcePath Le chemin du projet source à analyser.
     * @return Une liste de PrivateFieldInfo contenant les informations des champs privés sans getters.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'analyse des fichiers.
     */
    private static List<PrivateFieldInfo> findPrivateFieldsWithoutGetters(String sourcePath) throws IOException {
        List<PrivateFieldInfo> privateFieldsWithoutGetters = new ArrayList<>();
        SourceRoot root = new SourceRoot(Paths.get(sourcePath));
        root.tryToParseParallelized();
    
        for (CompilationUnit cu : root.getCompilationUnits()) {
            for (ClassOrInterfaceDeclaration classDeclaration : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                if (classDeclaration.isPublic()) {
                    String packageName = cu.getPackageDeclaration()
                            .map(p -> p.getName().asString())
                            .orElse("default");
    
                    List<String> existingGetters = new ArrayList<>();
                    for (MethodDeclaration method : classDeclaration.getMethods()) {
                        if (method.isPublic() && method.getName().asString().startsWith("get") &&
                            method.getBody().isPresent() && isSimpleGetter(method)) {
                            String getterName = method.getName().asString().substring(3).toLowerCase();
                            existingGetters.add(getterName);
                        }
                    }
    
                    for (FieldDeclaration field : classDeclaration.findAll(FieldDeclaration.class)) {
                        if (field.isPrivate()) {
                            String fieldName = field.getVariables().get(0).getName().asString().toLowerCase();
                            if (!existingGetters.contains(fieldName)) {
                                privateFieldsWithoutGetters.add(new PrivateFieldInfo(
                                        field.getVariables().get(0).getName().asString(),
                                        classDeclaration.getNameAsString(),
                                        packageName
                                ));
                            }
                        }
                    }
                }
            }
        }
    
        return privateFieldsWithoutGetters;
    }
    

      /**
     * Cette méthode vérifie si une méthode est un getter simple, c'est-à-dire qu'elle retourne
     * une valeur sans paramètre et contient un simple "return".
     * 
     * @param method La méthode à vérifier.
     * @return true si la méthode est un getter simple, false sinon.
     */
    private static boolean isSimpleGetter(MethodDeclaration method) {
        try {
            String methodBody = method.getBody().get().toString();
            return methodBody.contains("return") && method.getParameters().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
