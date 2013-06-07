package fr.ornicare.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.ornicare.entity.Entity;
import fr.ornicare.util.Location;

public class DMap {

	private int attemptForLocation;
	private double startSize;
	private Core core;

	public DMap(Core core, int attemptForLocation, double startSize) {
		this.core = core;
		this.attemptForLocation = attemptForLocation;
		this.startSize = startSize;
	}

	public Location getRandomLoc(double size) {
		int remainingAttempts = attemptForLocation;
		Location loc = null;
		while(remainingAttempts > 0 && loc == null) {
			GlobalVars.println("	Attemp "+(attemptForLocation-remainingAttempts+1));
			
			double r = Math.random()*startSize;
			double theta = Math.random()*Math.PI;
			double phi = Math.random()*2*Math.PI;
			
			double x = r*Math.sin(theta)*Math.cos(phi);
			double y = r*Math.sin(theta)*Math.sin(phi);
			double z = r*Math.cos(theta);
			
			Location attLoc = new Location(x,y,z);
			if(locationIsAvailable(size,attLoc)) loc = attLoc;
			remainingAttempts--;
		}
		return loc;
	}

	public boolean locationIsAvailable(double size,Location newLoc) {
		for(Entity ent : core.getEntityList()) {
			//In case of colliding return false
			if(ent.getLocation().distanceTo(newLoc) <= (ent.getSize()+ size)) return false;
		}
		return true;
	}
	
	public boolean locationIsAvailable(double newSize, Location location, Entity entity) {
		for(Entity ent : core.getEntityList()) {
			//In case of colliding return false
			if(!ent.equals(entity) && ent.getLocation().distanceTo(location) <= (ent.getSize()+ newSize)) return false;
		}
		return true;
	}
	
	public List<Entity> getNearestEntities(final Entity ent, double radius) {	
		List<Entity> nearestEntities = new ArrayList<Entity>();
		List<Entity> entList = core.getEntityList();
		for(Entity entN : entList) {
			if(!entN.equals(ent) && entN.getLocation().distanceTo(ent.getLocation())<radius) {
				nearestEntities.add(entN);
			}
			else if(!entN.equals(ent) && radius==-1) nearestEntities.add(entN);
		}
		
	    Collections.sort(nearestEntities, new Comparator<Entity>() {

	        public int compare(Entity o1, Entity o2) {
	            if(((o1.getLocation().distanceTo(ent.getLocation()) - o1.getSize()) - (o2.getLocation().distanceTo(ent.getLocation()) - o2.getSize()))>0) return 1;
	            return -1;
	        }
	    });
	    
//	    for(Entity ent2 : nearestEntities) {
//	    	System.out.println(ent2.getLocation().distanceTo(ent.getLocation()));
//	    }
	    
	    return nearestEntities;
		
	}

	public double getMaxRadius() {
		double maxR = 0;
		for(Entity ent : core.getEntityList()) {
			double entD = ent.getLocation().distanceTo(new Location(0, 0, 0));
			if(entD>maxR) {
				maxR=entD;
			}
		}
		return maxR;
	}

	public Location getRandomLoc(double size, double range, Location middle) {
		int remainingAttempts = attemptForLocation;
		Location loc = null;
		while(remainingAttempts > 0 && loc == null) {
			GlobalVars.println("	Attemp "+(attemptForLocation-remainingAttempts+1));
			
			double r = Math.random()*range;
			double theta = Math.random()*Math.PI;
			double phi = Math.random()*2*Math.PI;
			
			double x = r*Math.sin(theta)*Math.cos(phi)+middle.getX();
			double y = r*Math.sin(theta)*Math.sin(phi)+middle.getY();
			double z = r*Math.cos(theta)+middle.getZ();
			
			Location attLoc = new Location(x,y,z);
			if(locationIsAvailable(size,attLoc)) loc = attLoc;
			remainingAttempts--;
		}
		return loc;
	}

	public List<Entity> getLocalEnvironment(final Entity ent) {
		List<Entity> nearestEntities = new ArrayList<Entity>();
		List<Entity> entList = core.getEntityList();
		for(Entity entN : entList) {
			if(!entN.equals(ent) && ((entN.getLocation().distanceTo(ent.getLocation())-entN.getInteractionRange().getDominantGene().getValue()-ent.getInteractionRange().getDominantGene().getValue()-ent.getSize()-entN.getSize())<0)) {
				nearestEntities.add(entN);
			}
		}
		
		//entList.removeAll(entList);
		
	    return nearestEntities;
	}


}
