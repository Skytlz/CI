package tools;

import lox.Token;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: GenerateAst <output directory>");
            System.exit(64);
        }
        String outputDirectory = args[0];

        defineAst(outputDirectory, "Expression", Arrays.asList(
                "Binary : Expression left, Token operator, Expression right",
                "Grouping : Expression expression",
                "Literal : Object value",
                "Unary : Token operator, Expression right"
        ));
    }

    private static void defineAst(
            String outputDirectory, String basename, List<String> tokens)
            throws IOException {
        String path = outputDirectory + "/" + basename + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package lox;\n");
        writer.println("import java.util.List;\n");
        writer.println("abstract class " + basename + " {");

        defineVisitor(writer, basename, tokens);

        for (String token : tokens) {
            String classname = token.split(":")[0].trim();
            String fields = token.split(":")[1].trim();
            defineType(writer, basename, classname, fields);
        }

        writer.println();
        writer.println("  abstract <R> R accept (Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(
            PrintWriter writer, String basename, List<String> fields) {
        writer.println("    interface Visitor<R> {");
        for (String token : fields) {
            String typename = token.split(":")[0].trim();
            writer.println("    R visit" + typename + basename + "(" + typename + " " + basename.toLowerCase() +
                    ");");
        }
        writer.println("    }");
    }

    private static void defineType(
            PrintWriter writer, String basename,
            String classname, String fields) {
        writer.println(" static class " + classname + " extends " + basename + " {");

        writer.println("    " + classname + "(" + fields + ") {");

        String[] fieldList = fields.split(", ");
        for (String field : fieldList) {
            String name = field.split(" ")[1];
            writer.println("    this." + name + " = " + name + ";");
        }
        writer.println("    }");

        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("        return visitor.visit" + classname + basename + "(this);");
        writer.println("    }");

        writer.println();
        for (String field : fieldList) {
            writer.println("    final " + field + ";");
        }
        writer.println("  }");
    }
}
