# Compile

    javac -d compiler-out compiler/src/com/github/osiegmar/java9test/compiler/Compiler.java


# Run OK
    java -cp compiler-out com.github.osiegmar.java9test.compiler.Compiler true


# Run FAILS
    java -cp compiler-out com.github.osiegmar.java9test.compiler.Compiler false
