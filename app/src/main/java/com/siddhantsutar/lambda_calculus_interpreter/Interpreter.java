package com.siddhantsutar.lambda_calculus_interpreter;

public class Interpreter {

	private Lexer lexer;
	private Firebase firebase;

	public Interpreter() {
		firebase = Firebase.getInstance();
		lexer = Lexer.getInstance();
	}

	private Expression recursiveRenameVars(Expression expr, String name, String toname) throws InvalidExpressionTypeException {
		switch (expr.type) {
		case VARIABLE:
			if (expr.name.equals(name)) expr.name = toname;
		case COMBINATOR:
			return expr;
		case SLASHDOT:
			if (!expr.name.equals(name)) expr.updateRight(recursiveRenameVars(expr.right, name, toname));
			return expr;
		case APPLICATION:
			expr.updateLeft(recursiveRenameVars(expr.left, name, toname));
			expr.updateRight(recursiveRenameVars(expr.right, name, toname));
			return expr;
		default:
			throw new InvalidExpressionTypeException();
		}
	}
	
	private Expression markBoundVariables(Expression expr) throws InvalidExpressionTypeException {
		switch (expr.type) {
		case VARIABLE:
		case COMBINATOR:
			return expr;
		case SLASHDOT:
			String name = expr.name;
			String toname = '_' + name;
			expr.name = toname;
			expr.updateRight(recursiveRenameVars(expr.right, name, toname));
			expr.updateRight(markBoundVariables(expr.right));
			return expr;
		case APPLICATION:
			expr.updateLeft(markBoundVariables(expr.left));
			expr.updateRight(markBoundVariables(expr.right));
			return expr;
		default:
			throw new InvalidExpressionTypeException();
		}
	}
	
	private Expression markUnboundVariables(Expression expr) throws InvalidExpressionTypeException {
		switch (expr.type) {
		case VARIABLE:
			char[] exprName = expr.name.toCharArray();
			if (exprName[0] >= 'a' && exprName[0] <= 'z') exprName[0] = (char) (exprName[0] - 'a' + 'A');
			expr.name = String.valueOf(exprName);
		case COMBINATOR:
			return expr;
		case SLASHDOT:
			expr.updateRight(markUnboundVariables(expr.right));
			return expr;
		case APPLICATION:
			expr.updateLeft(markUnboundVariables(expr.left));
			expr.updateRight(markUnboundVariables(expr.right));
			return expr;
		default:
			throw new InvalidExpressionTypeException();
		}
	}
	
	private String integerToName(int countHandler) {
		char[] nameList = "xyzabcdefghijklmnopqrstuvw".toCharArray();
		StringBuilder name = new StringBuilder();
		while (countHandler > 25) {
			name.append(nameList[countHandler % 26]);
			countHandler = countHandler/26;
		}
		name.append(nameList[countHandler]);
		return name.reverse().toString();
	}
	
	private Expression prettyRenameVariables(Expression expr, SearchHandler searchHandler) throws InvalidExpressionTypeException {
		switch (expr.type) {
		case VARIABLE:
		case COMBINATOR:
			return expr;
		case SLASHDOT:
			int current_var = searchHandler.getCount();
			searchHandler.incCount();
			prettyRenameVariables(expr.right, searchHandler);
			String name = expr.name;
			String newName = integerToName(current_var);
			expr.name = newName;
			expr.updateRight(recursiveRenameVars(expr.right, name, newName));
			return expr;
		case APPLICATION:
			expr.updateLeft(prettyRenameVariables(expr.left, searchHandler));
			expr.updateRight(prettyRenameVariables(expr.right, searchHandler));		
			return expr;
		default:
			throw new InvalidExpressionTypeException();
		}
	}
	
	public Expression canonicalRenameBoundVars(Expression expr, SearchHandler searchHandler) throws InvalidExpressionTypeException {
		expr = markBoundVariables(expr);
		expr = markUnboundVariables(expr);
		Expression result = prettyRenameVariables(expr, searchHandler);
		return result;
	}
	
	private Expression recursiveRenameExpr(Expression expr, String name, Expression toexpr) throws InvalidExpressionTypeException {
		switch (expr.type) {
		case VARIABLE:
			if (expr.name.equals(name)) expr = toexpr.copy();
		case COMBINATOR:
			return expr;
		case SLASHDOT:
			if (!expr.name.equals(name)) expr.updateRight(recursiveRenameExpr(expr.right, name, toexpr));
			return expr;
		case APPLICATION:
			expr.updateLeft(recursiveRenameExpr(expr.left, name, toexpr));
			expr.updateRight(recursiveRenameExpr(expr.left, name, toexpr));
			return expr;
		default:
			throw new InvalidExpressionTypeException();
		}
	}
	
