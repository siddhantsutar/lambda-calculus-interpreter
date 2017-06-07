package com.siddhantsutar.lambda_calculus_interpreter;

import android.widget.TextView;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.util.Map;

public class Lexer {
	
	private PushbackInputStream yyin;
	private TextView yyout;
	private int yyleng;
	private StringBuilder yytext;
	private StringBuilder lexeme;
	private static Lexer instance = new Lexer();
	
	public static Lexer getInstance() {
		return instance;
	}
	
	public void setyyin(PushbackInputStream yyin) {
		this.yyin = yyin;
	}
	
	public PushbackInputStream getyyin() {
		return yyin;
	}
	
	public void setyyout(TextView yyout) {
		this.yyout = yyout;
	}

	public void print(String message) {
		yyout.setText(String.format("%s%s", yyout.getText().toString(), message));
	}

	public void println(String message) {
		yyout.setText(String.format("%s%s\n", yyout.getText().toString(), message));
	}
	
	public String getyytext() {
		return yytext.toString();
	}

	private boolean isAlNum(char c) {
		return Character.isLetter(c) || Character.isDigit(c);
	}

	private int isIdent() {
		return isIdent(false);
	}

	private int isIdent(boolean c) {
		int i = 0;
		if (c) {
			if (lexeme.charAt(0) != LexerConstants.C_IDENT) return 0;
			i = 1;
		}
		if (!(Character.isLetter(lexeme.charAt(i++)))) return 0;
		for (; i < lexeme.length(); i++) {
			if (!isAlNum(lexeme.charAt(i)) && lexeme.charAt(i) != '_') return 0;
		}
		return c ? Token.CIDENTIFIER : Token.IDENTIFIER;
	}

	private int isInt() {
		for (int i = 0; i < lexeme.length(); i++) {
			if (!(Character.isDigit(lexeme.charAt(i)))) return 0;
		}
		return Token.INTLIT;
	}

	private int isFloat() {
		boolean point = false;
		int i = 0;
		if (!(Character.isDigit(lexeme.charAt(i)))) return 0;
		for (; i < lexeme.length(); i++) {
    		if (!(Character.isDigit(lexeme.charAt(i)))) {
      			if (!point && lexeme.charAt(i) == '.') {
        			point = true;
        			continue;
     			}
      			return 0;
    		}
  		}
  		return point ? Token.FLOATLIT : 0;
	}

	private int isString() {
		int last = lexeme.length() - 1;
  		return (last > 0 && lexeme.charAt(0) == LexerConstants.STRING_DELIM && lexeme.charAt(last) == LexerConstants.STRING_DELIM) ? Token.STRINGLIT : 0;
	}

	private int isUnknown(Map<String, Integer> dict) {
  		return (lexeme.length() == 1 && lexeme.charAt(0) != LexerConstants.STRING_DELIM && !isAlNum(lexeme.charAt(0)) && dict.get(lexeme.toString()) == null) ? Token.UNKNOWN : 0;
	}

	private int isLexeme(Map<String, Integer> dict) {
		Integer value = dict.get(lexeme.toString());
  		if (value != null) return value;
  		return isIdent() + isIdent(true) + isInt() + isFloat() + isString() + isUnknown(dict);
  	}

	private char gotoNextLine(char currentChar) throws IOException {
		while (currentChar != '\n') currentChar = (char) yyin.read();
		return currentChar;
	}
	
  	private CharHandler getChar(boolean isStringLiteral) throws IOException {
  		int nextCharInt = yyin.read();
  		if (nextCharInt != -1 && !isStringLiteral && lexeme.length() == 0) {
  			char nextChar = (char) nextCharInt;
  			while (nextChar == LexerConstants.COMMENT_DELIM || nextChar == ' ' || nextChar == '\t' || nextChar == '\n') {
  				if (nextChar == LexerConstants.COMMENT_DELIM) nextChar = gotoNextLine(nextChar);
  				else {
  					nextCharInt = yyin.read();
  					nextChar = (char) nextCharInt;  				
  				}
  			}
  		}
  		if (nextCharInt == Character.getNumericValue(LexerConstants.STRING_DELIM)) isStringLiteral = !isStringLiteral;
  		if (nextCharInt != -1) {
  			lexeme.append((char) nextCharInt);
  			yyleng++;
  		}
  		return new CharHandler(nextCharInt, isStringLiteral);
  	}

  	private void putChar() throws IOException {
  		yyin.unread(lexeme.charAt(--yyleng));
  		lexeme.deleteCharAt(yyleng);
  	}

  	private int eofTest(int data, boolean isStringLiteral) {
  		if (data == -1) {
  			if (isStringLiteral) return Token.EOF_SL;
  			return Token.EOF;
  		}
  		return 0;
  	}

  	private void initLexeme() {
  		lexeme = new StringBuilder();
  		yytext = lexeme;
  		yyleng = 0;
  	}

  	public int yylex() throws IOException {
  		CharHandler charHandler;
  		int data;
  		Map<String, Integer> dict = Token.getMap();
  		int prev = 0;
  		int curr = 0;
  		boolean isStringLiteral = false;
  		int eof;

  		initLexeme();

  		while (prev == 0 || curr != 0) {
  			charHandler = getChar(isStringLiteral);
  			data = charHandler.getData();
  			isStringLiteral = charHandler.getIsStringLiteral();
  			if ((eof = eofTest(data, isStringLiteral)) != 0) return eof;
  			prev = curr;
  			curr = isLexeme(dict);
  		}
  		putChar();

  		return prev;
  	}
  	
  	public boolean reachedEOF() throws IOException {
  		int nextCharInt = yyin.read();
  		if (nextCharInt == -1) return true;
  		else {
  			yyin.unread(nextCharInt);
  			return false;
  		}
  	}

}