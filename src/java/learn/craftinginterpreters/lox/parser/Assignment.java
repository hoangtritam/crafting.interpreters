package learn.craftinginterpreters.lox.parser;

import learn.craftinginterpreters.lox.lexer.Token;

public class Assignment implements Expr {
	final Token identifier;
	final Expr value;

	public Assignment(Token identifier, Expr value) {
		this.identifier = identifier;
		this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	public Token getIdentifier() {
		return this.identifier;
	}
	
	public Expr getValue() {
		return this.value;
	}
}
