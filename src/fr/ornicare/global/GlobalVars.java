package fr.ornicare.global;

public class GlobalVars {
	public static boolean debug = true;
	public static double minSize = 0.1;
	public static double particleFactor = 1;
	public static boolean timer = true;
	public static double mutationChance = 0.1;
	public static double mutationAmplitude = 0.1;

	public static void println(Object string) {
		if(debug) System.out.println(string);
	}
}
