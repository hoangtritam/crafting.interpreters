package learn.craftinginterpreters.lox.parser;

public class Grouping implements Expr {
	final Expr expression;

	public Grouping(Expr expression) {
		this.expression = expression;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	public Expr getExpr() {
		return this.expression;
	}
}
