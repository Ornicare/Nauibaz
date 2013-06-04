package fr.ornicare.entity;

import fr.ornicare.global.DMap;
import fr.ornicare.global.GlobalVars;
import fr.ornicare.util.Allele;
import fr.ornicare.util.Gene;
import fr.ornicare.util.Location;
import fr.ornicare.util.MathHelper;

public class Entity implements Cloneable{
	
	private double size;
	private Location location;
	private DMap map;
	private boolean delete;
	private Allele growSpeed;
	private double birthChance;
	private double deathChance;

	
	/**
	 * Generation constructor
	 * @param map
	 */
	public Entity(DMap map) {
		delete = false;
		GlobalVars.println("I am entity "+this);
		this.size = Math.max(GlobalVars.minSize,Math.random());
		this.map = map;
		
		
		
		
		this.growSpeed = new Allele(new Gene(GlobalVars.minSize * Math.random(), Math.random()), new Gene(GlobalVars.minSize * Math.random(), Math.random()));
		location = map.getRandomLoc(size);
		birthChance = Math.random();
		deathChance = Math.random();
		
		if(location == null) this.delete = true;
	}
	
	/**
	 * Reproduction constructor
	 * @param map
	 * @param ent
	 * @param entN
	 */
	public Entity(DMap map, Entity ent, Entity entN) {
		delete = false;
		GlobalVars.println("I am entity "+this);
		this.size = MathHelper.random(ent.getSize(), entN.getSize());
		this.deathChance = MathHelper.random(ent.getDeathChance(), entN.getDeathChance());
		this.birthChance = MathHelper.random(ent.getBirthChance(), entN.getBirthChance());
		this.map = map;
		this.growSpeed = new Allele(ent.getGrowSpeed().getRandomGene(), entN.getGrowSpeed().getRandomGene());
		Location middle = ent.getLocation().middle(entN.getLocation());
		
		double range = ent.getLocation().distanceTo(entN.getLocation());
		
		location = map.getRandomLoc(size,range,middle);
		
		if(location == null) {
			this.delete = true;
		}
		else {
			double coef = ent.getSize()/(ent.getSize()+entN.getSize());
			double entSize = ent.getSize()-coef*size;
			double entNSize = entN.getSize()-(1-coef)*size;
			
			if(Math.min(entSize, entNSize)<GlobalVars.minSize) {
				this.delete = true;
			}
			else {
				ent.setSize(entSize);
				entN.setSize(entNSize);
			}
		}
	}

	public void setSize(double size) {
		this.size = size;
	}


	public double getBirthChance() {
		return birthChance;
	}

	public double getDeathChance() {
		return deathChance;
	}

	/**
	 * Copy contructor
	 * @param ent
	 */
	private Entity(Entity ent) {
		this.map = ent.getMap();
		this.location = ent.getLocation();
		this.size = ent.getSize();
		this.growSpeed = ent.getGrowSpeed();
		this.birthChance = ent.getBirthChance();
		this.deathChance = ent.getDeathChance();
		delete = false;
	}

	private Allele getGrowSpeed() {
		return growSpeed;
	}

	private DMap getMap() {
		return map;
	}

	public boolean deletion() {
		return delete;
	}

	public Location getLocation() {
		return location;
	}

	public double getSize() {
		return size;
	}
	
	@Override
	public Entity clone() {
		return new Entity(this);
	}
	
	public boolean equals(Entity ent) {
		return location.equals(ent.getLocation());
	}
	
	public void nextIter() {
		double newSize = size+growSpeed.getDominantGene().getValue();
		if(map.locationIsAvailable(newSize, location, this)) {
			size = newSize;
		}
		if(Math.random()<deathChance) {
			growSpeed.getDominantGene().setValue(- Math.abs(growSpeed.getDominantGene().getValue()));
		}
		if(size<=0) delete = true;
		
	}

}
