package learn.craftinginterpreters.lox.parser;

import learn.craftinginterpreters.lox.lexer.Token;

public class Variable implements Expr {

	final Token identifier;
	
	public Variable(Token identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	public Token getIdentifier() {
		return identifier;
	}
}
