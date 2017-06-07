package com.siddhantsutar.lambda_calculus_interpreter;

public class SearchHandler {

	private int count;
	private boolean found;
	
	public SearchHandler() {
		resetCount();
		resetFound();
	}
	
	public int getCount() {
		return count;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public void incCount() {
		count++;
	}
	
	public void resetCount() {
		count = 0;
	}
	
	public void resetFound() {
		found = false;
	}
	
	public void setFound() {
		found = true;
	}
	
}