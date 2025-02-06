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
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * Clase UMLGenerator para generar diagramas UML a partir del código fuente
 * Java.
 */
public class UMLGenerator {
    private static final List<String> classes = new ArrayList<>();
    private static final List<String> relationships = new ArrayList<>();
    private static final Set<String> javaNativeClasses = new HashSet<>(Arrays.asList(
            "String", "Integer", "Boolean", "Long", "Double", "Float", "Character", "Byte", "Short"));

    /**
     * Método principal que inicia el proceso de generación del diagrama UML.
     * 
     * @param args Argumentos de la línea de comandos.
     * @throws Exception Si ocurre un error durante el procesamiento.
     */
    public static void main(String[] args) throws Exception {
        File srcFolder = new File("src/main/java/bot"); // Directorio donde están los archivos Java
        processDirectory(srcFolder);
        generatePlantUML();
        generateImageFromPlantUML();
    }

    /**
     * Procesa recursivamente un directorio y sus archivos Java.
     * 
     * @param folder El directorio a procesar.
     * @throws Exception Si ocurre un error durante el procesamiento.
     */
    private static void processDirectory(File folder) throws Exception {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                processDirectory(file);
            } else if (file.getName().endsWith(".java")) {
                processFile(file);
            }
        }
    }

    /**
     * Procesa un archivo Java para extraer información de clases, atributos y
     * métodos.
     * 
     * @param file El archivo Java a procesar.
     * @throws Exception Si ocurre un error durante el procesamiento.
     */
    private static void processFile(File file) throws Exception {
        // Parsear el archivo Java con JavaParser
        CompilationUnit cu = new JavaParser().parse(file).getResult().orElse(null);
        if (cu == null) {
            return;
        }
        // Extraer clases, atributos y métodos
        processClasses(cu);

    }

    /**
     * Procesa las clases encontradas en una unidad de compilación.
     * 
     * @param cu La unidad de compilación a procesar.
     */
    private static void processClasses(CompilationUnit cu) {
        for (ClassOrInterfaceDeclaration clazz : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String className = clazz.getNameAsString();
            String classType = clazz.isInterface() ? "interface" : "class";
            classes.add(classType + " " + className + " {");

            // Procesar atributos
            processAtributes(clazz.getFields());

            // Procesar métodos
            processMethods(clazz);

            // Procesar relaciones
            processRelationships(cu);

            // Fin de la clase
            classes.add("}");


        }
    }

    /**
     * Procesa los atributos de una clase.
     * Obtiene el tipo y nombre de cada atributo y los agrega a la lista de clases
     * Mostrando el tipo, nombre y visibilidad del atributo.
     * 
     * @param fields Lista de declaraciones de campos de la clase.
     */
    private static void processAtributes(List<FieldDeclaration> fields) {
        for (FieldDeclaration field : fields) {
            String type = field.getVariable(0).getTypeAsString();
            String name = field.getVariable(0).getNameAsString();
            String visibility = field.isPrivate() ? "-" : field.isProtected() ? "#" : "+";
            classes.add(visibility + " " + name + " : " + type);
        }
    }

    /**
     * Procesa los métodos de una clase.
     * Obtiene el nombre, visibilidad, tipo de retorno y parámetros de cada método
     * y los agrega a la lista de clases.
     * 
     * @param clazz La declaración de la clase o interfaz.
     */
    private static void processMethods(ClassOrInterfaceDeclaration clazz) {
        for (MethodDeclaration method : clazz.getMethods()) {
            String methodName = method.getNameAsString();
            String visibility = method.isPrivate() ? "-" : method.isProtected() ? "#" : "+";
            String returnType = method.getTypeAsString();
            StringBuilder parameters = new StringBuilder();
            for (Parameter parameter : method.getParameters()) {
                if (parameters.length() > 0) {
                    parameters.append(", ");
                }
                parameters.append(parameter.getTypeAsString()).append(" ").append(parameter.getNameAsString());
            }
            classes.add(visibility + " " + methodName + "(" + parameters.toString() + ") : " + returnType);
        }
    }

    private static void processRelationships(CompilationUnit cu) {
        List<String> herencias = new ArrayList<>();
        List<String> implementaciones = new ArrayList<>();
        List<String> asociaciones = new ArrayList<>();
        List<String> agregaciones = new ArrayList<>();
        List<String> composiciones = new ArrayList<>();
        List<String> dependencias = new ArrayList<>();

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(coid -> {
            String className = coid.getNameAsString();

            // Herencia: Child --|> Parent
            coid.getExtendedTypes().forEach(extendedType ->
                herencias.add(className + " --|> " + extendedType.getNameAsString())
            );

            // Implementación: Class ..|> Interface
            coid.getImplementedTypes().forEach(implType ->
                implementaciones.add(className + " ..|> " + implType.getNameAsString())
            );

            // Asociación: Class --> FieldType
            coid.getFields().forEach(field ->
                field.getVariables().forEach(var -> {
                    if (!field.getElementType().isPrimitiveType()) {
                        asociaciones.add(className + " --> " + field.getElementType().toString());
                    }
                })
            );

            // Agregación y Composición:
            // Heurística: si el campo se instancia en el constructor se considera composición, de lo contrario agregación.
            coid.getFields().forEach(field ->
                field.getVariables().forEach(var -> {
                    boolean seInstanciaEnConstructor = coid.getConstructors().stream()
                        .anyMatch(cons -> cons.toString().contains("new " + field.getElementType().toString()));
                    if (seInstanciaEnConstructor) {
                        composiciones.add(className + " *-- " + field.getElementType().toString());
                    } else {
                        agregaciones.add(className + " o-- " + field.getElementType().toString());
                    }
                })
            );

            // Dependencia: Class ..> Dependency (se detecta en parámetros de métodos)
            coid.getMethods().forEach(method ->
                method.getParameters().forEach(param ->
                    dependencias.add(className + " ..> " + param.getType().toString())
                )
            );
        });

        // Imprimir el diagrama en formato PlantUML
        System.out.println("@startuml");
        herencias.forEach(System.out::println);
        implementaciones.forEach(System.out::println);
        asociaciones.forEach(System.out::println);
        agregaciones.forEach(System.out::println);
        composiciones.forEach(System.out::println);
        dependencias.forEach(System.out::println);
        System.out.println("@enduml");
    }

    /**
     * Verifica si una clase está en el directorio de origen.
     * 
     * @param className El nombre de la clase a verificar.
     * @return true si la clase está en el directorio, false en caso contrario.
     */
    private static boolean isClassInDirectory(String className) {
        File srcFolder = new File("src/main/java/bot");
        for (File file : srcFolder.listFiles()) {
            if (file.getName().equals(className + ".java")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Genera el archivo PlantUML a partir de la información extraída.
     * 
     * @throws Exception Si ocurre un error durante la generación del archivo.
     */
    private static void generatePlantUML() throws Exception {
        File outputDir = new File("uml_output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        FileWriter writer = new FileWriter(new File(outputDir, "diagrama.puml"));
        writer.write("@startuml diagrama\n");
        for (String clazz : classes)
            writer.write(clazz + "\n");
        for (String rel : relationships)
            writer.write(rel + "\n");
        writer.write("@enduml\n");
        writer.close();
        System.out.println("Diagrama generado en uml_output/diagrama.puml");
    }

    /**
     * Genera una imagen SVG a partir del archivo PlantUML.
     * 
     * @throws Exception Si ocurre un error durante la generación de la imagen.
     */
    private static void generateImageFromPlantUML() throws Exception {
        try {
            // Especificar la ruta completa del ejecutable de plantuml si es necesario
            String plantUmlCommand = "plantuml";
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                plantUmlCommand = "plantuml.bat"; // Cambiar a la ruta correcta en Windows
            }

            ProcessBuilder processBuilder = new ProcessBuilder(plantUmlCommand, "-tsvg", "uml_output/diagrama.puml");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("Imagen SVG generada en uml_output/diagrama.svg");
        } catch (IOException e) {
            System.err.println("Error al ejecutar el comando plantuml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}