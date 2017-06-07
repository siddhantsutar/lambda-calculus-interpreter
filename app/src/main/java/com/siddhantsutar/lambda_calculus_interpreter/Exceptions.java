package com.siddhantsutar.lambda_calculus_interpreter;

@SuppressWarnings("serial")
class InvalidExpressionTypeException extends Exception {
	
	public InvalidExpressionTypeException() {
		super("Invalid expression type.");
	}

}

@SuppressWarnings("serial")
class InvalidProgramStatementException extends Exception {
	
	public InvalidProgramStatementException() {
		super("Invalid program statement.");
	}

}

@SuppressWarnings("serial")
class InvalidSetVariableException extends Exception {

	public InvalidSetVariableException() {
		super("Invalid set variable.");
	}
	
}

@SuppressWarnings("serial")
class MissingKeywordException extends Exception {

	public MissingKeywordException() {
		super("Missing keyword.");
	}
	
}

@SuppressWarnings("serial")
class InvalidRuleException extends Exception {
	
	public InvalidRuleException(Rule rule) {
		super(rule.toString() + " error.");
	}
	
}

@SuppressWarnings("serial")
class InvalidCombinatorException extends Exception {
	
	public InvalidCombinatorException() {
		super("Invalid combinator variable.");
	}
	
}