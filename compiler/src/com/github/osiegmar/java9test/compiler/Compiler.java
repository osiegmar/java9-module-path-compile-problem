package com.github.osiegmar.java9test.compiler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class Compiler {

    private static final String MODULE_NAME_API = "com.github.osiegmar.java9test.api";
    private static final String MODULE_NAME_APP = "com.github.osiegmar.java9test.app";
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: Compiler true|false");
            System.exit(1);
        }

        boolean genericPath = Boolean.parseBoolean(args[0]);

        final Path apiBuildDir = compileApi();

        // calling the following method with genericPath=false, the result is a compile error:
        // com.github.osiegmar.java9test.app/src/module-info.java:3: error: module not found: com.github.osiegmar.java9test.api
        // requires com.github.osiegmar.java9test.api;
        //                                       ^
        compileApp(apiBuildDir, genericPath);
    }

    private static Path compileApi() throws IOException {
        final StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(
            null, null, StandardCharsets.UTF_8);

        final Path apiBuildOutput = Files.createDirectories(Paths.get("build", MODULE_NAME_API));
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(apiBuildOutput));

        final Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(
            "com.github.osiegmar.java9test.api/src/module-info.java",
            "com.github.osiegmar.java9test.api/src/com/github/osiegmar/java9test/api/HelloService.java"
        );

        final JavaCompiler.CompilationTask task =
            COMPILER.getTask(null, fileManager, null, null, null, compilationUnits);

        System.out.println("Compile API result: " + task.call());

        return apiBuildOutput;
    }

    private static void compileApp(final Path apiBuildDir, final boolean genericPath) throws IOException {
        final StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(
            null, null, StandardCharsets.UTF_8);

        final Path appBuildOutput = Files.createDirectories(Paths.get("build", MODULE_NAME_APP));
        fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(appBuildOutput));

        if (genericPath) {
            // set a generic module path for all compiled modules
            fileManager.setLocationFromPaths(StandardLocation.MODULE_PATH, List.of(Paths.get("build")));
        } else {
            // directly set the module path for the compiled api module
            fileManager.setLocationForModule(StandardLocation.MODULE_PATH, MODULE_NAME_API, List.of(apiBuildDir));
        }

        final Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(
            "com.github.osiegmar.java9test.app/src/module-info.java",
            "com.github.osiegmar.java9test.app/src/com/github/osiegmar/java9test/app/Example.java"
        );

        final JavaCompiler.CompilationTask task =
            COMPILER.getTask(null, fileManager, null, null, null, compilationUnits);

        // does not seem to have an effect:
        //task.addModules(List.of(MODULE_NAME_API));

        System.out.println("Compile APP result: " + task.call());
    }

}
