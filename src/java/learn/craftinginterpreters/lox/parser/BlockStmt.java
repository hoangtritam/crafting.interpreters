package learn.craftinginterpreters.lox.parser;

import java.util.ArrayList;
import java.util.List;

import learn.craftinginterpreters.lox.lexer.Token;

public class BlockStmt implements Stmt {

	final Token opening;
	final Token closing;
	final List<Stmt> stmts;

	public BlockStmt(Token opening, Token closing, List<Stmt> stmts) {
		this.opening = opening;
		this.closing = closing;
		this.stmts = new ArrayList<>();
		this.stmts.addAll(stmts);
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}
	
	public Iterable<Stmt> getStatements() {
		return this.stmts;
	}
}
