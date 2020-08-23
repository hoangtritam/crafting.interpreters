package learn.craftinginterpreters.lox.parser;

import learn.craftinginterpreters.lox.lexer.Token;

public class Binary implements Expr {
	final Expr left;
	final Token operator;
	final Expr right;

	public Binary(Expr left, Token operator, Expr right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	public Expr getLeft() {
		return left;
	}

	public Token getOperator() {
		return operator;
	}

	public Expr getRight() {
		return right;
	}
}
