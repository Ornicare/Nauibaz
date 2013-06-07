package fr.ornicare.global;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.ornicare.entity.Entity;

public class Core {

	private DMap map;
	private List<Entity> entityList;
	private int generation;
	private BufferedWriter out;

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
		
		//out file
		FileWriter fstream = null;
		try {
			fstream = new FileWriter("out.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		out = new BufferedWriter(fstream);
	}

	
	/**
	 * Return a COPY of the current entityList
	 * @return
	 */
	public List<Entity> getEntityList() {
		return entityList;
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
		
		if(GlobalVars.debug) System.out.println("Generation "+generation+" - "+entityList.size());
		generation++;
	}

	private void reproduce() {

//		List<Entity> test = new ArrayList<Entity>();
//		for(Entity ent : entityList) {
//			test.add(ent);
//		}

		
		int births = 0;
		List<Entity> birthEnt = new ArrayList<Entity>();
		
		for(Entity ent : new ArrayList<Entity>(entityList)) {

//			System.out.println(entityList.size());
			//int index = test.indexOf(ent);
			Entity son = ent.nextIter();
			if(son!=null) {
				birthEnt.add(son);
				births++;
			}
			//entityList.set(index, ent.clone());
		}
		
		entityList.addAll(birthEnt);
		

		
		//test.removeAll(test);
		
		int deaths = entityList.size();
		
		List<Entity> deletion = new ArrayList<Entity>();
		for(Entity ent : entityList) {
			if(!ent.deletion()) {
				deletion.add(ent);
				deaths--;
			}
		}
		
		entityList.removeAll(entityList);
		entityList = new ArrayList<Entity>(deletion);
		
		System.out.println(generation+","+entityList.size()+"/"+GlobalVars.aliveEntities+","+births+","+deaths+" - "+GlobalVars.entitiesCreated+"/"+((double)GlobalVars.entitieDeleted)+","+GlobalVars.entitiesCreated/((double)GlobalVars.entitieDeleted)+" - "+entityList.size()/((float)GlobalVars.aliveEntities)+" - "+(GlobalVars.entitiesCreated-((double)GlobalVars.entitieDeleted)));
//		System.out.println(generation+"/"+GlobalVars.aliveEntities+" - "+GlobalVars.entitiesCreated+"/"+((double)GlobalVars.entitieDeleted)+","+GlobalVars.entitiesCreated/((double)GlobalVars.entitieDeleted)+" - ");
		try {
			out.write(entityList.size()+","+births+","+deaths+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void closeOut() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getGeneration() {
		return generation;
	}

}
