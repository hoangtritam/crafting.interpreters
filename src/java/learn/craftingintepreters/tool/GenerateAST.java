package learn.craftingintepreters.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateAST {

	/*
	 * src/java/learn/craftinginterpreters/lox/
	 * "Binary   : Expr left, Token operator, Expr right"
	 * "Grouping : Expr expression" "Literal  : Object value"
	 * "Unary    : Token operator, Expr right"
	 */

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Not enought parameter. Please provide output location.");
			System.exit(64);
		}
		File outputDir = new File(args[0]);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		for (int i = 1; i < args.length; i++) {
			generate(outputDir, args[i]);
		}
		System.out.println("Program terminates..");
	}

	private static void generate(File outputDir, String source) {
		String[] syntax = source.split(":");
		String className = syntax[0].trim();
		syntax[1] = syntax[1].trim();

		File file = new File(outputDir, className + ".java");
		try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
			// package declaration
			pw.println("package learn.craftinginterpreters.lox;");
			pw.println();

			// class declaration
			pw.println("public class " + className + " extends Expr {");

			// field declarations
			String[] fields = syntax[1].split(",");
			for (int i = 0; i < fields.length; i++) {
				fields[i] = fields[i].trim();
				pw.println("\tfinal " + fields[i] + ";");
			}
			pw.println();

			// constructor
			pw.println("\tpublic " + className + "(" + syntax[1] + ") {");
			for (String field : fields) {
				int spaceIndex = field.indexOf(' ');
				String varName = field.substring(spaceIndex).trim();
				pw.println("\t\tthis." + varName + " = " + varName + ";");
			}
			pw.println("\t}");
			pw.println();

			// methods
			pw.println("\tpublic void accept(Visitor visitor) {");
			pw.println("\t\tvisitor.visit(this);");
			pw.println("\t}");

			pw.println("}");
		} catch (IOException e) {
			System.err.println("Unable to write syntax: " + source);
			e.printStackTrace();
			System.exit(65);
		}
	}
}
