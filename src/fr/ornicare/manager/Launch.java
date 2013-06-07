package fr.ornicare.manager;

import fr.ornicare.draw.DrawNavSecond;
import fr.ornicare.global.Core;
import fr.ornicare.global.GlobalVars;

public class Launch {

	private Core core;

	public Launch(int pop) {
		GlobalVars.debug = false;
		core = new Core();
		core.initialize(pop);
		
		//draw it
//		Draw object = new Draw(core);		 
//		object.frame = new MainFrame(object, object.imageWidth, object.imageHeight);
//		object.startDrawing();
//		object.validate();
		
		
		//Some tests
//		Entity ent = core.getEntityList().get(0);
//		List<Entity> nearestEntities = core.getMap().getNearestEntities(ent, 2*core.getMap().getMaxRadius());
//		System.out.println(nearestEntities.get(0).equals(ent));
		//for(int i = 0;i<100;i++) core.nextIter();
		draw();

	}

//	public void nextIter() {
////		core.nextIter();
//		for(int i = 0;i<100;i++) core.nextIter();
//		draw();
//	}

	private void draw() {
		@SuppressWarnings("unused")
		DrawNavSecond dN = new DrawNavSecond(core, this);
	}

}
