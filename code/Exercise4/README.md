# Code of your exercise

Le code de cet exercice ainsi que les captures d'écran nécessaires sont fournis ci-dessous.


```java

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




```

Field: longOption, Class: OptionBuilder, Package: org.apache.commons.cli
Field: description, Class: OptionBuilder, Package: org.apache.commons.cli
Field: argName, Class: OptionBuilder, Package: org.apache.commons.cli
Field: required, Class: OptionBuilder, Package: org.apache.commons.cli
Field: argCount, Class: OptionBuilder, Package: org.apache.commons.cli
Field: type, Class: OptionBuilder, Package: org.apache.commons.cli
Field: optionalArg, Class: OptionBuilder, Package: org.apache.commons.cli
Field: valueSeparator, Class: OptionBuilder, Package: org.apache.commons.cli
Field: INSTANCE, Class: OptionBuilder, Package: org.apache.commons.cli
Field: HEX_RADIX, Class: TypeHandler, Package: org.apache.commons.cli
Field: converterMap, Class: TypeHandler, Package: org.apache.commons.cli
Field: deprecatedHandler, Class: CommandLine, Package: org.apache.commons.cli
Field: serialVersionUID, Class: CommandLine, Package: org.apache.commons.cli
Field: deprecatedHandler, Class: CommandLine, Package: org.apache.commons.cli
Field: args, Class: Builder, Package: org.apache.commons.cli
Field: options, Class: Builder, Package: org.apache.commons.cli
Field: deprecatedHandler, Class: Builder, Package: org.apache.commons.cli
Field: serialVersionUID, Class: MissingArgumentException, Package: org.apache.commons.cli
Field: comparator, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: helpAppendable, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: optionFormatBuilder, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: optionGroupSeparator, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: comparator, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: helpAppendable, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: optionFormatBuilder, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: optionGroupSeparator, Class: AbstractHelpFormatter, Package: org.apache.commons.cli.help
Field: comparator, Class: Builder, Package: org.apache.commons.cli.help
Field: helpAppendable, Class: Builder, Package: org.apache.commons.cli.help
Field: optionFormatBuilder, Class: Builder, Package: org.apache.commons.cli.help
Field: optionGroupSeparator, Class: Builder, Package: org.apache.commons.cli.help
Field: DEFAULT_TYPE, Class: Option, Package: org.apache.commons.cli
Field: argCount, Class: Option, Package: org.apache.commons.cli
Field: longOption, Class: Option, Package: org.apache.commons.cli
Field: option, Class: Option, Package: org.apache.commons.cli
Field: optionalArg, Class: Option, Package: org.apache.commons.cli
Field: required, Class: Option, Package: org.apache.commons.cli
Field: serialVersionUID, Class: Option, Package: org.apache.commons.cli
Field: argCount, Class: Option, Package: org.apache.commons.cli
Field: longOption, Class: Option, Package: org.apache.commons.cli
Field: option, Class: Option, Package: org.apache.commons.cli
Field: optionalArg, Class: Option, Package: org.apache.commons.cli
Field: required, Class: Option, Package: org.apache.commons.cli
Field: DEFAULT_TYPE, Class: Builder, Package: org.apache.commons.cli
Field: argCount, Class: Builder, Package: org.apache.commons.cli
Field: argName, Class: Builder, Package: org.apache.commons.cli
Field: converter, Class: Builder, Package: org.apache.commons.cli
Field: deprecated, Class: Builder, Package: org.apache.commons.cli
Field: description, Class: Builder, Package: org.apache.commons.cli
Field: longOption, Class: Builder, Package: org.apache.commons.cli
Field: option, Class: Builder, Package: org.apache.commons.cli
Field: optionalArg, Class: Builder, Package: org.apache.commons.cli
Field: required, Class: Builder, Package: org.apache.commons.cli
Field: since, Class: Builder, Package: org.apache.commons.cli
Field: type, Class: Builder, Package: org.apache.commons.cli
Field: valueSeparator, Class: Builder, Package: org.apache.commons.cli
Field: tokens, Class: PosixParser, Package: org.apache.commons.cli
Field: eatTheRest, Class: PosixParser, Package: org.apache.commons.cli
Field: currentOption, Class: PosixParser, Package: org.apache.commons.cli
Field: options, Class: PosixParser, Package: org.apache.commons.cli
Field: serialVersionUID, Class: Options, Package: org.apache.commons.cli
Field: shortOpts, Class: Options, Package: org.apache.commons.cli
Field: longOpts, Class: Options, Package: org.apache.commons.cli
Field: requiredOpts, Class: Options, Package: org.apache.commons.cli
Field: optionGroups, Class: Options, Package: org.apache.commons.cli
Field: showSince, Class: HelpFormatter, Package: org.apache.commons.cli.help
Field: showSince, Class: HelpFormatter, Package: org.apache.commons.cli.help
Field: showSince, Class: Builder, Package: org.apache.commons.cli.help
Field: serialVersionUID, Class: AlreadySelectedException, Package: org.apache.commons.cli
Field: group, Class: AlreadySelectedException, Package: org.apache.commons.cli
Field: serialVersionUID, Class: AmbiguousOptionException, Package: org.apache.commons.cli
Field: UNSUPPORTED, Class: PatternOptionBuilder, Package: org.apache.commons.cli
Field: allowPartialMatching, Class: DefaultParser, Package: org.apache.commons.cli
Field: deprecatedHandler, Class: DefaultParser, Package: org.apache.commons.cli
Field: stripLeadingAndTrailingQuotes, Class: DefaultParser, Package: org.apache.commons.cli
Field: allowPartialMatching, Class: DefaultParser, Package: org.apache.commons.cli
Field: stripLeadingAndTrailingQuotes, Class: DefaultParser, Package: org.apache.commons.cli
Field: deprecatedHandler, Class: DefaultParser, Package: org.apache.commons.cli
Field: allowPartialMatching, Class: Builder, Package: org.apache.commons.cli
Field: deprecatedHandler, Class: Builder, Package: org.apache.commons.cli
Field: stripLeadingAndTrailingQuotes, Class: Builder, Package: org.apache.commons.cli
Field: scalable, Class: TextStyle, Package: org.apache.commons.cli.help
Field: scalable, Class: TextStyle, Package: org.apache.commons.cli.help
Field: alignment, Class: Builder, Package: org.apache.commons.cli.help
Field: scalable, Class: Builder, Package: org.apache.commons.cli.help
Field: serialVersionUID, Class: OptionGroup, Package: org.apache.commons.cli
Field: optionMap, Class: OptionGroup, Package: org.apache.commons.cli
Field: required, Class: OptionGroup, Package: org.apache.commons.cli
Field: serialVersionUID, Class: MissingOptionException, Package: org.apache.commons.cli
Field: serialVersionUID, Class: UnrecognizedOptionException, Package: org.apache.commons.cli
Field: options, Class: Parser, Package: org.apache.commons.cli
Field: requiredOptions, Class: Parser, Package: org.apache.commons.cli
Field: serialVersionUID, Class: ParseException, Package: org.apache.commons.cli
Field: BLANK_LINE, Class: TextHelpAppendable, Package: org.apache.commons.cli.help
Field: BREAK_CHAR_SET, Class: TextHelpAppendable, Package: org.apache.commons.cli.help
Field: argNameDelimiters, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: defaultArgName, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: deprecatedFormatFunction, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: longOptPrefix, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optPrefix, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optSeparator, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optArgSeparator, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optionalDelimiters, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: syntaxFormatFunction, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: DEFAULT_OPTIONAL_DELIMITERS, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: DEFAULT_ARG_NAME_DELIMITERS, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: argNameDelimiters, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: defaultArgName, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: deprecatedFormatFunction, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: longOptPrefix, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optPrefix, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optSeparator, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optArgSeparator, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: optionalDelimiters, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: syntaxFormatFunction, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: option, Class: OptionFormatter, Package: org.apache.commons.cli.help
Field: argNameDelimiters, Class: Builder, Package: org.apache.commons.cli.help
Field: defaultArgName, Class: Builder, Package: org.apache.commons.cli.help
Field: deprecatedFormatFunction, Class: Builder, Package: org.apache.commons.cli.help
Field: longOptPrefix, Class: Builder, Package: org.apache.commons.cli.help
Field: optPrefix, Class: Builder, Package: org.apache.commons.cli.help
Field: optSeparator, Class: Builder, Package: org.apache.commons.cli.help
Field: optArgSeparator, Class: Builder, Package: org.apache.commons.cli.help
Field: optionalDelimiters, Class: Builder, Package: org.apache.commons.cli.help
Field: syntaxFormatFunction, Class: Builder, Package: org.apache.commons.cli.help
Field: forRemoval, Class: DeprecatedAttributes, Package: org.apache.commons.cli
Field: EMPTY_STRING, Class: DeprecatedAttributes, Package: org.apache.commons.cli
Field: forRemoval, Class: DeprecatedAttributes, Package: org.apache.commons.cli
Field: description, Class: Builder, Package: org.apache.commons.cli
Field: forRemoval, Class: Builder, Package: org.apache.commons.cli
Field: since, Class: Builder, Package: org.apache.commons.cli
Field: DEFAULT_DEPRECATED_FORMAT, Class: HelpFormatter, Package: org.apache.commons.cli
Field: deprecatedFormatFunction, Class: HelpFormatter, Package: org.apache.commons.cli
Field: printStream, Class: HelpFormatter, Package: org.apache.commons.cli
Field: showSince, Class: HelpFormatter, Package: org.apache.commons.cli
Field: serialVersionUID, Class: HelpFormatter, Package: org.apache.commons.cli
Field: HEADER_OPTIONS, Class: HelpFormatter, Package: org.apache.commons.cli
Field: HEADER_SINCE, Class: HelpFormatter, Package: org.apache.commons.cli
Field: HEADER_DESCRIPTION, Class: HelpFormatter, Package: org.apache.commons.cli
Field: deprecatedFormatFunction, Class: HelpFormatter, Package: org.apache.commons.cli
Field: printWriter, Class: HelpFormatter, Package: org.apache.commons.cli
Field: showSince, Class: HelpFormatter, Package: org.apache.commons.cli
Field: DEFAULT_DEPRECATED_FORMAT, Class: Builder, Package: org.apache.commons.cli
Field: deprecatedFormatFunction, Class: Builder, Package: org.apache.commons.cli
Field: printStream, Class: Builder, Package: org.apache.commons.cli
Field: showSince, Class: Builder, Package: org.apache.commons.cli