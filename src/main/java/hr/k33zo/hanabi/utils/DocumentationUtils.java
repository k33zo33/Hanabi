package hr.k33zo.hanabi.utils;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocumentationUtils {



    public static void generateDocumentation()  {

        try{
            List<String> classFilePaths = Files.walk(Paths.get("target"))
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".class"))
                    .filter(f -> !f.endsWith("module-info.class"))
                    .toList();

            String htmlHeader = """
                <!DOCTYPE html>
                <html>
                <head>
                <title>Page Title</title>
                </head>
                <body>
                """;

            for(String classFilePath : classFilePaths) {
                String[] pathTokens = classFilePath.split("classes");
                String secondToken = pathTokens[1];
                String fqnWithSlashes = secondToken.substring(1, secondToken.lastIndexOf('.'));
                String fqn = fqnWithSlashes.replace('/', '.').replace('\\', '.');
                //String fqn = fqnWithSlashes.replace('/', '.');
                System.out.println("Attempting to load class: " + fqn);
                Class<?> deserializedClass = Class.forName(fqn);

                htmlHeader += "<h2>" + deserializedClass.getName() + "</h2>";

                Field[] fields = deserializedClass.getDeclaredFields();
//                htmlHeader += "<h2>Fields</h2>";

                for(Field field : fields) {

                    int modifiers = field.getModifiers();

                    if(Modifier.isPublic(modifiers)) {
                        htmlHeader += "public ";
                    }
                    else if (Modifier.isPrivate(modifiers)) {
                        htmlHeader += "private ";
                    }
                    else if (Modifier.isProtected(modifiers)) {
                        htmlHeader += "protected ";
                    }
                    if(Modifier.isStatic(modifiers)) {
                        htmlHeader += "static ";
                    }
                    if(Modifier.isFinal(modifiers)) {
                        htmlHeader += "final ";
                    }

                    String type = field.getType().getTypeName();

                    htmlHeader += type + " ";

                    String name = field.getName();

                    htmlHeader += name + "<br>";

                }

                Constructor<?>[] constructors = deserializedClass.getDeclaredConstructors();
                if (constructors.length > 0) {
                    htmlHeader += "<h3>Constructors</h3>";
                }
                for (Constructor<?> constructor : constructors) {
                    int modifiers = constructor.getModifiers();
                    htmlHeader += "<p>";
                    htmlHeader += Modifier.toString(modifiers) + " ";
                    htmlHeader += deserializedClass.getSimpleName();
                    htmlHeader += "(";
                    Parameter[] parameters = constructor.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        htmlHeader += parameter.getType().getSimpleName() + " " + parameter.getName();
                        if (i < parameters.length - 1) {
                            htmlHeader += ", ";
                        }
                    }
                    htmlHeader += ");";
                    htmlHeader += "<p/>";
                }
                Method[] methods = deserializedClass.getDeclaredMethods();
                if (methods.length > 0) {
                    htmlHeader += "<h3>Methods</h3>";
                }
                for (Method method : methods)
                {
                    int modifiers = method.getModifiers();
                    htmlHeader += "<p>";
                    htmlHeader += Modifier.toString(modifiers) + " ";
                    htmlHeader += method.getReturnType().getSimpleName() + " ";
                    htmlHeader += method.getName();
                    htmlHeader += "(";
                    Parameter[] parameters = method.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        htmlHeader += parameter.getType().getSimpleName() + " " + parameter.getName();
                        if (i < parameters.length - 1) {
                            htmlHeader += ", ";
                        }
                    }
                    htmlHeader += ");<br>";
                    htmlHeader += "</p>";
                }

            }

            String htmlFooter = """
                </body>
                </html>
                """;

            Path htmlDocumentationFile = Path.of("files/documentation.html");

            String fullHtml = htmlHeader + htmlFooter;

            Files.write(htmlDocumentationFile, fullHtml.getBytes());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File created!");
            alert.setHeaderText("Creation of HTML documentation file succeeded!");
            alert.show();
        }
        catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("An error occurred while generating the documentation file!");
            alert.setContentText(e.getMessage());
            alert.show();
        }


    }

}
