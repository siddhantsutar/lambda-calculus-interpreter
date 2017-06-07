package com.siddhantsutar.lambda_calculus_interpreter;

public class CharHandler {
	
	private int data;
	private boolean isStringLiteral;

	public CharHandler(int data, boolean isStringLiteral) {
		this.data = data;
		this.isStringLiteral = isStringLiteral;
	}

	public int getData() {
		return data;
	}

	public boolean getIsStringLiteral() {
		return isStringLiteral;
	}

	public void setData(int data) {
		this.data = data;
	}

	public void setIsStringLiteral(boolean isStringLiteral) {
		this.isStringLiteral = isStringLiteral;
	}

}