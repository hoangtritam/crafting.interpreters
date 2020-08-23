package learn.craftinginterpreters.lox.parser;

public class Ternary implements Expr {
	final Expr cond;
	final Expr left;
	final Expr right;

	public Ternary(Expr cond, Expr left, Expr right) {
		this.cond = cond;
		this.left = left;
		this.right = right;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	public Expr getCond() {
		return cond;
	}

	public Expr getLeft() {
		return left;
	}

	public Expr getRight() {
		return right;
	}
}
