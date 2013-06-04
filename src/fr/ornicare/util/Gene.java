package fr.ornicare.util;

import fr.ornicare.global.GlobalVars;

public class Gene {

	private double value;
	private double expressionValue;
	
	public Gene(double a, double b) {
		expressionValue = b;
		value = a;
	}

	public double getExpressionValue() {
		return expressionValue;
	}

	public double getValue() {
		return value;
	}
	
	public void mutate() {
		expressionValue+=MathHelper.random(GlobalVars.mutationAmplitude, -GlobalVars.mutationAmplitude);
		value+=MathHelper.random(GlobalVars.mutationAmplitude, -GlobalVars.mutationAmplitude);
	}

	public void setValue(double v) {
		value = v;
	}

	
}
