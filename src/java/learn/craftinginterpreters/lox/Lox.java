package learn.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import learn.craftinginterpreters.lox.interpreter.Interpreter;
import learn.craftinginterpreters.lox.lexer.Lexer;
import learn.craftinginterpreters.lox.lexer.Token;
import learn.craftinginterpreters.lox.parser.Parser;
import learn.craftinginterpreters.lox.parser.Stmt;

public class Lox {

	private static boolean hadError = false;
	private static boolean hadRuntimeError = false;
	private static Interpreter interpreter;

	public static void main(String[] args) throws IOException {
		if (args.length > 2) {
			System.err.println("Too many parameters. Usage: lox [script]");
			System.exit(64);
		}

		interpreter = new Interpreter();
		if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}

	/**
	 * Run lox from a script file.
	 * 
	 * @param path - path to script file
	 * @throws IOException
	 */
	protected static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		if (hadError) {
			System.exit(65);
		} else if (hadRuntimeError) {
			System.exit(70);
		}
	}

	/**
	 * Run lox lively from command prompt.
	 * 
	 * @throws IOException
	 */
	protected static void runPrompt() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String line;
			while (true) {
				System.out.print("> ");
				line = reader.readLine();
				if (line == null) {
					break;
				}
				run(line);
				hadError = false;
				hadRuntimeError = false;
			}
		}
	}

	/**
	 * Parse and run the command.
	 * 
	 * @param source - line
	 */
	protected static void run(String source) {
		Lexer lexer = new Lexer(source);
		Parser parser = new Parser(lexer.scan());
		List<Stmt> statements = parser.parse();
		if (hadError) {
			// has parser error
			System.out.println();
			return;
		}
		try {
			for (Stmt statement : statements) {
				statement.accept(interpreter);
			}
		} catch (RuntimeError e) {
			hadRuntimeError = true;
			Token t = e.getOperator();
			if (t != null) {
				error(t.getLine(), e.getMessage());
			}
			else {
				System.err.println(e.getMessage());
			}
		}
		System.out.println();
	}

	public static void error(int line, String message) {
		error(line, "", message);
	}

	private static void error(int line, String where, String message) {
		System.err.println("[" + line + "] Error" + where + ": " + message);
		hadError = true;
	}
}
