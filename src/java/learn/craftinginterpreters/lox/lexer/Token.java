package learn.craftinginterpreters.lox.lexer;

public class Token {

	private final Type type;
	private final String lexem;
	private final Object literal;
	private final int line;

	public Token(Type type, String lexem, Object literal, int line) {
		this.type = type;
		this.lexem = lexem;
		this.literal = literal;
		this.line = line;
	}

	public boolean match(Type... types) {
		for (Type type : types) {
			if (this.type == type) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Token: ").append(type).append(" at line ").append(line).append("\n");
		sb.append("Lexem: ").append(lexem).append("\n");
		sb.append("Literal: ").append(literal).append("\n");
		return sb.toString();
	}

	public Type getType() {
		return this.type;
	}

	public String getLexem() {
		return this.lexem;
	}

	public Object getLiteral() {
		return this.literal;
	}

	public int getLine() {
		return this.line;
	}
}
