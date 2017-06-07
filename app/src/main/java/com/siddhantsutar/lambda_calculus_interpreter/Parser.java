package com.siddhantsutar.lambda_calculus_interpreter;

import android.widget.TextView;
import java.io.InputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Parser {

	private Lexer lexer;
	private Interpreter interpreter;
	private ExpressionDictionary combinatorDictionary;
	private SetDictionary variableSettings;
	private OutputParseHandler outputParseHandler;
	private RuleHandler ruleHandler;
	private int parsingDepth;
	private boolean verbose;
	private enum PrintVerboseType {ENTER, FOUND, EXIT};
	
	public Parser(SetDictionary variableSettings, boolean verbose) {
		lexer = Lexer.getInstance();
		interpreter = new Interpreter();
		combinatorDictionary = new ExpressionDictionary();
		this.variableSettings = variableSettings;
		outputParseHandler = new OutputParseHandler();
		ruleHandler = new RuleHandler();
		parsingDepth = -1;
		this.verbose = verbose;
		outputParseHandler.setCombinatorEvaluate(false);
	}

	private String psp(int n) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < n; i++) result.append(' ');
		return result.toString();
	}
	
	private void outputParse(int type) throws InvalidExpressionTypeException, InvalidSetVariableException, InvalidCombinatorException {
		switch(type) {
			case Token.COMBINATOR:
				variableSettings.put("printLevel", 0);
				if (outputParseHandler.getCombinatorEvaluate()) outputParseHandler.setExpr(interpreter.evaluateExpr(new ExpressionHandler(outputParseHandler.getExpr(), combinatorDictionary), variableSettings, true));
				variableSettings.put("printLevel", 1);
				outputParseHandler.setCombinatorEvaluate(false);
				if (!interpreter.hasUnboundVars(interpreter.canonicalRenameBoundVars(outputParseHandler.getExpr(), new SearchHandler()))) combinatorDictionary.insertItem(outputParseHandler.getIdent(), outputParseHandler.getExpr());
				else lexer.println("WARNING: Expression was not added to the dictionary since it is not a combinator.");
				break;
			case Token.EVALUATE:
				outputParseHandler.setExpr(interpreter.evaluateExpr(new ExpressionHandler(outputParseHandler.getExpr(), combinatorDictionary), variableSettings, false));
				break;
			case Token.DICTIONARY:
				printCombinatorDictionary();
				break;
			case Token.SET:
				variableSettings.put(outputParseHandler.getIdent(), outputParseHandler.getIntLiteral());
				break;
		}
	}
	
	public int run(InputStream input, TextView output) throws InvalidSetVariableException, IOException {
		lexer.setyyin(new PushbackInputStream(input));
		lexer.setyyout(output);
		updateOutputParseHandlerToken();
		try {
			if (outputParseHandler.getToken() == Token.EOF) return -1;
			if (!isFirstOfP()) throw new MissingKeywordException();
			P();
		} catch (Exception e) {
			lexer.println(e.getMessage());
			e.printStackTrace();
			while (!lexer.reachedEOF()) {
				updateOutputParseHandlerToken();
				if (outputParseHandler.getToken() == Token.SEMICOLON) break;
			}
			updateOutputParseHandlerToken();
		}
		if (variableSettings.get("preOrderEvaluate") != 2) {
			printCombinatorDictionary();
			printVariableSettings();
		}
		return 0;
	}
	
	private void printCombinatorDictionary() {
		lexer.println("combinatorDictionary is: ");
		lexer.println(combinatorDictionary.toString());
	}
	
	private void printVariableSettings() {
		lexer.println("variableSettings is: ");
		lexer.println(variableSettings.toString());
	}
	
	private void printVerbose(Rule rule, PrintVerboseType type) {
		if (verbose) {
			boolean increment = false;
			if (type == PrintVerboseType.ENTER) {
				parsingDepth++;
				increment = true;
			}
			lexer.print(psp(parsingDepth));
			if (type == PrintVerboseType.FOUND) lexer.println(type.toString() + " " + lexer.getyytext());
			else lexer.println(type.toString() + " " + rule.toString() + " " + ruleHandler.ruleCount(rule, increment));
			if (type == PrintVerboseType.EXIT) parsingDepth--;
		}
	}
		
	private void P() throws InvalidCombinatorException, InvalidExpressionTypeException, InvalidRuleException, InvalidSetVariableException, IOException {
		printVerbose(Rule.P, PrintVerboseType.ENTER);
		if (isFirstOfS()) S();
		else throw new InvalidRuleException(Rule.P);
		while (isFirstOfS()) S();
		printVerbose(Rule.P, PrintVerboseType.EXIT);
	}
	
	private void S() throws InvalidCombinatorException, InvalidExpressionTypeException, InvalidRuleException, InvalidSetVariableException, IOException {
		printVerbose(Rule.S, PrintVerboseType.ENTER);
		int token;
		if (isFirstOfJ()) {
			J();
			token = Token.COMBINATOR;
		} else if (isFirstOfK()) {
			K();
			token = Token.EVALUATE;
		} else if (isFirstOfL()) {
			L();
			token = Token.DICTIONARY;
		} else if (isFirstOfM()) {
			M();
			token = Token.SET;
		} else throw new InvalidRuleException(Rule.S);
		if (outputParseHandler.getToken() != Token.SEMICOLON) throw new InvalidRuleException(Rule.S);
		printVerbose(Rule.S, PrintVerboseType.FOUND);
		outputParse(token);
		updateOutputParseHandlerToken();
		printVerbose(Rule.S, PrintVerboseType.EXIT);
	}
	
	private void J() throws InvalidRuleException, IOException {
		printVerbose(Rule.J, PrintVerboseType.ENTER);
		Expression expr = null;
		printVerbose(Rule.J, PrintVerboseType.FOUND);
		updateOutputParseHandlerToken();
		if (outputParseHandler.getToken() == Token.EVALUATE) {
			outputParseHandler.setCombinatorEvaluate(true);
			printVerbose(Rule.J, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
		}
		if (outputParseHandler.getToken() != Token.IDENTIFIER) throw new InvalidRuleException(Rule.J);
		printVerbose(Rule.J, PrintVerboseType.FOUND);
		String ident = lexer.getyytext();
		updateOutputParseHandlerToken();
		if (isFirstOfA()) expr = A();
		printVerbose(Rule.J, PrintVerboseType.EXIT);
		outputParseHandler.setIdent(ident);
		outputParseHandler.setExpr(expr);
	}
	
	private void K() throws IOException, InvalidRuleException {
		printVerbose(Rule.K, PrintVerboseType.ENTER);
		Expression expr = null;
		printVerbose(Rule.K, PrintVerboseType.FOUND);
		updateOutputParseHandlerToken();
		if (isFirstOfA()) expr = A();
		else throw new InvalidRuleException(Rule.K);
		printVerbose(Rule.K, PrintVerboseType.EXIT);
		outputParseHandler.setExpr(expr);
	}
	
	private void L() throws IOException {
		printVerbose(Rule.L, PrintVerboseType.ENTER);
		printVerbose(Rule.L, PrintVerboseType.FOUND);
		updateOutputParseHandlerToken();
		printVerbose(Rule.L, PrintVerboseType.EXIT);
	}
	
	private void M() throws InvalidRuleException, IOException {
		printVerbose(Rule.M, PrintVerboseType.ENTER);
		int intLiteral;
		printVerbose(Rule.M, PrintVerboseType.FOUND);
		updateOutputParseHandlerToken();
		if (outputParseHandler.getToken() != Token.IDENTIFIER) throw new InvalidRuleException(Rule.M);
		printVerbose(Rule.M, PrintVerboseType.FOUND);
		String ident = lexer.getyytext();
		updateOutputParseHandlerToken();
		if (isFirstOfE()) intLiteral = E();
		else throw new InvalidRuleException(Rule.M);
		printVerbose(Rule.M, PrintVerboseType.EXIT);
		outputParseHandler.setIdent(ident);
		outputParseHandler.setIntLiteral(intLiteral);
	}
	
	private Expression A() throws InvalidRuleException, IOException {
		printVerbose(Rule.A, PrintVerboseType.ENTER);
		Expression expr = null;
		if (isFirstOfI()) expr = I();
		else throw new InvalidRuleException(Rule.A);
		while (isFirstOfI()) expr = Expression.makeApplication(expr, I());
		printVerbose(Rule.A, PrintVerboseType.EXIT);
		return expr;
	}
	
	private Expression I() throws InvalidRuleException, IOException {
		printVerbose(Rule.I, PrintVerboseType.ENTER);
		Expression expr = null;
		switch (outputParseHandler.getToken()) {
		case Token.CIDENTIFIER:
			printVerbose(Rule.I, PrintVerboseType.FOUND);
			expr = Expression.makeCombinator(lexer.getyytext().substring(1));
			updateOutputParseHandlerToken();
			break;
		case Token.IDENTIFIER:
			printVerbose(Rule.I, PrintVerboseType.FOUND);
			expr = Expression.makeVariable(lexer.getyytext());
			updateOutputParseHandlerToken();
			break;
		case Token.OPENPAREN:
			printVerbose(Rule.I, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			if (!isFirstOfA()) throw new InvalidRuleException(Rule.I);
			expr = A();
			if (outputParseHandler.getToken() == Token.CLOSEPAREN) {
				printVerbose(Rule.I, PrintVerboseType.FOUND);
				updateOutputParseHandlerToken();
			}
			else throw new InvalidRuleException(Rule.I);
			break;
		default:
			if (isFirstOfF()) expr = F();
			else throw new InvalidRuleException(Rule.I);
			break;
		}
		printVerbose(Rule.I, PrintVerboseType.EXIT);
		return expr;
	}
	
	private Expression F() throws InvalidRuleException, IOException {
		printVerbose(Rule.F, PrintVerboseType.ENTER);
		Expression expr = null;
		switch (outputParseHandler.getToken()) {
		case Token.SLASH:
			printVerbose(Rule.F, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			if (outputParseHandler.getToken() != Token.IDENTIFIER) throw new InvalidRuleException(Rule.F);
			String yytext = lexer.getyytext();
			printVerbose(Rule.F, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			if (outputParseHandler.getToken() != Token.DOT) throw new InvalidRuleException(Rule.F);
			printVerbose(Rule.F, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			if (!isFirstOfA()) throw new InvalidRuleException(Rule.F);
			expr = Expression.makeSlashDot(yytext, A());
			break;
		case Token.OPENBRACE:
			printVerbose(Rule.F, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			if (!isFirstOfF()) throw new InvalidRuleException(Rule.F);
			expr = F();
			if (outputParseHandler.getToken() == Token.CLOSEBRACE) {
				printVerbose(Rule.F, PrintVerboseType.FOUND);
				updateOutputParseHandlerToken();
			}
			else throw new InvalidRuleException(Rule.F);
			break;
		default:
			throw new InvalidRuleException(Rule.F);
		}
		printVerbose(Rule.F, PrintVerboseType.EXIT);
		return expr;
	}
	
	private int E() throws InvalidRuleException, IOException {
		int rValue1 = 0, rValue2 = 0;
		printVerbose(Rule.E, PrintVerboseType.ENTER);
		if (isFirstOfT()) rValue1 = T();
		else throw new InvalidRuleException(Rule.E);
		int token = outputParseHandler.getToken();
		while (token == Token.PLUS || token == Token.MINUS) {
			printVerbose(Rule.E, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			if (isFirstOfT()) rValue2 = T();
			else throw new InvalidRuleException(Rule.E);
		}
		switch (token) {
		case Token.PLUS:
			rValue1 = rValue1 + rValue2;
			break;
		case Token.MINUS:
			rValue1 = rValue1 - rValue2;
			break;
		}
		printVerbose(Rule.E, PrintVerboseType.EXIT);
		return rValue1;
	}
	
	private int T() throws InvalidRuleException, IOException {
		int rValue1 = 0, rValue2 = 0;
		printVerbose(Rule.T, PrintVerboseType.ENTER);
		if (isFirstOfR()) rValue1 = R();
		else throw new InvalidRuleException(Rule.T);
		int token = outputParseHandler.getToken();
		while (token == Token.MULTIPLY || token == Token.DIVIDE) {
			printVerbose(Rule.T, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			if (isFirstOfR()) rValue2 = R();
			else throw new InvalidRuleException(Rule.T);
		}
		switch (token) {
		case Token.MULTIPLY:
			rValue1 = rValue1 * rValue2;
			break;
		case Token.DIVIDE:
			rValue1 = rValue1 / rValue2;
			break;
		}
		printVerbose(Rule.T, PrintVerboseType.EXIT);
		return rValue1;
	}
	
	private int R() throws InvalidRuleException, IOException {
		int rValue = 0;
		printVerbose(Rule.R, PrintVerboseType.ENTER);
		switch (outputParseHandler.getToken()) {
		case Token.INTLIT:
			printVerbose(Rule.R, PrintVerboseType.FOUND);
			rValue = Integer.valueOf(lexer.getyytext());
			updateOutputParseHandlerToken();
			break;
		case Token.OPENPAREN:
			printVerbose(Rule.R, PrintVerboseType.FOUND);
			updateOutputParseHandlerToken();
			rValue = E();
			if (outputParseHandler.getToken() == Token.CLOSEPAREN) {
				printVerbose(Rule.R, PrintVerboseType.FOUND);
				updateOutputParseHandlerToken();
			}
			else throw new InvalidRuleException(Rule.R);
			break;
		default:
			throw new InvalidRuleException(Rule.R);
		}
		return rValue;
	}
	
	private void updateOutputParseHandlerToken() throws IOException {
		outputParseHandler.setToken(lexer.yylex());
	}
	
	private boolean isFirstOfP() {
		return outputParseHandler.getToken() == Token.COMBINATOR || outputParseHandler.getToken() == Token.EVALUATE || outputParseHandler.getToken() == Token.DICTIONARY || outputParseHandler.getToken() == Token.SET;
	}

	private boolean isFirstOfS() {
		return outputParseHandler.getToken() == Token.COMBINATOR || outputParseHandler.getToken() == Token.EVALUATE || outputParseHandler.getToken() == Token.DICTIONARY || outputParseHandler.getToken() == Token.SET;
	}

	private boolean isFirstOfJ() {
		return outputParseHandler.getToken() == Token.COMBINATOR;
	}

	private boolean isFirstOfK() {
		return outputParseHandler.getToken() == Token.EVALUATE;
	}

	private boolean isFirstOfL() {
		return outputParseHandler.getToken() == Token.DICTIONARY;
	}

	private boolean isFirstOfM() {
		return outputParseHandler.getToken() == Token.SET;
	}

	private boolean isFirstOfA() {
		return outputParseHandler.getToken() == Token.IDENTIFIER || outputParseHandler.getToken() == Token.CIDENTIFIER || outputParseHandler.getToken() == Token.SLASH || outputParseHandler.getToken() == Token.OPENPAREN || outputParseHandler.getToken() == Token.OPENBRACE;
	}

	private boolean isFirstOfF() {
		return outputParseHandler.getToken() == Token.SLASH || outputParseHandler.getToken() == Token.OPENBRACE;
	}

	private boolean isFirstOfI() {
		return outputParseHandler.getToken() == Token.IDENTIFIER || outputParseHandler.getToken() == Token.CIDENTIFIER || outputParseHandler.getToken() == Token.SLASH || outputParseHandler.getToken() == Token.OPENPAREN || outputParseHandler.getToken() == Token.OPENBRACE;
	}

	private boolean isFirstOfE() {
		return outputParseHandler.getToken() == Token.OPENPAREN || outputParseHandler.getToken() == Token.INTLIT;
	}

	private boolean isFirstOfT() {
		return outputParseHandler.getToken() == Token.OPENPAREN || outputParseHandler.getToken() == Token.INTLIT;
	}

	private boolean isFirstOfR() {
		return outputParseHandler.getToken() == Token.OPENPAREN || outputParseHandler.getToken() == Token.INTLIT;
	}
	
}