	public boolean hasUnboundVars(Expression expr) {
		if (expr != null) {
			if (expr.type == Expression.ExpressionType.VARIABLE && Character.isUpperCase(expr.name.charAt(0))) return true;
			return hasUnboundVars(expr.left) || hasUnboundVars(expr.right);
		}
		return false;
	}
	
	private boolean isSubstitutionApplication(Expression expr) {
		return (expr.left != null) && (expr.left.type == Expression.ExpressionType.SLASHDOT || expr.left.type == Expression.ExpressionType.COMBINATOR);
	}

	public Expression evaluateExpr(ExpressionHandler exprHandler, SetDictionary setDictionary, boolean combinatorEvaluate) throws InvalidExpressionTypeException, InvalidSetVariableException, InvalidCombinatorException {
		Expression original = exprHandler.getExpr();
		int preOrderEvaluateInt = setDictionary.get("preOrderEvaluate");
		String evaluationType = null;
		if (preOrderEvaluateInt == 0) evaluationType = "PostOrder";
		else if (preOrderEvaluateInt == 1) evaluationType = "PreOrder";
		else evaluationType = "ShortCircuit";
		if (!combinatorEvaluate) lexer.println(outputFormat(original.toString(), true, evaluationType));
		if (preOrderEvaluateInt == 2) {
			firebase.pull(original.toString());
			return null;
		}
		boolean preOrderEvaluate = (setDictionary.get("preOrderEvaluate") == 1);
		int i = 0;
		SearchHandler searchHandler = new SearchHandler();
		Expression result = orderSearch(exprHandler, searchHandler, preOrderEvaluate);
		while (searchHandler.getFound()) {
			if (i == setDictionary.get("maxEvalSteps") - 1) {
				lexer.println("Maximum number of steps exceeded!");
				break;
			}
			if (result != null) exprHandler.setExpr(result.copy());
			if (setDictionary.get("printLevel") != 0) lexer.println(String.format("%s --- %s", i, exprHandler.getExpr().toString()));
			i++;
			searchHandler.resetFound();
			orderSearch(exprHandler, searchHandler, preOrderEvaluate);
		}
		searchHandler.resetCount();
		result = canonicalRenameBoundVars(exprHandler.getExpr(), searchHandler);
		if (!combinatorEvaluate) lexer.println(outputFormat(result.toString()));
		firebase.push(original.toString(), result.toString());
		return result;
	}
	
	private Expression orderSearch(ExpressionHandler exprHandler, SearchHandler searchHandler, boolean preOrderEvaluate) throws InvalidCombinatorException, InvalidExpressionTypeException {
		Expression expr = exprHandler.getExpr();
		ExpressionDictionary exprDictionary = exprHandler.getExprDictionary();
		if (expr != null) {
			if (preOrderEvaluate && isSubstitutionApplication(expr)) {
				searchHandler.setFound();
				exprHandler.setExpr(substitution(exprHandler, searchHandler));
			}
			else {
				expr.updateLeft(orderSearch(new ExpressionHandler(expr.left, exprDictionary), searchHandler, preOrderEvaluate));
				expr.updateRight(orderSearch(new ExpressionHandler(expr.right, exprDictionary), searchHandler, preOrderEvaluate));
			}
			if (!preOrderEvaluate && isSubstitutionApplication(expr)) {
				searchHandler.setFound();
				exprHandler.setExpr(substitution(exprHandler, searchHandler));
			}
		}
		return exprHandler.getExpr();
	}
	
	private Expression substitution(ExpressionHandler exprHandler, SearchHandler searchHandler) throws InvalidCombinatorException, InvalidExpressionTypeException {
		Expression expr = exprHandler.getExpr();
		ExpressionDictionary exprDictionary = exprHandler.getExprDictionary();
		switch (expr.left.type) {
		case SLASHDOT:
			String var = expr.left.name;
			Expression toexpr = expr.right.copy();
			Expression rnode = expr.left.right.copy();
			expr = recursiveRenameExpr(rnode, var, toexpr);
			break;
		case COMBINATOR:
			if (!exprDictionary.hasItem(expr.left.name)) throw new InvalidCombinatorException();
			expr.updateLeft(canonicalRenameBoundVars(exprDictionary.getItem(expr.left.name), searchHandler));
			break;
		default:
			break;
		}
		return expr;
	}

	public static String outputFormat(String expression) {
		return outputFormat(expression, false, null);
	}

	private static String outputFormat(String expression, boolean entry, String entryMessage) {
		if (entry) return String.format("evaluate%s with expression %s", entryMessage, expression);
		else return String.format("Expression Evaluates To: %s", expression);
	}
	
}