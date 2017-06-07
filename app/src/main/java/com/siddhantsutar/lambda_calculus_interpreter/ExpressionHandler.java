package com.siddhantsutar.lambda_calculus_interpreter;

public class ExpressionHandler {

	private Expression expr;
	private ExpressionDictionary exprDictionary;

	public ExpressionHandler(Expression expr, ExpressionDictionary exprDictionary) {
		this.expr = expr;
		this.exprDictionary = exprDictionary;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public ExpressionDictionary getExprDictionary() {
		return exprDictionary;
	}
	
	public void setExpr(Expression expr) {
		this.expr = expr;
	}
	
}