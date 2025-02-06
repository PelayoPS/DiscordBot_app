package bot;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;

public class UMLGenerator {
    private static final List<String> classes = new ArrayList<>();
    private static final List<String> relationships = new ArrayList<>();
    private static final Set<String> javaNativeClasses = new HashSet<>(Arrays.asList(
        "String", "Integer", "Boolean", "Long", "Double", "Float", "Character", "Byte", "Short"
    ));

    public static void main(String[] args) throws Exception {
        File srcFolder = new File("src/main/java/bot"); // Directorio donde están los archivos Java
        processDirectory(srcFolder);
        generatePlantUML();
        generateImageFromPlantUML();
    }

    private static void processDirectory(File folder) throws Exception {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                processDirectory(file);
            } else if (file.getName().endsWith(".java")) {
                processFile(file);
            }
        }
    }

    private static void processFile(File file) throws Exception {
        CompilationUnit cu = new JavaParser().parse(file).getResult().orElse(null);
        if (cu == null)
            return;

        for (ClassOrInterfaceDeclaration clazz : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String className = clazz.getNameAsString();
            String classType = clazz.isInterface() ? "interface" : "class";
            classes.add(classType + " " + className + " {");

            // Extraer atributos
            for (FieldDeclaration field : clazz.getFields()) {
                String fieldType = field.getVariables().get(0).getTypeAsString();
                if (!javaNativeClasses.contains(fieldType) && isClassInDirectory(fieldType)) {
                    if (fieldType.startsWith("List<")) {
                        relationships.add(className + " o-- " + fieldType.substring(5, fieldType.length() - 1));
                    } else {
                        relationships.add(className + " --> " + fieldType);
                    }
                }
                classes.add("    " + field.getVariables().get(0).getName() + " : " + fieldType);
            }

            // Extraer métodos
            for (MethodDeclaration method : clazz.getMethods()) {
                Type returnType = method.getType();
                classes.add("    " + method.getName() + "() : " + returnType);
            }

            classes.add("}");

            // Extraer relaciones de herencia e implementación
            clazz.getExtendedTypes().forEach(ext -> relationships.add(ext.getNameAsString() + " <|-- " + className));
            clazz.getImplementedTypes()
                    .forEach(impl -> relationships.add(impl.getNameAsString() + " <|.. " + className));

            // Extraer relaciones de uso
            for (VariableDeclarator variable : cu.findAll(VariableDeclarator.class)) {
                String variableType = variable.getTypeAsString();
                if (!javaNativeClasses.contains(variableType) && !variableType.equals(className) && isClassInDirectory(variableType)) {
                    if (variableType.startsWith("List<")) {
                        relationships.add(className + " o-- " + variableType.substring(5, variableType.length() - 1));
                    } else {
                        relationships.add(className + " --> " + variableType);
                    }
                }
            }
        }
    }

    private static boolean isClassInDirectory(String className) {
        File srcFolder = new File("src/main/java/bot");
        for (File file : srcFolder.listFiles()) {
            if (file.getName().equals(className + ".java")) {
                return true;
            }
        }
        return false;
    }

    private static void generatePlantUML() throws Exception {
        FileWriter writer = new FileWriter("uml_diagram.puml");
        writer.write("@startuml Diagrama\n");
        for (String clazz : classes)
            writer.write(clazz + "\n");
        for (String rel : relationships)
            writer.write(rel + "\n");
        writer.write("@enduml\n");
        writer.close();
        System.out.println("Diagrama generado en uml_diagram.puml");
    }

    private static void generateImageFromPlantUML() throws Exception {
        try {
            // Especificar la ruta completa del ejecutable de plantuml si es necesario
            String plantUmlCommand = "plantuml";
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                plantUmlCommand = "plantuml.bat"; // Cambiar a la ruta correcta en Windows
            }

            ProcessBuilder processBuilder = new ProcessBuilder(plantUmlCommand, "uml_diagram.puml");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Imagen generada a partir de uml_diagram.puml");
        } catch (IOException e) {
            System.err.println("Error al ejecutar el comando plantuml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
