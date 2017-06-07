package com.siddhantsutar.lambda_calculus_interpreter;

public class Expression {
	
	public enum ExpressionType {VARIABLE, COMBINATOR, SLASHDOT, APPLICATION};
	public ExpressionType type;
	public String name;
	public Expression left;
	public Expression right;

	public Expression(ExpressionType type) {
		this.type = type;
		name = null;
		left = null;
		right = null;
	}

	public Expression(ExpressionType type, String name) {
		this.type = type;
		this.name = name;
		left = null;
		right = null;
	}

	public Expression(ExpressionType type, String name, Expression expr) {
		this.type = type;
		this.name = name;
		left = null;
		right = expr;
	}

	public Expression(ExpressionType type, Expression expr1, Expression expr2) {
		this.type = type;
		name = null;
		left = expr1;
		right = expr2;
	}

	public Expression updateLeft(Expression p) {
		left = p;
		return p;
	}

	public Expression updateRight(Expression p) {
		right = p;
		return p;
	}

	public Expression copy() throws InvalidExpressionTypeException {
		switch (type) {
			case VARIABLE:
			case COMBINATOR:
				return new Expression(type, name);
			case SLASHDOT:
				return new Expression(type, name, right.copy());
			case APPLICATION:
				return new Expression(type, left.copy(), right.copy());
			default:
				throw new InvalidExpressionTypeException();
		}
	}

	@Override public String toString() {
		switch (type) {
			case VARIABLE:
				return name;
			case COMBINATOR:
				return "$" + name;
			case SLASHDOT:
      			return "{\\" + name + "." + right.toString() + "}";
      		case APPLICATION:
      			return "(" + left.toString() + " " + right.toString() + ")";
		}
		return "<{Err}>";
	}

	public static Expression makeVariable(String name) {
		return new Expression(ExpressionType.VARIABLE, name);
	}

	public static Expression makeCombinator(String name) {
		return new Expression(ExpressionType.COMBINATOR, name);
	}

	public static Expression makeSlashDot(String name, Expression argument) {
		return new Expression(ExpressionType.SLASHDOT, name, argument);
	}

	public static Expression makeApplication(Expression left, Expression right) {
		return new Expression(ExpressionType.APPLICATION, left, right);
	}

}