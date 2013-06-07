package fr.ornicare.entity;

import java.util.List;

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
	private Allele birthChance;
	private Allele deathChance;
	private Allele interactionRange;
	
	private double growFactor;

	
	/**
	 * Generation constructor
	 * @param map
	 */
	public Entity(DMap map) {
		GlobalVars.aliveEntities+=1;
		GlobalVars.entitiesCreated+=1;
		
		//Non-Hereditary variables
		delete = false;
		this.map = map;
		size = Math.max(GlobalVars.minSize,Math.random());
		location = map.getRandomLoc(size);
		growFactor = 1;
		
		//Hereditary variables
		growSpeed = new Allele(new Gene(GlobalVars.minSize * Math.random(), Math.random()), new Gene(GlobalVars.minSize * Math.random(), Math.random()));
		birthChance = new Allele(new Gene(Math.random()*GlobalVars.initialBirthChance, Math.random()), new Gene(Math.random()*GlobalVars.initialBirthChance, Math.random()));
		deathChance = new Allele(new Gene(Math.random()*GlobalVars.initialDeathChance, Math.random()), new Gene(Math.random()*GlobalVars.initialDeathChance, Math.random()));
		interactionRange = new Allele(new Gene(Math.random(), Math.random()), new Gene(Math.random(), Math.random()));
	
		//If the entity doesn't have the place to be created, schedule it deletion
		if(location == null) {
			this.delete = true;
		}
		else {
			GlobalVars.println("I am entity number "+this);
		}
	}
	
	/**
	 * Reproduction constructor
	 * @param map
	 * @param ent
	 * @param entN
	 */
	public Entity(DMap map, Entity ent, Entity entN) {
		GlobalVars.aliveEntities+=1;
		GlobalVars.entitiesCreated+=1;
		
		//Non-Hereditary variables
		delete = false;
		this.map = map;
		this.size = MathHelper.random(ent.getSize(), entN.getSize());
		growFactor = 1;
		
		Location middle = ent.getLocation().middle(entN.getLocation());
		double range = ent.getLocation().distanceTo(entN.getLocation());
		location = map.getRandomLoc(size,range,middle);
		
		//Hereditary variables
		this.deathChance = new Allele(ent.getDeathChance().getRandomGene(), entN.getDeathChance().getRandomGene());
		this.birthChance = new Allele(ent.getBirthChance().getRandomGene(), entN.getBirthChance().getRandomGene());
		this.growSpeed = new Allele(ent.getGrowSpeed().getRandomGene(), entN.getGrowSpeed().getRandomGene());
		this.interactionRange = new Allele(ent.getInteractionRange().getRandomGene(), entN.getInteractionRange().getRandomGene());
		
		
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

				GlobalVars.println("I am entity number "+this);
				ent.setSize(entSize);
				entN.setSize(entNSize);
			}
		}
	}
	
	/**
	 * Copy contructor
	 * @param ent
	 */
	private Entity(Entity ent) {
		GlobalVars.aliveEntities+=1;
		GlobalVars.entitiesCreated+=1;
		
		this.map = ent.getMap();
		this.location = ent.getLocation().clone();
		this.size = new Double(ent.getSize());
		this.growSpeed = ent.getGrowSpeed().clone();
		this.birthChance = ent.getBirthChance().clone();
		this.deathChance = ent.getDeathChance().clone();
		this.interactionRange = ent.getInteractionRange().clone();
		this.growFactor = new Double(ent.getGrowFactor());
		delete = new Boolean(ent.deletion());
	}
	
	/**
	 * Debug constructor
	 * @param map2
	 * @param b
	 */
	public Entity(DMap map, boolean b) {
		GlobalVars.aliveEntities+=1;
		GlobalVars.entitiesCreated+=1;
		
		//Non-Hereditary variables
//		delete = false;
//		//this.map = map;
//		size = Math.max(GlobalVars.minSize,Math.random());
		location = new Location(0, 0, 0);
//		growFactor = 1;
		
		//Hereditary variables
//		growSpeed = new Allele(new Gene(GlobalVars.minSize * Math.random(), Math.random()), new Gene(GlobalVars.minSize * Math.random(), Math.random()));
//		birthChance = new Allele(new Gene(Math.random()*GlobalVars.initialBirthChance, Math.random()), new Gene(Math.random()*GlobalVars.initialBirthChance, Math.random()));
//		deathChance = new Allele(new Gene(Math.random()*GlobalVars.initialDeathChance, Math.random()), new Gene(Math.random()*GlobalVars.initialDeathChance, Math.random()));
//		interactionRange = new Allele(new Gene(Math.random(), Math.random()), new Gene(Math.random(), Math.random()));
	}

	public void finalize() {
		GlobalVars.aliveEntities-=1;
		GlobalVars.entitieDeleted+=1;
	}

	public Allele getInteractionRange() {
		return interactionRange;
	}

	public void setSize(double size) {
		this.size = size;
	}


	public Allele getBirthChance() {
		return birthChance;
	}

	public Allele getDeathChance() {
		return deathChance;
	}



	private double getGrowFactor() {
		return growFactor;
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
	@Deprecated
	public Entity clone() {
		return new Entity(this);
	}
	
	public boolean equals(Entity ent) {
		return location.equals(ent.getLocation());
	}
	
	public Entity nextIter() {
		mutate();
		Entity son = reproduce();
		grow();
		die();
		return son;
	}
	
	private void mutate() {
		this.deathChance.mutate();
		this.birthChance.mutate();
		this.growSpeed.mutate();
		this.interactionRange.mutate();
	}

	public void grow() {
		double newSize = size+growFactor*growSpeed.getDominantGene().getValue();
		if(map.locationIsAvailable(newSize, location, this)) {
			size = newSize;
			growFactor=Math.min(1, growFactor*2);
		}
		else {
			growFactor/=2;
		}
	}
	
	public void die() {
		if(Math.random()<deathChance.getDominantGene().getValue()) {
			growSpeed.getDominantGene().setValue(- Math.abs(growSpeed.getDominantGene().getValue()));
		}
		if(size<=0) delete = true;
	}
	
	public void setDead() {
		growSpeed.getDominantGene().setValue(- Math.abs(growSpeed.getDominantGene().getValue()));
	}
	
	public Entity reproduce() {
	List<Entity> nearestEntities = getLocalEnvironment();
		if(!nearestEntities.isEmpty()) {
			Entity entN = nearestEntities.get(0);
			if(Math.random()<birthChance.getDominantGene().getValue()) {
				Entity son = new Entity(map,this, entN);
				if(!son.deletion()) {
					return son;
				}
			}
		}
		return null;
	}

	/**
	 * Return all entities in range.
	 * @return
	 */
	public List<Entity> getLocalEnvironment() {
		//interactionRange start at the surface
		return map.getLocalEnvironment(this);
	}

}
