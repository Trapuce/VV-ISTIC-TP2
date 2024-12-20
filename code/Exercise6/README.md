# Code of your exercise

Le code de cet exercice ainsi que les captures d'écran nécessaires sont fournis ci-dessous.
![Capture d'écran du 2024-12-20 17-23-52](./Capture%20d’écran%20du%202024-12-20%2017-23-52.png)

![Very Large Graph](./very_large_graph.png)


```java
package fr.istic.vv.Exercise6;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
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

            Map<String, Map<String, Double>> allProjectCohesion = new HashMap<>();
            allProjectCohesion.put(sourcePath, classCohesion);
            generateCohesionHistogram(allProjectCohesion);

            Map<String, Set<String>> projectDependencies = calculator.findDependencies(sourcePath);
            Map<String, Map<String, Set<String>>> allProjectDependencies = new HashMap<>();
            allProjectDependencies.put(sourcePath, projectDependencies);
            
            generateDependencyGraphs(allProjectDependencies);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Analyse le projet source et calcule la cohésion pour chaque classe publique.
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
     * Trouve toutes les dépendances entre les classes du projet.
     */
    public Map<String, Set<String>> findDependencies(String sourcePath) throws Exception {
        Map<String, Set<String>> dependencies = new HashMap<>();
        
        SourceRoot sourceRoot = new SourceRoot(Paths.get(sourcePath));
        sourceRoot.tryToParseParallelized();
        
        for (CompilationUnit cu : sourceRoot.getCompilationUnits()) {
            for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                if (cls.isPublic()) {
                    Set<String> classDependencies = new HashSet<>();
                    
                    cls.getFields().forEach(field -> {
                        field.getVariable(0).getType().ifClassOrInterfaceType(type -> {
                            classDependencies.add(type.getNameAsString());
                        });
                    });
                    
                    cls.getMethods().forEach(method -> {
                        method.findAll(ClassOrInterfaceType.class).forEach(type -> {
                            classDependencies.add(type.getNameAsString());
                        });
                    });
                    
                    dependencies.put(cls.getNameAsString(), classDependencies);
                }
            }
        }
        
        return dependencies;
    }

    /**
     * Calcule la cohésion d'une classe en fonction du nombre de méthodes qui accèdent aux champs.
     */
    private double calculateCohesion(ClassOrInterfaceDeclaration cls) {
        if (cls.getMethods().isEmpty()) {
            return 0.0;
        }

        long fieldAccessingMethods = 0;
        for (MethodDeclaration method : cls.getMethods()) {
            if (method.findAll(com.github.javaparser.ast.expr.FieldAccessExpr.class).size() > 0) {
                fieldAccessingMethods++;
            }
        }
        
        return (double) fieldAccessingMethods / cls.getMethods().size();
    }

    /**
     * Génère un histogramme des valeurs de cohésion.
     */
    private static void generateCohesionHistogram(Map<String, Map<String, Double>> allProjectCohesion) {
        for (Map.Entry<String, Map<String, Double>> project : allProjectCohesion.entrySet()) {
            Map<String, Double> cohesionValues = project.getValue();
            Map<Double, Integer> cohesionFrequency = new HashMap<>();
            
            for (Double cohesion : cohesionValues.values()) {
                cohesionFrequency.merge(cohesion, 1, Integer::sum);
            }

            System.out.println("Cohesion Histogram for Project: " + project.getKey());
            cohesionFrequency.forEach((cohesion, count) -> 
                System.out.println("Cohesion " + cohesion + ": " + count + " classes")
            );
        }
    }

    /**
     * Génère et sauvegarde le graphe de dépendances au format DOT.
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
```

Method complexities for project: /home/daouda-traore/Téléchargements/commons-cli-master/src/main/java/org/apache/commons/cli
DefaultParser.handleConcatenatedOptions(String token) : Cyclomatic Complexity = 5
Option.setDescription(String description) : Cyclomatic Complexity = 1
DefaultParser.parse(Options options, String[] arguments, Properties properties) : Cyclomatic Complexity = 1
HelpFormatter.getLongOptPrefix() : Cyclomatic Complexity = 1
Parser.parse(Options options, String[] arguments, Properties properties) : Cyclomatic Complexity = 1
HelpFormatter.printUsage(PrintWriter pw, int width, String app, Options options) : Cyclomatic Complexity = 6
AmbiguousOptionException.getMatchingOptions() : Cyclomatic Complexity = 1
OptionFormatter.isRequired() : Cyclomatic Complexity = 1
OptionBuilder.isRequired() : Cyclomatic Complexity = 1
Option.setValueSeparator(char valueSeparator) : Cyclomatic Complexity = 1
OptionFormatter.setArgumentNameDelimiters(String begin, String end) : Cyclomatic Complexity = 1
HelpAppendable.appendHeader(int level, CharSequence text) : Cyclomatic Complexity = 1
DefaultParser.setDeprecatedHandler(Consumer<Option> deprecatedHandler) : Cyclomatic Complexity = 1
Parser.setOptions(Options options) : Cyclomatic Complexity = 1
Option.hasArg() : Cyclomatic Complexity = 1
Options.getOptionGroup(Option opt) : Cyclomatic Complexity = 1
HelpFormatter.rtrim(String s) : Cyclomatic Complexity = 3
CommandLine.getParsedOptionValue(Option option, Supplier<T> defaultValue) : Cyclomatic Complexity = 3
HelpFormatter.setLongOptSeparator(String longOptSeparator) : Cyclomatic Complexity = 1
PosixParser.burstToken(String token, boolean stopAtNonOption) : Cyclomatic Complexity = 5
HelpFormatter.appendOptions(A sb, int width, Options options, int leftPad, int descPad) : Cyclomatic Complexity = 17
HelpFormatter.determineMaxSinceLength(Options options) : Cyclomatic Complexity = 3
CommandLine.iterator() : Cyclomatic Complexity = 1
OptionBuilder.reset() : Cyclomatic Complexity = 1
OptionBuilder.withValueSeparator() : Cyclomatic Complexity = 1
HelpFormatter.getTableDefinition(Iterable<Option> options) : Cyclomatic Complexity = 5
CommandLine.getParsedOptionValue(OptionGroup optionGroup) : Cyclomatic Complexity = 1
FilterHelpAppendable.append(CharSequence text) : Cyclomatic Complexity = 1
Options.hasShortOption(String opt) : Cyclomatic Complexity = 1
OptionGroup.isRequired() : Cyclomatic Complexity = 1
DefaultParser.get() : Cyclomatic Complexity = 1
TextHelpAppendable.systemOut() : Cyclomatic Complexity = 1
HelpFormatter.appendWrappedText(A appendable, int width, int nextLineTabStop, String text) : Cyclomatic Complexity = 6
DefaultParser.handleUnknownToken(String token) : Cyclomatic Complexity = 3
DeprecatedAttributes.isForRemoval() : Cyclomatic Complexity = 1
CommandLine.hasOption(OptionGroup optionGroup) : Cyclomatic Complexity = 2
OptionGroup.getOptions() : Cyclomatic Complexity = 1
DeprecatedAttributes.builder() : Cyclomatic Complexity = 1
TextStyle.builder() : Cyclomatic Complexity = 1
CommandLine.get(Supplier<T> supplier) : Cyclomatic Complexity = 2
CommandLine.getParsedOptionValue(Option option) : Cyclomatic Complexity = 1
OptionBuilder.withLongOpt(String newLongopt) : Cyclomatic Complexity = 1
DefaultParser.isArgument(String token) : Cyclomatic Complexity = 1
OptionFormatter.setSyntaxFormatFunction(BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction) : Cyclomatic Complexity = 1
OptionGroup.setSelected(Option option) : Cyclomatic Complexity = 3
TextHelpAppendable.appendParagraph(CharSequence paragraph) : Cyclomatic Complexity = 2
HelpFormatter.getDescPadding() : Cyclomatic Complexity = 1
Option.valueSeparator(char valueSeparator) : Cyclomatic Complexity = 1
DefaultParser.isNegativeNumber(String token) : Cyclomatic Complexity = 1
TextStyle.get() : Cyclomatic Complexity = 1
TextHelpAppendable.appendHeader(int level, CharSequence text) : Cyclomatic Complexity = 3
Option.getValue(String defaultValue) : Cyclomatic Complexity = 2
Util.stripLeadingHyphens(String str) : Cyclomatic Complexity = 4
TextStyle.getAlignment() : Cyclomatic Complexity = 1
Option.getOpt() : Cyclomatic Complexity = 1
OptionBuilder.withType(Object newType) : Cyclomatic Complexity = 1
FilterHelpAppendable.append(CharSequence csq, int start, int end) : Cyclomatic Complexity = 1
Option.hashCode() : Cyclomatic Complexity = 1
CommandLine.hasOption(char optionChar) : Cyclomatic Complexity = 1
Option.clone() : Cyclomatic Complexity = 1
CommandLineParser.parse(Options options, String[] arguments, boolean stopAtNonOption) : Cyclomatic Complexity = 1
CommandLine.getOptionValue(Option option, String defaultValue) : Cyclomatic Complexity = 1
AbstractHelpFormatter.sort(Iterable<Option> options) : Cyclomatic Complexity = 2
TextHelpAppendable.resize(TextStyle.Builder builder, double fraction) : Cyclomatic Complexity = 3
OptionFormatter.getOpt() : Cyclomatic Complexity = 2
CommandLine.getOptionValue(OptionGroup optionGroup, Supplier<String> defaultValue) : Cyclomatic Complexity = 2
CommandLine.getOptions() : Cyclomatic Complexity = 1
HelpFormatter.printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) : Cyclomatic Complexity = 1
Options.addOption(String opt, boolean hasArg, String description) : Cyclomatic Complexity = 1
HelpFormatter.getOptPrefix() : Cyclomatic Complexity = 1
Option.setOptionalArg(boolean optionalArg) : Cyclomatic Complexity = 1
HelpFormatter.setLongOptPrefix(String prefix) : Cyclomatic Complexity = 1
Option.toString() : Cyclomatic Complexity = 5
OptionFormatter.toSyntaxOption() : Cyclomatic Complexity = 1
FilterHelpAppendable.append(char ch) : Cyclomatic Complexity = 1
AlreadySelectedException.getOption() : Cyclomatic Complexity = 1
Parser.parse(Options options, String[] arguments, boolean stopAtNonOption) : Cyclomatic Complexity = 1
OptionBuilder.withArgName(String name) : Cyclomatic Complexity = 1
AlreadySelectedException.getOptionGroup() : Cyclomatic Complexity = 1
Option.option(String option) : Cyclomatic Complexity = 1
Option.clearValues() : Cyclomatic Complexity = 1
TextStyle.getMaxWidth() : Cyclomatic Complexity = 1
Option.hasNoValues() : Cyclomatic Complexity = 1
TextHelpAppendable.writeColumnQueues(List<Queue<String>> columnQueues, List<TextStyle> styles) : Cyclomatic Complexity = 4
Option.hasArgName() : Cyclomatic Complexity = 1
PatternOptionBuilder.getValueType(char ch) : Cyclomatic Complexity = 10
Option.isRequired() : Cyclomatic Complexity = 1
OptionGroup.setRequired(boolean required) : Cyclomatic Complexity = 1
HelpFormatter.printHelp(String cmdLineSyntax, Options options) : Cyclomatic Complexity = 1
Option.optionalArg(boolean optionalArg) : Cyclomatic Complexity = 2
TextHelpAppendable.appendList(boolean ordered, Collection<CharSequence> list) : Cyclomatic Complexity = 3
Options.addOptions(Options options) : Cyclomatic Complexity = 2
OptionGroup.isSelected() : Cyclomatic Complexity = 1
AbstractHelpFormatter.setComparator(Comparator<Option> comparator) : Cyclomatic Complexity = 1
TextHelpAppendable.makeColumnQueue(CharSequence columnData, TextStyle style) : Cyclomatic Complexity = 4
HelpFormatter.getNewLine() : Cyclomatic Complexity = 1
AbstractHelpFormatter.printOptions(Options options) : Cyclomatic Complexity = 1
OptionFormatter.setOptArgSeparator(String optArgSeparator) : Cyclomatic Complexity = 1
DeprecatedAttributes.getDescription() : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(String optionName) : Cyclomatic Complexity = 1
AbstractHelpFormatter.getOptionFormatter(Option option) : Cyclomatic Complexity = 1
HelpFormatter.findWrapPos(String text, int width, int startPos) : Cyclomatic Complexity = 8
OptionBuilder.withValueSeparator(char sep) : Cyclomatic Complexity = 1
Option.getSince() : Cyclomatic Complexity = 1
AbstractHelpFormatter.toSyntaxOptions(Iterable<Option> options, Function<Option, OptionGroup> lookup) : Cyclomatic Complexity = 3
TextStyle.getIndent() : Cyclomatic Complexity = 1
Option.required() : Cyclomatic Complexity = 1
GnuParser.flatten(Options options, String[] arguments, boolean stopAtNonOption) : Cyclomatic Complexity = 11
DefaultParser.isJavaProperty(String token) : Cyclomatic Complexity = 2
AbstractHelpFormatter.getOptionGroupSeparator() : Cyclomatic Complexity = 1
Option.hasArgs() : Cyclomatic Complexity = 1
CommandLine.builder() : Cyclomatic Complexity = 1
UnrecognizedOptionException.getOption() : Cyclomatic Complexity = 1
HelpFormatter.setDescPadding(int padding) : Cyclomatic Complexity = 1
MissingOptionException.getMissingOptions() : Cyclomatic Complexity = 1
TextStyle.getLeftPad() : Cyclomatic Complexity = 1
HelpFormatter.printHelp(String cmdLineSyntax, Options options, boolean autoUsage) : Cyclomatic Complexity = 1
DeprecatedAttributes.setSince(String since) : Cyclomatic Complexity = 1
OptionBuilder.hasArg() : Cyclomatic Complexity = 1
CommandLine.getArgList() : Cyclomatic Complexity = 1
HelpFormatter.printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer) : Cyclomatic Complexity = 1
CommandLine.hasOption(Option option) : Cyclomatic Complexity = 2
OptionFormatter.toOptional(String text) : Cyclomatic Complexity = 2
Option.getType() : Cyclomatic Complexity = 1
DefaultParser.parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption) : Cyclomatic Complexity = 2
Parser.parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption) : Cyclomatic Complexity = 13
OptionBuilder.hasArg(boolean hasArg) : Cyclomatic Complexity = 2
Option.getId() : Cyclomatic Complexity = 1
OptionFormatter.setLongOptPrefix(String prefix) : Cyclomatic Complexity = 1
OptionBuilder.create() : Cyclomatic Complexity = 2
CommandLineParser.parse(Options options, String[] arguments) : Cyclomatic Complexity = 1
OptionFormatter.getBothOpt() : Cyclomatic Complexity = 2
DefaultParser.handleProperties(Properties properties) : Cyclomatic Complexity = 8
HelpFormatter.setOptionComparator(Comparator<Option> comparator) : Cyclomatic Complexity = 1
TextStyle.toString() : Cyclomatic Complexity = 2
TextHelpAppendable.resize(int orig, double fraction) : Cyclomatic Complexity = 1
DefaultParser.handleLongOption(String token) : Cyclomatic Complexity = 2
Option.getDescription() : Cyclomatic Complexity = 1
HelpFormatter.getArgName() : Cyclomatic Complexity = 1
TextHelpAppendable.getMaxWidth() : Cyclomatic Complexity = 1
MissingOptionException.createMessage(List<?> missingOptions) : Cyclomatic Complexity = 4
AbstractHelpFormatter.printOptions(TableDefinition tableDefinition) : Cyclomatic Complexity = 1
Util.stripLeadingAndTrailingQuotes(String str) : Cyclomatic Complexity = 3
TableDefinition.columnTextStyles() : Cyclomatic Complexity = 1
OptionBuilder.hasArgs() : Cyclomatic Complexity = 1
CommandLine.resolveOption(String optionName) : Cyclomatic Complexity = 3
HelpFormatter.setPrintWriter(PrintWriter printWriter) : Cyclomatic Complexity = 1
MissingArgumentException.getOption() : Cyclomatic Complexity = 1
AbstractHelpFormatter.setOptionGroupSeparator(String optionGroupSeparator) : Cyclomatic Complexity = 1
HelpFormatter.setShowSince(boolean showSince) : Cyclomatic Complexity = 1
CommandLine.getOptionValues(Option option) : Cyclomatic Complexity = 5
CommandLine.setDeprecatedHandler(Consumer<Option> deprecatedHandler) : Cyclomatic Complexity = 1
AbstractHelpFormatter.getComparator() : Cyclomatic Complexity = 1
DefaultParser.setStripLeadingAndTrailingQuotes(Boolean stripLeadingAndTrailingQuotes) : Cyclomatic Complexity = 1
Parser.processOption(String arg, ListIterator<String> iter) : Cyclomatic Complexity = 3
AbstractHelpFormatter.getSerializer() : Cyclomatic Complexity = 1
Option.required(boolean required) : Cyclomatic Complexity = 1
CommandLine.hasOption(String optionName) : Cyclomatic Complexity = 1
Option.builder() : Cyclomatic Complexity = 1
Option.getLongOpt() : Cyclomatic Complexity = 1
Options.hasOption(String opt) : Cyclomatic Complexity = 1
OptionGroup.getNames() : Cyclomatic Complexity = 1
Option.getKey() : Cyclomatic Complexity = 2
Option.getValue() : Cyclomatic Complexity = 2
AbstractHelpFormatter.setHelpAppendable(HelpAppendable helpAppendable) : Cyclomatic Complexity = 2
DefaultParser.checkRequiredOptions() : Cyclomatic Complexity = 2
HelpFormatter.createDefaultPrintWriter() : Cyclomatic Complexity = 1
HelpFormatter.getLongOptSeparator() : Cyclomatic Complexity = 1
DefaultParser.builder() : Cyclomatic Complexity = 1
Converter.apply(String string) : Cyclomatic Complexity = 1
DefaultParser.parse(Options options, String[] arguments) : Cyclomatic Complexity = 1
PosixParser.gobble(Iterator<String> iter) : Cyclomatic Complexity = 3
TextStyle.getMinWidth() : Cyclomatic Complexity = 1
AbstractHelpFormatter.printHelp(String cmdLineSyntax, String header, Iterable<Option> options, String footer, boolean autoUsage) : Cyclomatic Complexity = 5
TextStyle.isScalable() : Cyclomatic Complexity = 1
Option.deprecated(DeprecatedAttributes deprecated) : Cyclomatic Complexity = 1
Option.getArgs() : Cyclomatic Complexity = 1
HelpFormatter.renderWrappedText(StringBuffer sb, int width, int nextLineTabStop, String text) : Cyclomatic Complexity = 1
PatternOptionBuilder.getValueClass(char ch) : Cyclomatic Complexity = 1
OptionValidator.isValidOpt(char c) : Cyclomatic Complexity = 1
Util.isEmpty(String str) : Cyclomatic Complexity = 1
TableDefinition.from(String caption, List<TextStyle> columnStyle, List<String> headers, Iterable<List<String>> rows) : Cyclomatic Complexity = 1
HelpFormatter.compare(Option opt1, Option opt2) : Cyclomatic Complexity = 1
OptionBuilder.hasOptionalArg() : Cyclomatic Complexity = 1
CommandLine.addArg(String arg) : Cyclomatic Complexity = 2
Options.getOptions() : Cyclomatic Complexity = 1
DeprecatedAttributes.get() : Cyclomatic Complexity = 1
CommandLine.getOptionObject(char optionChar) : Cyclomatic Complexity = 1
Options.getOptionGroups() : Cyclomatic Complexity = 1
HelpFormatter.setNewLine(String newline) : Cyclomatic Complexity = 1
Option.numberOfArgs(int argCount) : Cyclomatic Complexity = 1
Parser.processProperties(Properties properties) : Cyclomatic Complexity = 8
CommandLine.getParsedOptionValue(String optionName) : Cyclomatic Complexity = 1
AbstractHelpFormatter.setSyntaxPrefix(String prefix) : Cyclomatic Complexity = 1
Option.deprecated() : Cyclomatic Complexity = 1
HelpAppendable.appendFormat(String format, Object args) : Cyclomatic Complexity = 1
BasicParser.flatten(Options options, String[] arguments, boolean stopAtNonOption) : Cyclomatic Complexity = 1
TextHelpAppendable.setIndent(int indent) : Cyclomatic Complexity = 1
TextHelpAppendable.indexOfWrap(CharSequence text, int width, int startPos) : Cyclomatic Complexity = 8
OptionGroup.addOption(Option option) : Cyclomatic Complexity = 1
Option.acceptsArg() : Cyclomatic Complexity = 1
CommandLine.getOptionValue(OptionGroup optionGroup, String defaultValue) : Cyclomatic Complexity = 1
PatternOptionBuilder.parsePattern(String pattern) : Cyclomatic Complexity = 6
HelpFormatter.setSyntaxPrefix(String prefix) : Cyclomatic Complexity = 1
HelpFormatter.getDescription(Option option) : Cyclomatic Complexity = 2
Option.getValueSeparator() : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(Option option, T[] defaultValue) : Cyclomatic Complexity = 1
AmbiguousOptionException.createMessage(String option, Collection<String> matchingOptions) : Cyclomatic Complexity = 3
HelpAppendable.appendList(boolean ordered, Collection<CharSequence> list) : Cyclomatic Complexity = 1
DefaultParser.stripLeadingAndTrailingQuotesDefaultOff(String token) : Cyclomatic Complexity = 2
TextStyle.pad(boolean addIndent, CharSequence text) : Cyclomatic Complexity = 12
HelpFormatter.getLeftPadding() : Cyclomatic Complexity = 1
Options.addOption(String opt, String longOpt, boolean hasArg, String description) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(OptionGroup optionGroup) : Cyclomatic Complexity = 1
Options.addRequiredOption(String opt, String longOpt, boolean hasArg, String description) : Cyclomatic Complexity = 1
Option.setType(Object type) : Cyclomatic Complexity = 1
HelpAppendable.appendParagraphFormat(String format, Object args) : Cyclomatic Complexity = 1
Option.requiresArg() : Cyclomatic Complexity = 3
TextHelpAppendable.appendTable(TableDefinition rawTable) : Cyclomatic Complexity = 1
PosixParser.init() : Cyclomatic Complexity = 1
AbstractHelpFormatter.toSyntaxOptions(OptionGroup group) : Cyclomatic Complexity = 5
Option.toDeprecatedString() : Cyclomatic Complexity = 3
Option.builder(String option) : Cyclomatic Complexity = 1
CommandLine.getOptionValue(String optionName, Supplier<String> defaultValue) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(char optionChar, T[] defaultValue) : Cyclomatic Complexity = 1
DefaultParser.build() : Cyclomatic Complexity = 1
TextStyle.setTextStyle(TextStyle style) : Cyclomatic Complexity = 1
HelpFormatter.createPadding(int len) : Cyclomatic Complexity = 1
TextStyle.setMaxWidth(int maxWidth) : Cyclomatic Complexity = 1
DefaultParser.getLongPrefix(String token) : Cyclomatic Complexity = 3
CommandLine.getOptionValue(char optionChar, Supplier<String> defaultValue) : Cyclomatic Complexity = 1
OptionBuilder.create(String opt) : Cyclomatic Complexity = 1
OptionValidator.search(char[] chars, char c) : Cyclomatic Complexity = 2
HelpFormatter.printHelp(String cmdLineSyntax, String header, Options options, String footer) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValue(char optionChar, Supplier<T> defaultValue) : Cyclomatic Complexity = 1
Option.isDeprecated() : Cyclomatic Complexity = 1
TextStyle.setAlignment(Alignment alignment) : Cyclomatic Complexity = 1
CommandLine.getArgs() : Cyclomatic Complexity = 1
Option.valueSeparator() : Cyclomatic Complexity = 1
HelpFormatter.renderWrappedTextBlock(A appendable, int width, int nextLineTabStop, String text) : Cyclomatic Complexity = 3
CommandLine.getParsedOptionValue(Option option, T defaultValue) : Cyclomatic Complexity = 1
Option.desc(String description) : Cyclomatic Complexity = 1
HelpFormatter.getWidth() : Cyclomatic Complexity = 1
HelpFormatter.setLeftPadding(int padding) : Cyclomatic Complexity = 1
OptionFormatter.getSince() : Cyclomatic Complexity = 1
DeprecatedAttributes.toEmpty(String since) : Cyclomatic Complexity = 2
CommandLine.get() : Cyclomatic Complexity = 1
Options.toString() : Cyclomatic Complexity = 1
Option.setArgName(String argName) : Cyclomatic Complexity = 1
TextStyle.setLeftPad(int leftPad) : Cyclomatic Complexity = 1
DefaultParser.handleToken(String token) : Cyclomatic Complexity = 8
TextHelpAppendable.makeColumnQueues(List<String> columnData, List<TextStyle> styles) : Cyclomatic Complexity = 2
Option.hasOptionalArg() : Cyclomatic Complexity = 1
AbstractHelpFormatter.getHelpAppendable() : Cyclomatic Complexity = 1
DefaultParser.handleLongOptionWithEqual(String token) : Cyclomatic Complexity = 5
DefaultParser.isShortOption(String token) : Cyclomatic Complexity = 4
HelpFormatter.setShowDeprecated(boolean useDefaultFormat) : Cyclomatic Complexity = 2
HelpFormatter.printHelp(int width, String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) : Cyclomatic Complexity = 1
OptionValidator.isValidChar(char c) : Cyclomatic Complexity = 1
HelpFormatter.setArgName(String name) : Cyclomatic Complexity = 1
DefaultParser.updateRequiredOptions(Option option) : Cyclomatic Complexity = 4
OptionFormatter.get() : Cyclomatic Complexity = 1
OptionGroup.getSelected() : Cyclomatic Complexity = 1
Option.processValue(String value) : Cyclomatic Complexity = 5
Parser.flatten(Options opts, String[] arguments, boolean stopAtNonOption) : Cyclomatic Complexity = 1
AbstractHelpFormatter.printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) : Cyclomatic Complexity = 1
OptionFormatter.getArgName() : Cyclomatic Complexity = 2
OptionGroup.toString() : Cyclomatic Complexity = 5
DeprecatedAttributes.toString() : Cyclomatic Complexity = 4
Options.addOption(Option opt) : Cyclomatic Complexity = 4
CommandLine.getOptionValue(String optionName, String defaultValue) : Cyclomatic Complexity = 1
Parser.getRequiredOptions() : Cyclomatic Complexity = 1
PatternOptionBuilder.unsupported() : Cyclomatic Complexity = 1
CommandLine.handleDeprecated(Option option) : Cyclomatic Complexity = 2
Option.addValue(String value) : Cyclomatic Complexity = 1
AbstractHelpFormatter.toSyntaxOptions(Iterable<Option> options) : Cyclomatic Complexity = 1
Option.setArgs(int num) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(String optionName, T[] defaultValue) : Cyclomatic Complexity = 1
CommandLine.getOptionValue(char optionChar, String defaultValue) : Cyclomatic Complexity = 1
Parser.updateRequiredOptions(Option opt) : Cyclomatic Complexity = 4
DefaultParser.indexOfEqual(String token) : Cyclomatic Complexity = 1
HelpFormatter.printWrapped(PrintWriter pw, int width, String text) : Cyclomatic Complexity = 1
TextHelpAppendable.getTextStyleBuilder() : Cyclomatic Complexity = 1
HelpFormatter.getSyntaxPrefix() : Cyclomatic Complexity = 1
AbstractHelpFormatter.sort(Options options) : Cyclomatic Complexity = 2
TextStyle.setScalable(boolean scalable) : Cyclomatic Complexity = 1
DeprecatedAttributes.getSince() : Cyclomatic Complexity = 1
PosixParser.processNonOptionToken(String value, boolean stopAtNonOption) : Cyclomatic Complexity = 2
OptionBuilder.hasOptionalArgs(int numArgs) : Cyclomatic Complexity = 1
OptionBuilder.hasArgs(int num) : Cyclomatic Complexity = 1
DefaultParser.handleShortAndLongOption(String hyphenToken) : Cyclomatic Complexity = 11
HelpFormatter.setShowDeprecated(Function<Option, String> deprecatedFormatFunction) : Cyclomatic Complexity = 1
HelpAppendable.appendTitle(CharSequence title) : Cyclomatic Complexity = 1
HelpFormatter.printUsage(PrintWriter pw, int width, String cmdLineSyntax) : Cyclomatic Complexity = 1
CommandLine.getOptionValues(char optionChar) : Cyclomatic Complexity = 1
OptionFormatter.toSyntaxOption(boolean isRequired) : Cyclomatic Complexity = 1
CommandLine.getOptionValue(Option option) : Cyclomatic Complexity = 2
AbstractHelpFormatter.toArgName(String argName) : Cyclomatic Complexity = 1
TextStyle.setMinWidth(int minWidth) : Cyclomatic Complexity = 1
Option.getValues() : Cyclomatic Complexity = 2
Option.hasArg(boolean hasArg) : Cyclomatic Complexity = 2
CommandLine.getParsedOptionValue(String optionName, T defaultValue) : Cyclomatic Complexity = 1
OptionFormatter.build(Option option) : Cyclomatic Complexity = 1
OptionFormatter.builder() : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValue(String optionName, Supplier<T> defaultValue) : Cyclomatic Complexity = 1
TextHelpAppendable.setMaxWidth(int maxWidth) : Cyclomatic Complexity = 1
TextHelpAppendable.appendTitle(CharSequence title) : Cyclomatic Complexity = 2
TextHelpAppendable.printWrapped(String text) : Cyclomatic Complexity = 1
Option.setRequired(boolean required) : Cyclomatic Complexity = 1
OptionBuilder.withDescription(String newDescription) : Cyclomatic Complexity = 1
TextHelpAppendable.setLeftPad(int leftPad) : Cyclomatic Complexity = 1
OptionFormatter.setOptSeparator(String optSeparator) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(OptionGroup optionGroup, Supplier<T[]> defaultValue) : Cyclomatic Complexity = 2
Util.isEmpty(Object[] array) : Cyclomatic Complexity = 1
CommandLine.build() : Cyclomatic Complexity = 1
Options.addOption(String opt, String description) : Cyclomatic Complexity = 1
OptionValidator.validate(String option) : Cyclomatic Complexity = 7
HelpFormatter.printOptions(PrintWriter pw, int width, Options options, int leftPad, int descPad) : Cyclomatic Complexity = 1
HelpFormatter.printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(char optionChar) : Cyclomatic Complexity = 1
Parser.parse(Options options, String[] arguments) : Cyclomatic Complexity = 1
TextHelpAppendable.getLeftPad() : Cyclomatic Complexity = 1
CommandLine.getOptionProperties(String optionName) : Cyclomatic Complexity = 2
HelpFormatter.appendOption(StringBuilder buff, Option option, boolean required) : Cyclomatic Complexity = 7
TableDefinition.headers() : Cyclomatic Complexity = 1
Option.longOpt(String longOption) : Cyclomatic Complexity = 1
Option.build() : Cyclomatic Complexity = 2
CommandLine.getOptionValue(OptionGroup optionGroup) : Cyclomatic Complexity = 2
AbstractHelpFormatter.setOptionFormatBuilder(OptionFormatter.Builder optionFormatBuilder) : Cyclomatic Complexity = 2
Parser.checkRequiredOptions() : Cyclomatic Complexity = 2
Option.hasLongOpt() : Cyclomatic Complexity = 1
OptionFormatter.setOptionalDelimiters(String begin, String end) : Cyclomatic Complexity = 1
Option.add(String value) : Cyclomatic Complexity = 2
AbstractHelpFormatter.getOptionFormatBuilder() : Cyclomatic Complexity = 1
CommandLine.getOptionObject(String optionName) : Cyclomatic Complexity = 1
TextHelpAppendable.getIndent() : Cyclomatic Complexity = 1
HelpFormatter.setOptPrefix(String prefix) : Cyclomatic Complexity = 1
OptionFormatter.toArgName(String argName) : Cyclomatic Complexity = 1
DefaultParser.checkRequiredArgs() : Cyclomatic Complexity = 3
OptionBuilder.withType(Class<?> newType) : Cyclomatic Complexity = 1
CommandLine.addOption(Option option) : Cyclomatic Complexity = 2
PosixParser.processOptionToken(String token, boolean stopAtNonOption) : Cyclomatic Complexity = 3
TextHelpAppendable.printWrapped(String text, TextStyle style) : Cyclomatic Complexity = 1
PosixParser.flatten(Options options, String[] arguments, boolean stopAtNonOption) : Cyclomatic Complexity = 13
CommandLine.getParsedOptionValues(String optionName, Supplier<T[]> defaultValue) : Cyclomatic Complexity = 1
DefaultParser.isLongOption(String token) : Cyclomatic Complexity = 5
CommandLine.getParsedOptionValue(char optionChar) : Cyclomatic Complexity = 1
Options.helpOptions() : Cyclomatic Complexity = 1
HelpFormatter.printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer, boolean autoUsage) : Cyclomatic Complexity = 5
Parser.getOptions() : Cyclomatic Complexity = 1
Option.type(Class<?> type) : Cyclomatic Complexity = 1
HelpFormatter.getOptionComparator() : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValues(char optionChar, Supplier<T[]> defaultValue) : Cyclomatic Complexity = 1
CommandLine.getOptionValue(char optionChar) : Cyclomatic Complexity = 1
Option.since(String since) : Cyclomatic Complexity = 1
OptionFormatter.setOptPrefix(String optPrefix) : Cyclomatic Complexity = 1
TableDefinition.caption() : Cyclomatic Complexity = 1
AbstractHelpFormatter.printOptions(Iterable<Option> options) : Cyclomatic Complexity = 1
Options.getRequiredOptions() : Cyclomatic Complexity = 1
TableDefinition.rows() : Cyclomatic Complexity = 1
AbstractHelpFormatter.getTableDefinition(Iterable<Option> options) : Cyclomatic Complexity = 1
HelpFormatter.appendOptionGroup(StringBuilder buff, OptionGroup group) : Cyclomatic Complexity = 6
TextHelpAppendable.adjustTableFormat(TableDefinition table) : Cyclomatic Complexity = 9
CommandLine.getParsedOptionValues(Option option, Supplier<T[]> defaultValue) : Cyclomatic Complexity = 4
TextStyle.setIndent(int indent) : Cyclomatic Complexity = 1
OptionBuilder.create(char opt) : Cyclomatic Complexity = 1
DeprecatedAttributes.setForRemoval(boolean forRemoval) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValue(OptionGroup optionGroup, T defaultValue) : Cyclomatic Complexity = 1
TextHelpAppendable.printQueue(Queue<String> queue) : Cyclomatic Complexity = 1
Option.getValuesList() : Cyclomatic Complexity = 1
Option.setConverter(Converter<?, ?> converter) : Cyclomatic Complexity = 1
HelpAppendable.appendTable(TableDefinition table) : Cyclomatic Complexity = 1
HelpFormatter.setWidth(int width) : Cyclomatic Complexity = 1
HelpFormatter.renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad) : Cyclomatic Complexity = 1
AbstractHelpFormatter.getSyntaxPrefix() : Cyclomatic Complexity = 1
ParseException.wrap(Throwable e) : Cyclomatic Complexity = 3
Options.getMatchingOptions(String opt) : Cyclomatic Complexity = 3
Option.setLongOpt(String longOpt) : Cyclomatic Complexity = 1
Options.hasLongOption(String opt) : Cyclomatic Complexity = 1
CommandLine.getOptionValues(String optionName) : Cyclomatic Complexity = 1
AbstractHelpFormatter.asThis() : Cyclomatic Complexity = 1
CommandLine.processPropertiesFromValues(Properties props, List<String> values) : Cyclomatic Complexity = 3
HelpFormatter.printHelp(int width, String cmdLineSyntax, String header, Options options, String footer) : Cyclomatic Complexity = 1
DefaultParser.parse(Options options, String[] arguments, boolean stopAtNonOption) : Cyclomatic Complexity = 1
DefaultParser.getMatchingLongOptions(String token) : Cyclomatic Complexity = 3
DefaultParser.stripLeadingAndTrailingQuotesDefaultOn(String token) : Cyclomatic Complexity = 2
OptionFormatter.getLongOpt() : Cyclomatic Complexity = 2
Option.toType(Class<?> type) : Cyclomatic Complexity = 2
CommandLine.getOptionValue(String optionName) : Cyclomatic Complexity = 1
Option.argName(String argName) : Cyclomatic Complexity = 1
OptionFormatter.from(Option option) : Cyclomatic Complexity = 1
AbstractHelpFormatter.toSyntaxOptions(Options options) : Cyclomatic Complexity = 1
DeprecatedAttributes.setDescription(String description) : Cyclomatic Complexity = 1
Options.getOption(String opt) : Cyclomatic Complexity = 2
DefaultParser.isOption(String token) : Cyclomatic Complexity = 1
OptionFormatter.setDeprecatedFormatFunction(Function<Option, String> deprecatedFormatFunction) : Cyclomatic Complexity = 1
OptionBuilder.hasOptionalArgs() : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValue(char optionChar, T defaultValue) : Cyclomatic Complexity = 1
CommandLine.getOptionValues(OptionGroup optionGroup) : Cyclomatic Complexity = 2
OptionFormatter.getDescription() : Cyclomatic Complexity = 2
CommandLine.getParsedOptionValues(Option option) : Cyclomatic Complexity = 1
Option.getArgName() : Cyclomatic Complexity = 1
HelpFormatter.get() : Cyclomatic Complexity = 1
DefaultParser.handleOption(Option option) : Cyclomatic Complexity = 2
OptionFormatter.setDefaultArgName(String name) : Cyclomatic Complexity = 1
HelpAppendable.appendParagraph(CharSequence paragraph) : Cyclomatic Complexity = 1
Option.getValue(int index) : Cyclomatic Complexity = 2
CommandLine.getOptionValue(Option option, Supplier<String> defaultValue) : Cyclomatic Complexity = 2
HelpFormatter.builder() : Cyclomatic Complexity = 1
Option.hasValueSeparator() : Cyclomatic Complexity = 1
Options.addOptionGroup(OptionGroup group) : Cyclomatic Complexity = 2
OptionBuilder.isRequired(boolean newRequired) : Cyclomatic Complexity = 1
CommandLine.getParsedOptionValue(OptionGroup optionGroup, Supplier<T> defaultValue) : Cyclomatic Complexity = 2
CommandLine.getParsedOptionValues(OptionGroup optionGroup, T[] defaultValue) : Cyclomatic Complexity = 1
PatternOptionBuilder.isValueCode(char ch) : Cyclomatic Complexity = 1
Option.getConverter() : Cyclomatic Complexity = 2
Option.getDeprecated() : Cyclomatic Complexity = 1
DefaultParser.handleLongOptionWithoutEqual(String token) : Cyclomatic Complexity = 4
DefaultParser.setAllowPartialMatching(boolean allowPartialMatching) : Cyclomatic Complexity = 1
Parser.processArgs(Option opt, ListIterator<String> iter) : Cyclomatic Complexity = 4
CommandLine.getOptionProperties(Option option) : Cyclomatic Complexity = 2
Option.equals(Object obj) : Cyclomatic Complexity = 3
Option.converter(Converter<?, ?> converter) : Cyclomatic Complexity = 1
Option.setType(Class<?> type) : Cyclomatic Complexity = 1

Cyclomatic Complexity Histogram for project: /home/daouda-traore/Téléchargements/commons-cli-master/src/main/java/org/apache/commons/cli
CC 1: 298 method(s)
CC 2: 58 method(s)
CC 3: 25 method(s)
CC 4: 12 method(s)
CC 5: 12 method(s)
CC 6: 4 method(s)
CC 7: 2 method(s)
CC 8: 5 method(s)
CC 9: 1 method(s)
CC 10: 1 method(s)
CC 11: 2 method(s)
CC 12: 1 method(s)
CC 13: 2 method(s)
CC 17: 1 method(s)

Histogram saved to: /home/daouda-traore/Téléchargements/commons-cli-master/src/main/java/org/apache/commons/cli_complexity_histogram.csv
