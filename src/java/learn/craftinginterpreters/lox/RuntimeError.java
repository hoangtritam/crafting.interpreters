package learn.craftinginterpreters.lox;

import learn.craftinginterpreters.lox.lexer.Token;

@SuppressWarnings("serial")
public class RuntimeError extends RuntimeException {
	private Token operator;
	private String msg;

	public RuntimeError(Token operator, String msg) {
		this.operator = operator;
		this.msg = msg;
	}

	public Token getOperator() {
		return this.operator;
	}

	public String getMessage() {
		return this.msg;
	}
}
