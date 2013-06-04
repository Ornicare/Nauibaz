package fr.ornicare.global;

import java.util.ArrayList;
import java.util.List;

import fr.ornicare.entity.Entity;

public class Core {

	private DMap map;
	private List<Entity> entityList;
	private int generation;

	public void initialize(int pop) {
		int percent = -1;
		
		map = new DMap(this,10,Math.pow(pop,1/3.));
		entityList = new ArrayList<Entity>();
		for(int i = 0;i < pop;i++) {
			//percentage management
			int newPer = (100*i)/pop;
			if(GlobalVars.debug && newPer>percent) {
				percent = newPer;
				System.out.println(percent);
			}
			
			Entity ent = new Entity(map);
			if(!ent.deletion()) entityList.add(ent);
		}
	}

	public List<Entity> getEntityList() {

		List<Entity> test = new ArrayList<Entity>();
		for(Entity ent : entityList) {
			test.add(ent.clone());
		}
		return test;
	}
	
	public int getPopulation() {
		return entityList.size();
	}

	public DMap getMap() {
		return map;
	}

	public void nextIter() {
		
		//Temporary example
		reproduce();
		
		System.out.println("Generation "+generation+" - "+entityList.size());
		generation++;
	}

	private void reproduce() {

		List<Entity> test = new ArrayList<Entity>();
		for(Entity ent : entityList) {
			test.add(ent.clone());
		}
		
		int births = 0;
		
		for(Entity ent : test) {
			List<Entity> nearestEntities = getMap().getNearestEntities(
					ent, -1);
			Entity entN = nearestEntities.get(0);
			if(Math.random()<ent.getBirthChance()) {
				Entity son = new Entity(map,ent, entN);
				if(!son.deletion()) {
					entityList.add(son);
					births++;
				}
			}

			
			//grow
			int index = test.indexOf(ent);
			ent.nextIter();
			entityList.set(index, ent);
		}
		
		int deaths = entityList.size();
		
		List<Entity> deletion = new ArrayList<Entity>();
		for(Entity ent : entityList) {
			if(!ent.deletion()) {
				deletion.add(ent);
				deaths--;
			}
		}
		
		entityList = deletion;
		
		System.out.println(births+" births - "+deaths+" deaths");
	}

	public int getGeneration() {
		return generation;
	}

}
