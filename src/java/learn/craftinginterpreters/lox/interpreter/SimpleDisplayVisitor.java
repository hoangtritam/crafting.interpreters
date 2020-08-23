package learn.craftinginterpreters.lox.interpreter;

import learn.craftinginterpreters.lox.parser.Assignment;
import learn.craftinginterpreters.lox.parser.Binary;
import learn.craftinginterpreters.lox.parser.Expr;
import learn.craftinginterpreters.lox.parser.Grouping;
import learn.craftinginterpreters.lox.parser.Literal;
import learn.craftinginterpreters.lox.parser.Ternary;
import learn.craftinginterpreters.lox.parser.Unary;
import learn.craftinginterpreters.lox.parser.Variable;

public class SimpleDisplayVisitor implements Expr.Visitor<String> {

	public SimpleDisplayVisitor() {
	}

	@Override
	public String visit(Binary expr) {
		StringBuilder sb = new StringBuilder();
		sb.append(expr.getLeft().accept(this));
		sb.append(" " + expr.getOperator().getLexem() + " ");
		sb.append(expr.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(Grouping expr) {
		StringBuilder sb = new StringBuilder();
		sb.append(" (");
		sb.append(expr.getExpr().accept(this));
		sb.append(") ");
		return sb.toString();
	}

	@Override
	public String visit(Literal expr) {
		return expr.getValue().toString();
	}

	@Override
	public String visit(Unary expr) {
		return " (" + expr.getOperator().getLexem() + expr.getRight().accept(this) + ") ";
	}

	@Override
	public String visit(Ternary expr) {
		StringBuilder sb = new StringBuilder();
		sb.append(expr.getCond().accept(this));
		sb.append(" ? ");
		sb.append(expr.getLeft().accept(this));
		sb.append(" : ");
		sb.append(expr.getRight().accept(this));
		return sb.toString();
	}

	@Override
	public String visit(Variable expr) {
		return expr.getIdentifier().getLexem();
	}

	@Override
	public String visit(Assignment expr) {
		StringBuilder sb = new StringBuilder();
		sb.append(expr.getIdentifier().getLexem());
		sb.append(" = ").append(expr.getValue()).append(";");
		return sb.toString();
	}

}
