package com.siddhantsutar.lambda_calculus_interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionDictionary {
	
	private Map<String, Expression> dictionary;

	public ExpressionDictionary() {
		dictionary = new HashMap<>();
	}

	public int size() {
		return dictionary.size();
	}

	public List<String> nameList() {
		List<String> result = new ArrayList<>();
		for (String each : dictionary.keySet()) result.add(each);
		return result;
	}

	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, Expression> each : dictionary.entrySet()) {
			result.append(each.getKey() + ": " + each.getValue().toString() + "\n");
		}
		return result.toString().trim();
	}

	public boolean hasItem(String name) {
		return (dictionary.get(name) != null);
	}

	public void insertItem(String name, Expression e) {
		dictionary.put(name, e);
	}

	public Expression getItem(String name) throws InvalidExpressionTypeException {
		Expression result = dictionary.get(name);
		return result != null ? result.copy() : null;
	}

	public String getItemString(String name) {
		Expression result = dictionary.get(name);
		return result != null ? result.toString() : "<ERR>";
	}

}