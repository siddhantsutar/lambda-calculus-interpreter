package com.siddhantsutar.lambda_calculus_interpreter;

import java.util.HashMap;
import java.util.Map;

public class Token {
	
	public static final int COMBINATOR	= 1001;
	public static final int EVALUATE = 1002;
	public static final int DICTIONARY = 1003;
	public static final int SET = 1004;
	
	public static final int INT = 1100;
	public static final int FLOAT = 1101;
	public static final int STRING = 1102;

	public static final int SEMICOLON = 2000;	
	public static final int OPENPAREN = 2001;	
	public static final int CLOSEPAREN = 2002;	
	public static final int OPENBRACKET = 2003;
	public static final int CLOSEBRACKET = 2004;
	public static final int OPENBRACE = 2005;
	public static final int CLOSEBRACE = 2006;
	public static final int COMMA = 2007;

	public static final int SLASH = 3000;
	public static final int DOT = 3001;
	public static final int PLUS = 3002;	
	public static final int MINUS = 3003;	
	public static final int MULTIPLY = 3004;	
	public static final int DIVIDE = 3005;	
	public static final int ASSIGN = 3006;	
	public static final int EQUALTO = 3007;	
	public static final int LESSTHAN = 3008;	
	public static final int GREATERTHAN = 3009;	
	public static final int NOTEQUALTO = 3010;
	public static final int AND = 3011;
	public static final int OR = 3012;
	public static final int NOT = 3013;
	public static final int LENGTH = 3014;

	public static final int IDENTIFIER = 4000;
	public static final int CIDENTIFIER = 4001;
	public static final int INTLIT = 4002;
	public static final int FLOATLIT = 4003;
	public static final int STRINGLIT = 4004;

	public static final int EOF = 5000;
	public static final int EOF_SL = 5001;
	public static final int UNKNOWN = 6000;

	public static final Map<String, Integer> getMap() {
		Map<String, Integer> map = new HashMap<>();
		map.put("combinator", COMBINATOR);
		map.put("evaluate", EVALUATE);
	 	map.put("dictionary", DICTIONARY);
	  	map.put("set", SET);
		map.put("int", INT);
		map.put("float", FLOAT);
		map.put("string", STRING);
		map.put(";", SEMICOLON);
		map.put("(", OPENPAREN);
		map.put(")", CLOSEPAREN);
		map.put("[", OPENBRACKET);
		map.put("]", CLOSEBRACKET);
		map.put("{", OPENBRACE);
		map.put("}", CLOSEBRACE);
		map.put(",", COMMA);
		map.put("\\", SLASH);
		map.put(".", DOT);
		map.put("+", PLUS);
		map.put("-", MINUS);
		map.put("*", MULTIPLY);
		map.put("/", DIVIDE);
		map.put(":=", ASSIGN);
		map.put("==", EQUALTO);
		map.put("<", LESSTHAN);
		map.put(">", GREATERTHAN);
		map.put("<>", NOTEQUALTO);
		map.put("and", AND);
		map.put("or", OR);
		map.put("not", NOT);
		map.put("length", LENGTH);
		return map;
	}

}