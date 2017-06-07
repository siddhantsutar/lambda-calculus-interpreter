package com.siddhantsutar.lambda_calculus_interpreter;

public class OutputParseHandler {

	private int token;
	private String ident;
	private Expression expr;
	private int intLiteral;
	private boolean combinatorEvaluate;
	
	public void setToken(int token) {
		this.token = token;
	}
	
	public void setIdent(String ident) {
		this.ident = ident;
	}
	
	public void setExpr(Expression expr) {
		this.expr = expr;
	}
	
	public void setIntLiteral(int intLiteral) {
		this.intLiteral = intLiteral;
	}
	
	public void setCombinatorEvaluate(boolean combinatorEvaluate) {
		this.combinatorEvaluate = combinatorEvaluate;
	}
	
	public int getToken() {
		return token;
	}
	
	public String getIdent() {
		return ident;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public int getIntLiteral() {
		return intLiteral;
	}
	
	public boolean getCombinatorEvaluate() {
		return combinatorEvaluate;
	}
	
}
