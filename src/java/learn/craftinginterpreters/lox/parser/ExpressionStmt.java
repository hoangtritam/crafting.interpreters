package learn.craftinginterpreters.lox.parser;

public class ExpressionStmt implements Stmt {
	final Expr expr;

	public ExpressionStmt(Expr expr) {
		this.expr = expr;
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}
	
	public Expr getExpression() {
		return expr;
	}
}
