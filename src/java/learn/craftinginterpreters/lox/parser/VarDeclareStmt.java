package learn.craftinginterpreters.lox.parser;

import learn.craftinginterpreters.lox.lexer.Token;

public class VarDeclareStmt implements Stmt {

	final Token var;
	final Expr initializer;
	
	public VarDeclareStmt(Token var, Expr initializer) {
		this.var = var;
		this.initializer = initializer;
	}
	
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}
	
	public Token getIdentifier() {
		return var;
	}
	
	public Expr getInitializer() {
		return initializer;
	}
}
