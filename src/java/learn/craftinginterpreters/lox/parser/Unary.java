package learn.craftinginterpreters.lox.parser;

import learn.craftinginterpreters.lox.lexer.Token;

public class Unary implements Expr {
	final Token operator;
	final Expr right;

	public Unary(Token operator, Expr right) {
		this.operator = operator;
		this.right = right;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	public Token getOperator() {
		return this.operator;
	}

	public Expr getRight() {
		return this.right;
	}
}
