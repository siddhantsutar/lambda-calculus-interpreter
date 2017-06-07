package com.siddhantsutar.lambda_calculus_interpreter;

import java.util.HashMap;
import java.util.Map;

public class SetDictionary {
	
	Map<String, Integer> dictionary;

	public SetDictionary() {
		dictionary = new HashMap<>();
	}

	public void put(String key, int value) {
		dictionary.put(key, value);
	}

	public int get(String key) throws InvalidSetVariableException {
		Integer value = dictionary.get(key);
		if (value == null) throw new InvalidSetVariableException();
		return value;
	}
	
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, Integer> each : dictionary.entrySet()) {
			result.append(each.getKey() + ": " + each.getValue().toString() + "\n");
		}
		return result.toString().trim();
	}

}