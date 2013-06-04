package fr.ornicare.util;

public class MathHelper {

	public static double random(double a, double b) {
		double ap = Math.min(a, b);
		double bp = Math.max(a, b);
		
		return (bp-ap)*Math.random()+ap;
	}
}
