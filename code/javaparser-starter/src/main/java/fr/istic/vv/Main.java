package fr.istic.vv;

import fr.istic.vv.Exercise4.PrivateFieldAnalyzer;
import fr.istic.vv.Exercise5.CyclomaticComplexityCalculator;
import fr.istic.vv.Exercise6.ClassCohesionCalculator;
import com.github.javaparser.utils.SourceRoot;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        String sourcePath = args[0];
        SourceRoot root = new SourceRoot(file.toPath());

        PublicElementsPrinter printer = new PublicElementsPrinter();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(printer, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        System.out.println("\nStarting analysis for private fields without getters:");

        PrivateFieldAnalyzer.main(new String[] { sourcePath });

        System.out.println("\nStarting class cohesion analysis...");

        ClassCohesionCalculator.main(new String[] { sourcePath });

        CyclomaticComplexityCalculator.main(new String[] { sourcePath });

        System.out.println("\nAnalysis complete.");
    }
}
