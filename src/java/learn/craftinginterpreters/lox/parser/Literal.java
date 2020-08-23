package learn.craftinginterpreters.lox.parser;

public class Literal implements Expr {
	final Object value;

	public Literal(Object value) {
		this.value = value;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	public Object getValue() {
		return this.value;
	}
}
