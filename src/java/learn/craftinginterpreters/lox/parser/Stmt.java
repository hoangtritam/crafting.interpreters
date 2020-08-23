package learn.craftinginterpreters.lox.parser;

public interface Stmt {
	public abstract <R> R accept(Visitor<R> visitor);

	public static interface Visitor<R> {
		public R visit(ExpressionStmt stmt);

		public R visit(PrintStmt stmt);
		
		public R visit(VarDeclareStmt stmt);
	}
}
