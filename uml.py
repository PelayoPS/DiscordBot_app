import os
import sys
from pathlib import Path
import javalang

def analyze_java_project(project_path):
    classes = []
    class_map = {}
    for root, _, files in os.walk(project_path):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, "r", encoding="utf-8") as f:
                    java_code = f.read()
                try:
                    tree = javalang.parse.parse(java_code)
                    for path, node in tree.filter(javalang.tree.ClassDeclaration):
                        class_name = node.name
                        fields = [var.name for field in node.fields for var in field.declarators]
                        methods = [method.name for method in node.methods]
                        extends = node.extends.name if node.extends else None
                        implements = [impl.name for impl in node.implements] if node.implements else []
                        classes.append({
                            "name": class_name,
                            "fields": fields,
                            "methods": methods,
                            "extends": extends,
                            "implements": implements
                        })
                        class_map[class_name] = node
                except javalang.parser.JavaSyntaxError as e:
                    print(f"Error parsing {file}: {e}")
    return classes, class_map

def generate_plantuml(classes, class_map):
    uml_code = "@startuml\n"
    for cls in classes:
        uml_code += f"class {cls['name']} {{\n"
        for field in cls['fields']:
            uml_code += f"  {field}\n"
        for method in cls['methods']:
            uml_code += f"  {method}()\n"
        uml_code += "}\n"
        if cls['extends']:
            uml_code += f"{cls['name']} --|> {cls['extends']}\n"
        for impl in cls['implements']:
            uml_code += f"{cls['name']} ..|> {impl}\n"
    uml_code += "@enduml\n"
    return uml_code

def save_and_render_uml(uml_code, output_path):
    """
    Guarda el código PlantUML y renderiza el diagrama.
    """
    plantuml_file = os.path.join(output_path, "diagram.puml")
    with open(plantuml_file, "w", encoding="utf-8") as f:
        f.write(uml_code)

    # Usa PlantUML para generar la imagen
    os.system(f"plantuml {plantuml_file}")

# Ruta al proyecto Java y carpeta de salida
project_path = sys.argv[1] if len(sys.argv) > 1 else "."
output_path = "./uml_output"

# Crear carpeta de salida si no existe
Path(output_path).mkdir(parents=True, exist_ok=True)

# Analizar el proyecto y generar el diagrama
classes, class_map = analyze_java_project(project_path)
uml_code = generate_plantuml(classes, class_map)
save_and_render_uml(uml_code, output_path)