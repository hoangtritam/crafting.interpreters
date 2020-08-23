package learn.craftinginterpreters.lox.parser;

public interface Expr {
	
	public <R> R accept(Visitor<R> visitor);
	
	public static interface Visitor<R> {
		public R visit(Binary expr);

		public R visit(Ternary expr);

		public R visit(Grouping expr);

		public R visit(Literal expr);

		public R visit(Unary expr);
		
		public R visit(Variable expr);
		
		public R visit(Assignment expr);
	}
}
