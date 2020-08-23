package learn.craftinginterpreters.lox.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learn.craftinginterpreters.lox.Lox;

public class Lexer {

	private static final Map<String, Type> KEYWORDS;

	static {
		KEYWORDS = new HashMap<>();
		KEYWORDS.put("and", Type.AND);
		KEYWORDS.put("class", Type.CLASS);
		KEYWORDS.put("else", Type.ELSE);
		KEYWORDS.put("false", Type.FALSE);
		KEYWORDS.put("for", Type.FOR);
		KEYWORDS.put("fun", Type.FUN);
		KEYWORDS.put("if", Type.IF);
		KEYWORDS.put("nil", Type.NIL);
		KEYWORDS.put("or", Type.OR);
		KEYWORDS.put("print", Type.PRINT);
		KEYWORDS.put("return", Type.RETURN);
		KEYWORDS.put("super", Type.SUPER);
		KEYWORDS.put("this", Type.THIS);
		KEYWORDS.put("true", Type.TRUE);
		KEYWORDS.put("var", Type.VAR);
		KEYWORDS.put("while", Type.WHILE);
	}

	// TODO: case read a single file only: perhaps large file
	// TODO: parse a stream of character and analyze them lively
	// TODO: user a buffer
	// TODO: improve the scanning of string, numbers, identifiers
	// TODO: improve the handling of keywords

	private final String source;
	private final List<Token> tokens;

	private int start;
	private int current;
	private int line;

	public Lexer(String source) {
		this.source = source;
		this.tokens = new ArrayList<Token>();
		this.start = 0;
		this.current = 0;
		this.line = 1;
	}

	public List<Token> scan() {
		while (!eof()) {
			start = current;
			scanNext();
		}
		tokens.add(new Token(Type.EOF, "", null, line));
		return tokens;
	}

	private void scanNext() {
		char c = advance();
		switch (c) {
		case '(':
			addToken(Type.LEFT_PAREN);
			break;
		case ')':
			addToken(Type.RIGHT_PAREN);
			break;
		case '{':
			addToken(Type.LEFT_BRACE);
			break;
		case '}':
			addToken(Type.RIGHT_BRACE);
			break;
		case ',':
			addToken(Type.COMMA);
			break;
		case '.':
			addToken(Type.DOT);
			break;
		case '-':
			addToken(Type.MINUS);
			break;
		case '+':
			addToken(Type.PLUS);
			break;
		case ';':
			addToken(Type.SEMICOLON);
			break;
		case ':':
			addToken(Type.COLON);
			break;
		case '*':
			addToken(Type.STAR);
			break;
		case '?':
			addToken(Type.QUESTION);
			break;

		case '!':
			addToken(match('=') ? Type.BANG_EQUAL : Type.BANG);
			break;
		case '=':
			addToken(match('=') ? Type.EQUAL_EQUAL : Type.EQUAL);
			break;
		case '<':
			addToken(match('=') ? Type.LESS_EQUAL : Type.LESS);
			break;
		case '>':
			addToken(match('=') ? Type.GREATER_EQUAL : Type.GREATER);
			break;

		case '/': {
			if (match('/')) {
				while (peek() != '\n' && !eof()) {
					advance();
				}
				addToken(Type.COMMENT, source.substring(start + 2, current));
			} else if (match('*')) {
				blockComment();
			} else {
				addToken(Type.SLASH);
			}
			break;
		}

		case '"':
			string();
			break;

		case ' ':
		case '\r':
		case '\t':
			// ignore comment
			break;

		case '\n':
			line++;
			break;

		default:
			if (isDigit(c)) {
				number();
			} else if (isAlpha(c)) {
				identifier();
			} else {
				Lox.error(line, "Unexpected character: " + c);
			}
		}
	}

	private void number() {
		while (isDigit(peek())) {
			advance();
		}

		if (peek() == '.' && isDigit(peekNext())) {
			advance();

			while (isDigit(peek())) {
				advance();
			}
		}

		Double d = Double.parseDouble(source.substring(start, current));
		addToken(Type.NUMBER, d);
	}

	private void blockComment() {
		while (!eof()) {
			char c = advance();
			switch (c) {
			case '"': {
				boolean endOfString = false;
				while (!eof()) {
					char d = advance();
					if (d == '\n') {
						line++;
					} else if (d == '"') {
						endOfString = true;
						break;
					}
				}
				if (!endOfString) {
					Lox.error(line, "Unterminated string in block comment.");
				}
				break;
			}
			case '\n':
				line++;
				break;
			case '*':
				if (match('/')) {
					addToken(Type.COMMENT, source.substring(start + 2, current - 2));
					return;
				}
			}
		}
		Lox.error(line, "Unterminated block comment.");
	}

	private void string() {
		while (peek() != '"' && !eof()) {
			if (peek() == '\n') {
				line++;
			}
			advance();
		}

		if (eof()) {
			Lox.error(line, "Error unterminated string.");
		}

		advance();

		String str = source.substring(start + 1, current - 1);
		addToken(Type.STRING, str);
	}

	private void identifier() {
		while (isAlphaNumeric(peek())) {
			advance();
		}

		String value = source.substring(start, current);
		if (KEYWORDS.containsKey(value)) {
			// TODO: here perform substring twice
			addToken(KEYWORDS.get(value));
		} else {
			// TODO: here perform substring twice
			addToken(Type.IDENTIFIER);
		}
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private char advance() {
		current++;
		return source.charAt(current - 1);
	}

	private boolean match(char expected) {
		if (eof()) {
			return false;
		}
		if (source.charAt(current) != expected) {
			return false;
		}

		current++;
		return true;
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private char peek() {
		if (eof()) {
			return '\0';
		}
		return source.charAt(current);
	}

	private char peekNext() {
		if (current > source.length() + 2) {
			return '\0';
		}
		return source.charAt(current + 1);
	}

	private boolean eof() {
		return current >= source.length();
	}

	private void addToken(Type type) {
		addToken(type, null);
	}

	private void addToken(Type type, Object literal) {
		Token token = new Token(type, source.substring(start, current), literal, line);
		tokens.add(token);
	}
}
