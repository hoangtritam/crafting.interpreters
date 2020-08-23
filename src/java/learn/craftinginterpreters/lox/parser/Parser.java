package learn.craftinginterpreters.lox.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import learn.craftinginterpreters.lox.Lox;
import learn.craftinginterpreters.lox.lexer.Token;
import learn.craftinginterpreters.lox.lexer.Type;

/**
 * A top-down recursive descent parser
 * 
 * @author tritamhoang
 */
public class Parser {

	List<Stmt> statements;

	@SuppressWarnings("serial")
	public static class ParserError extends RuntimeException {
	}

	// TODO: switch the implementation to LR(1)
	// TODO: implement bottom up parsing

	private final List<Token> tokens;
	private int current;

	public Parser(List<Token> tokens) {
		this.tokens = new ArrayList<Token>();
		this.tokens.addAll(tokens);
		this.current = 0;
		statements = new ArrayList<>();
	}

	public List<Stmt> parse() {
		while (!eof()) {
			Stmt s = statement();
			if (s != null) {
				statements.add(blockStatement());
			}
		}
		return statements;
	}
	
	protected Stmt blockStatement() {
		if (match(Type.LEFT_BRACE)) {
			Token opening = previous();
			List<Stmt> stmts = new LinkedList<>();
			while (!eof() && match(Type.RIGHT_BRACE)) {
				Stmt s = statement();
				if (s != null) {
					stmts.add(statement());
				}
			}
			
			Token previous = previous();
			if (previous.match(Type.RIGHT_BRACE))
			{
				return new BlockStmt(opening, previous, stmts);
			}
			throw error(opening, "Expect } for block statement.");
		}
		return statement();
	}

	protected Stmt statement() {
		try {
			if (match(Type.PRINT)) {
				return printStatement();
			} else if (match(Type.VAR)) {
				return varDeclaration();
			}
			return expressionStatement();
		} catch (ParserError e) {
			synchronize();
			return null;
		}
	}

	protected VarDeclareStmt varDeclaration() {
		Token var = consume("var keyword requires an identifier;", Type.IDENTIFIER);
		Expr init = null;
		if (match(Type.EQUAL)) {
			init = expr();
		}
		consume("Missing semi colon at variable declaration.", Type.SEMICOLON);
		return new VarDeclareStmt(var, init);
	}

	protected PrintStmt printStatement() {
		Expr expr = expr();
		consume("Print statement is missing semi-colon ;", Type.SEMICOLON);
		return new PrintStmt(expr);
	}

	protected ExpressionStmt expressionStatement() {
		Expr expr = expr();
		consume("Expression statement is missing semi-colon ;", Type.SEMICOLON);
		return new ExpressionStmt(expr);
	}

	protected Expr expr() {
		Expr expr = ternary();
		while (match(Type.COMMA)) {
			expr = new Binary(expr, previous(), ternary());
		}
		return expr;
	}
	
	protected Expr assignment() {
		Expr expr = ternary();
		if (match(Type.EQUAL)) {
			Token equal = previous();
			Expr value = assignment();
			
			if (expr instanceof Variable) {
				Token identifier = ((Variable) expr).getIdentifier();
				return new Assignment(identifier, value);
			}
			
			throw error(equal, "Invalid assignement.");
		}
		return expr;
	}

	protected Expr ternary() {
		Expr expr = equality();
		if (match(Type.QUESTION)) {
			Expr first = ternary();
			consume("Missing colon ':' ternary operator.", Type.COLON);
			Expr second = ternary();
			return new Ternary(expr, first, second);
		}
		return expr;
	}

	protected Expr equality() {
		Expr expr = compare();
		while (match(Type.BANG_EQUAL, Type.EQUAL_EQUAL)) {
			// if matched, current index is advanced, and previous is the matching operator
			expr = new Binary(expr, previous(), compare());
		}
		return expr;
	}

	protected Expr compare() {
		Expr expr = addition();
		while (match(Type.LESS_EQUAL, Type.LESS, Type.GREATER_EQUAL, Type.GREATER)) {
			expr = new Binary(expr, previous(), addition());
		}
		return expr;
	}

	protected Expr addition() {
		Expr expr = multiplication();
		while (match(Type.PLUS, Type.MINUS)) {
			expr = new Binary(expr, previous(), multiplication());
		}
		return expr;
	}

	protected Expr multiplication() {
		Expr expr = unary();
		while (match(Type.STAR, Type.SLASH)) {
			expr = new Binary(expr, previous(), unary());
		}
		return expr;
	}

	protected Expr unary() {
		if (match(Type.BANG, Type.MINUS)) {
			return new Unary(previous(), unary());
		}
		return primary();
	}

	protected Expr primary() {
		if (match(Type.TRUE)) {
			return new Literal(true);
		}
		if (match(Type.FALSE)) {
			return new Literal(false);
		}
		if (match(Type.NIL)) {
			return new Literal(null);
		}
		if (match(Type.NUMBER, Type.STRING)) {
			return new Literal(previous().getLiteral());
		}
		if (match(Type.LEFT_PAREN)) {
			Expr expr = new Grouping(expr());
			consume("Missing closing parenthesis.", Type.RIGHT_PAREN);
			return expr;
		}
		if (match(Type.IDENTIFIER)) {
			return new Variable(previous());
		}
		Lox.error(peek().getLine(), "Parsing error: primary expected.");
		return null;
	}

	protected void synchronize() {
		while (!eof()) {
			if (peek().match(Type.SEMICOLON)) {
				advance();
				return;
			}

			switch (peek().getType()) {
			case CLASS:
			case FUN:
			case IF:
			case WHILE:
			case FOR:
			case VAR:
			case RETURN:
			case PRINT:
				return;
			default:
				break;
			}
		}
	}

	protected Token consume(String errorMsg, Type... types) {
		if (peek().match(types)) {
			advance();
			return previous();
		}
		throw error(peek(), errorMsg);
	}

	protected boolean match(Type... types) {
		if (peek().match(types)) {
			advance();
			return true;
		}
		return false;
	}

	protected Token advance() {
		if (!eof()) {
			current++;
		}
		return peek();
	}

	protected Token previous() {
		if (current <= 0) {
			System.err.println("Index out of bound during parsing.");
			System.exit(67);
		}
		return tokens.get(current - 1);
	}

	protected Token peek() {
		return tokens.get(current);
	}

	protected boolean eof() {
		return peek().match(Type.EOF);
	}

	protected ParserError error(Token token, String message) {
		Lox.error(token.getLine(), message);
		return new ParserError();
	}
}
