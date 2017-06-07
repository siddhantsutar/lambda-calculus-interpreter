package com.siddhantsutar.lambda_calculus_interpreter;

import java.util.HashMap;
import java.util.Map;

public class RuleHandler {
	
	private Map<Rule, Integer> ruleCounter;
	
	public RuleHandler() {
		ruleCounter = new HashMap<>();
		for (Rule rule : Rule.values()) ruleCounter.put(rule, 0);
	}

	public int ruleCount(Rule rule, boolean increment) {
		if (increment) ruleCounter.put(rule, ruleCounter.get(rule)+1);
		return ruleCounter.get(rule);
	}

}