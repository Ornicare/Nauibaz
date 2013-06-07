package fr.ornicare.global;

public class GlobalVars {
	public static int aliveEntities = 0;
	public static int entitiesCreated = 0;
	public static int entitieDeleted = 0;
	
	public static boolean debug = true;
	public static boolean timer = false;
	public static boolean show = false;
	
	public static double minSize = 0.1;
	public static double mutationChance = 0.1;
	public static double mutationAmplitude = 0.01;
	public static double initialDeathChance = 0.1;
	public static double initialBirthChance = 0.5;
	
	
	public static double particleFactor = 1;

	public static boolean showLocal = true;
	public static int localMode = 2; //0 = grid, 1 = transparent sphere, 2 = both
	public static double localFactor = 1;
	public static float localTransparency = 0.8f;
	
	
	public static void println(Object string) {
		if(debug) System.out.println(string);
	}
}
