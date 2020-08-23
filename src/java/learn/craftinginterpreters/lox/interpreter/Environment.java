package learn.craftinginterpreters.lox.interpreter;

import java.util.HashMap;
import java.util.Map;

import learn.craftinginterpreters.lox.RuntimeError;
import learn.craftinginterpreters.lox.lexer.Token;

public class Environment {
	private final Environment parent;
	private final Map<String, Object> variables;

	public Environment() {
		parent = null;
		variables = new HashMap<>();
	}

	public Environment(Environment parent) {
		this.parent = parent;
		variables = new HashMap<>();
	}

	public boolean hasVariable(String name) {
		if (!variables.containsKey(name)) {
			if (parent != null) {
				return parent.hasVariable(name);
			}
			return false;
		}
		return true;
	}

	public Object getValue(String name) {
		if (!variables.containsKey(name)) {
			if (parent != null) {
				return parent.getValue(name);
			}
		}
		return variables.get(name);
	}

	public void init(String name) {
		init(name, null);
	}

	public void init(String name, Object value) {
		variables.put(name, value);
	}

	public void assign(Token token, Object value) {
		if (variables.containsKey(token.getLexem())) {
			variables.put(token.getLexem(), value);
		} else if (parent != null) {
			parent.assign(token, value);
		}
		throw new RuntimeError(token, "Undeclared variable " + token.getLexem());
	}

	public Environment getParent() {
		return parent;
	}
}
