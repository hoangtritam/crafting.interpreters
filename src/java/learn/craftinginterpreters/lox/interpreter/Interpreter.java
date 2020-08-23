package learn.craftinginterpreters.lox.interpreter;

import learn.craftinginterpreters.lox.RuntimeError;
import learn.craftinginterpreters.lox.lexer.Token;
import learn.craftinginterpreters.lox.lexer.Type;
import learn.craftinginterpreters.lox.parser.Assignment;
import learn.craftinginterpreters.lox.parser.Binary;
import learn.craftinginterpreters.lox.parser.BlockStmt;
import learn.craftinginterpreters.lox.parser.Expr;
import learn.craftinginterpreters.lox.parser.ExpressionStmt;
import learn.craftinginterpreters.lox.parser.Grouping;
import learn.craftinginterpreters.lox.parser.Literal;
import learn.craftinginterpreters.lox.parser.PrintStmt;
import learn.craftinginterpreters.lox.parser.Stmt;
import learn.craftinginterpreters.lox.parser.Ternary;
import learn.craftinginterpreters.lox.parser.Unary;
import learn.craftinginterpreters.lox.parser.VarDeclareStmt;
import learn.craftinginterpreters.lox.parser.Variable;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

	Environment env = new Environment();

	public Interpreter() {
	}

	@Override
	public Object visit(Binary expr) {
		Type t = expr.getOperator().getType();
		Object left = expr.getLeft().accept(this);
		Object right = expr.getRight().accept(this);

		switch (t) {
		case PLUS: {
			if (left instanceof Double && right instanceof Double) {
				return (double) left + (double) right;
			}
			if (left instanceof String || right instanceof String) {
				return left.toString() + right.toString();
			}
			throw new RuntimeError(expr.getOperator(), "Operands must be either Double or String");
		}
		case MINUS:
			checkNumbers(expr.getOperator(), left, right);
			return (double) left - (double) right;
		case STAR:
			checkNumbers(expr.getOperator(), left, right);
			return (double) left * (double) right;
		case SLASH:
			checkNumbers(expr.getOperator(), left, right);
			return (double) left / (double) right;
		case EQUAL_EQUAL:
			return isEqual(left, right);
		case BANG_EQUAL:
			return !isEqual(left, right);
		case COMMA:
			return right;
		default:
		}

		if (t == Type.LESS_EQUAL || t == Type.LESS || t == Type.GREATER || t == Type.GREATER_EQUAL) {
			int compare = 0;
			if (left instanceof Double && right instanceof Double) {
				compare = ((Double) left).compareTo((Double) right);
			} else if (left instanceof String && right instanceof String) {
				compare = ((String) left).compareTo((String) right);
			} else {
				throw new RuntimeError(expr.getOperator(),
						"Uncomparable objects " + left.toString() + " vs " + right.toString());
			}

			switch (t) {
			case LESS_EQUAL:
				return compare <= 0;
			case LESS:
				return compare < 0;
			case GREATER:
				return compare > 0;
			case GREATER_EQUAL:
				return compare >= 0;
			default:
			}
		}

		throw new RuntimeError(expr.getOperator(), "Unreachable code in Interpreter's Binary.");
	}

	/*
	 * EXPRESSION VISITOR METHODS
	 */

	@Override
	public Object visit(Ternary expr) {
		boolean cond = truthy(expr.getCond().accept(this));
		return cond ? expr.getLeft().accept(this) : expr.getRight().accept(this);
	}

	@Override
	public Object visit(Grouping expr) {
		return expr.getExpr().accept(this);
	}

	@Override
	public Object visit(Literal expr) {
		return expr.getValue();
	}

	@Override
	public Object visit(Unary expr) {
		switch (expr.getOperator().getType()) {
		case BANG:
			return !truthy(expr.getRight().accept(this));
		case MINUS: {
			Object result = expr.getRight().accept(this);
			checkNumber(expr.getOperator(), result);
			return -(double) result;
		}
		default:
			return null;
		}
	}

	@Override
	public Object visit(Variable expr) {
		Token identifier = expr.getIdentifier();
		String name = identifier.getLexem();
		if (env.hasVariable(name)) {
			return env.getValue(name);
		}
		throw error(identifier, "Undefined variable " + name);
	}

	@Override
	public Object visit(Assignment expr) {
		Object value = evaluate(expr.getValue());
		env.assign(expr.getIdentifier(), value);
		return value;
	}

	/*
	 * STATEMENT VISITOR METHODS
	 */

	@Override
	public Void visit(ExpressionStmt expr) {
		evaluate(expr.getExpression());
		return null;
	}

	@Override
	public Void visit(PrintStmt expr) {
		Object value = evaluate(expr.getExpression());
		System.out.println(value.toString());
		return null;
	}

	@Override
	public Void visit(VarDeclareStmt stmt) {
		Token identifier = stmt.getIdentifier();
		String name = identifier.getLexem();
		if (env.hasVariable(name)) {
			throw error(identifier, "Variable has been declared: " + name);
		}

		Object value = null;
		Expr init = stmt.getInitializer();
		if (init != null) {
			value = stmt.getInitializer().accept(this);
		}
		env.init(name, value);
		return null;
	}
	
	@Override
	public Void visit(BlockStmt block) {
		env = new Environment(env);
		for (Stmt stmt : block.getStatements())
		{
			stmt.accept(this);
		}
		env = env.getParent();
		return null;
	}

	protected Object evaluate(Expr expr) {
		if (expr instanceof Binary) {
			return visit((Binary) expr);
		} else if (expr instanceof Literal) {
			return visit((Literal) expr);
		} else if (expr instanceof Ternary) {
			return visit((Ternary) expr);
		} else if (expr instanceof Grouping) {
			return visit((Grouping) expr);
		} else if (expr instanceof Variable) {
			return visit((Variable) expr);
		}
		throw error(null, "Unhanded expression type");
	}

	protected static boolean truthy(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof Boolean) {
			return (boolean) object;
		}
		return true;
	}

	protected static boolean isEqual(Object left, Object right) {
		if (left == null && right == null) {
			return true;
		}
		if (left == null) {
			return false;
		}
		return left.equals(right);
	}

	protected static void checkNumber(Token operator, Object operand) {
		if (operand instanceof Double) {
			return;
		}
		throw new RuntimeError(operator, "Operand must be number!");
	}

	protected static void checkNumbers(Token operator, Object left, Object right) {
		if (left instanceof Double && right instanceof Double) {
			return;
		}
		throw new RuntimeError(operator, "Operands must be number!");
	}

	protected static void checkString(Token operator, Object operand) {
		if (operand instanceof String) {
			return;
		}
		throw new RuntimeError(operator, "Operand must be string!");
	}

	protected static void checkStrings(Token operator, Object left, Object right) {
		if (left instanceof String && right instanceof String) {
			return;
		}
		throw new RuntimeError(operator, "Operands must be string!");
	}

	protected RuntimeError error(Token token, String message) {
		return new RuntimeError(token, message);
	}
}
