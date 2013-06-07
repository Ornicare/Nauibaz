package fr.ornicare.util;

public class Location {
	private double x;
	private double y;
	private double z;
	
	public Location(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public double distanceTo(Location otherLoc) {
		double xDif = otherLoc.getX() - x;
		double yDif = otherLoc.getY() - y;
		double zDif = otherLoc.getZ() - z;
		return Math.sqrt(xDif*xDif+yDif*yDif+zDif*zDif);
	}
	
	public Location clone() {
		return new Location(new Double(x), new Double(y), new Double(z));
	}


	public Location middle(Location location) {
		return new Location((this.x+location.getX())/2, (this.y+location.getY())/2, (this.z+location.getZ())/2);
	}
	
	public boolean equals(Location loc) {
		return x==loc.getX() && y==loc.getY() && z==loc.getZ();
	}
}
