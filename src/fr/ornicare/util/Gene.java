package fr.ornicare.util;

import fr.ornicare.global.GlobalVars;

public class Gene {

	private double value;
	private double expressionValue;
	
	public Gene(double value, double expressionValue) {
		this.expressionValue = expressionValue;
		this.value = value;
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
	
	public Gene clone() {
		return new Gene(new Double(value), new Double(expressionValue));
	}

	
}
