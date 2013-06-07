package fr.ornicare.draw;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingLeaf;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PickInfo;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.Timer;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.SimpleUniverse;

import fr.ornicare.entity.Entity;
import fr.ornicare.global.Core;
import fr.ornicare.global.GlobalVars;
import fr.ornicare.manager.Launch;
import fr.ornicare.util.Location;

public class DrawNavSecond extends Frame implements ActionListener,
		MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Color3f black;
	private static Color3f blue;
	
	private BranchGroup group;
	private TransformGroup boxTransformGroup;
	private Core core;
	private float factor = 1;
	private Timer timer;
	@SuppressWarnings("unused")
	private Launch launcher;

	protected Canvas3D myCanvas3D;
	protected Locale myLocale;

	protected Button exitButton;
	protected Button nExitButton;
	protected Button killAll;
	protected Button showButton;

	// private JPanel canvasContainer;

	private Transform3D transform;
	private TransformGroup tg;
	private VirtualUniverse myUniverse;
	private View myView;
	private PhysicalBody myBody;
	private Transform3D viewXfm;
	private TransformGroup viewXfmGroup;
	private BoundingSphere movingBounds;
	private ViewPlatform myViewPlatform;
	private PhysicalEnvironment myEnvironment;
	private KeyNavigatorBehavior keyNav;
	private BoundingLeaf boundLeaf;
	private BranchGroup viewBranch;
	private PickCanvas pickCanvas;
	private MouseRotate behavior;
	
	static {
		black = new Color3f(0.0f, 0.0f, 0.0f);
		blue = new Color3f(0.0f, 0.0f, 1.0f);
	}

	public DrawNavSecond(Core core, Launch launcher) {
		this.launcher = launcher;
		this.core = core;
		
		defineBehavior();
		initializeViewAndUniverse();
		refresh();
		createWindow();
		
		timer = new Timer(100, this);
		if (GlobalVars.timer) timer.start();

	}
	
	private void initializeViewAndUniverse() {
		transform = new Transform3D();


		viewXfm = new Transform3D();
		viewXfm.set(new Vector3f(0.0f, 0.0f, 20.0f));

		myViewPlatform = new ViewPlatform();

		movingBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		myEnvironment = new PhysicalEnvironment();

		viewXfmGroup = new TransformGroup(viewXfm);
		viewXfmGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		viewXfmGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		boundLeaf = new BoundingLeaf(movingBounds);
		viewXfmGroup.addChild(myViewPlatform);
		viewXfmGroup.addChild(boundLeaf);

		keyNav = new KeyNavigatorBehavior(viewXfmGroup);
		keyNav.setSchedulingBounds(movingBounds);

		myView = new View();
		myView.setFieldOfView(1);
		myView.setFrontClipPolicy(View.VISIBILITY_DRAW_ALL);
		myView.setBackClipDistance(1000);
		myView.setSceneAntialiasingEnable(false);

		myBody = new PhysicalBody();
		myUniverse = new VirtualUniverse();
		myLocale = new Locale(myUniverse);
	}

	private void defineBehavior() {
		//Define boundaries
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				10000.0);
		
		//Create a new behavior
		behavior = new MouseRotate();
		behavior.setSchedulingBounds(bounds);
	}

	private void createWindow() {

		setTitle("SimpleKeyNav");
		setSize(400, 400);
		setLayout(new BorderLayout());
		Panel bottom = new Panel();
		
		killAll = new Button("Kill all");
		exitButton = new Button("Exit");
		nExitButton = new Button("Switch timer");
		showButton = new Button("Show");
		bottom.add(killAll);
		bottom.add(exitButton);
		bottom.add(nExitButton);
		bottom.add(showButton);
		killAll.addActionListener(this);
		exitButton.addActionListener(this);
		nExitButton.addActionListener(this);
		showButton.addActionListener(this);
		
		add(BorderLayout.CENTER, myCanvas3D);
		add(BorderLayout.SOUTH, bottom);

		setVisible(true);
	}

	private void refresh() {
		myCanvas3D = new Canvas3D(
				SimpleUniverse.getPreferredConfiguration());

		group = new BranchGroup();
		
		myUniverse.removeLocale(myLocale);
		myLocale = new Locale(myUniverse);

		getScene();
		myLocale.addBranchGraph(buildViewBranch(myCanvas3D));
		myLocale.addBranchGraph(group);

		pickCanvas = new PickCanvas(myCanvas3D, group);
		pickCanvas.setMode(PickCanvas.TYPE_SHAPE3D);
		myCanvas3D.addMouseListener(this);
	}

	protected BranchGroup buildViewBranch(Canvas3D c) {
		if (viewBranch != null)
			viewBranch.removeAllChildren();

		viewBranch = new BranchGroup();

		viewBranch.addChild(viewXfmGroup);

		myView.addCanvas3D(c);
		myView.attachViewPlatform(myViewPlatform);
		myView.setPhysicalBody(myBody);
		myView.setPhysicalEnvironment(myEnvironment);

		viewBranch.addChild(keyNav);

		return viewBranch;
	}

	public void getScene() {
		//Add some light to the scene
		addLights(group);

		if(boxTransformGroup!=null) boxTransformGroup.removeAllChildren();

		
		//Allow transformation on the fly
		boxTransformGroup = new TransformGroup();
		boxTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		boxTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		//Apply the behavior
		boxTransformGroup.addChild(behavior);
		behavior.setTransformGroup(boxTransformGroup);

		
		//Draw the objects
		for (Entity ent : core.getEntityList()) {
			Location loc = ent.getLocation();
			Color3f col = new Color3f((float) ent.getSize(),
					(float) (1 - ent.getSize()), 0f);
			Vector3f vector = new Vector3f((float) loc.getX() / factor,
					(float) loc.getY() / factor, (float) loc.getZ() / factor);
			transform.setTranslation(vector);

			
			drawParticles(col, ent);
			if(GlobalVars.showLocal) drawLocal(col, ent);
			drawLinks(loc, ent);
		}
		

		group.addChild(boxTransformGroup);

	}

	private void drawLinks(Location loc, Entity ent) {
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(blue,
				ColoringAttributes.SHADE_FLAT);
		app.setColoringAttributes(ca);

		for (Entity entN : ent.getLocalEnvironment()) {
			Point3f[] plaPts = new Point3f[2];
			plaPts[0] = new Point3f((float) loc.getX() / factor,
					(float) loc.getY() / factor, (float) loc.getZ()
							/ factor);

			Location locN = entN.getLocation().clone();
			plaPts[1] = new Point3f((float) locN.getX() / factor,
					(float) locN.getY() / factor, (float) locN.getZ()
							/ factor);
			LineArray pla = new LineArray(20, LineArray.COORDINATES);
			pla.setCoordinates(0, plaPts);
			Shape3D plShape = new Shape3D(pla, app);

			boxTransformGroup.addChild(plShape);
		}
	}

	private void drawParticles(Color3f col, Entity ent) {
		Appearance ap = new Appearance();
		ap.setMaterial(new Material(col, black, col, black, 1.0f));
		
		Sphere sphere = new Sphere(
				(float) (ent.getSize() / factor * GlobalVars.particleFactor));
		sphere.setName("qsdqdsds");
		sphere.setAppearance(ap);

		tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.setTransform(transform);

		tg.addChild(sphere);

		boxTransformGroup.addChild(tg);
	}

	private void drawLocal(Color3f col, Entity ent) {
		Sphere sphereInfluence = new Sphere((float) ((new Double(ent.getSize()+ent.getInteractionRange().getDominantGene().getValue())) / factor * GlobalVars.localFactor));
		
		Appearance ap2 = new Appearance();
		
		switch (GlobalVars.localMode) {
		case 0:
			ap2.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_LINE,PolygonAttributes.CULL_BACK,0.0f));
			break;
		case 1:
			ap2.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST,GlobalVars.localTransparency));
			break;
		case 2:
			ap2.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_LINE,PolygonAttributes.CULL_BACK,0.0f));
			ap2.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST,GlobalVars.localTransparency));
			break;
		default:
			break;
		}
		
		
		ap2.setMaterial(new Material(col, black, col, black, 1.0f));
		sphereInfluence.setAppearance(ap2);

		tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	
		tg.setTransform(transform);

		tg.addChild(sphereInfluence);

		boxTransformGroup.addChild(tg);
	}

	public static void addLights(BranchGroup group) {
		Color3f light1Color = new Color3f(0.7f, 0.8f, 0.8f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color,
				light1Direction);
		light1.setInfluencingBounds(bounds);
		group.addChild(light1);
		AmbientLight light2 = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
		light2.setInfluencingBounds(bounds);
		group.addChild(light2);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		pickCanvas.setShapeLocation(e);
		PickInfo result = pickCanvas.pickClosest();

		if (result == null) {
			System.out.println("Nothing picked");
		} else {
			Shape3D s = (Shape3D) result.getNode();
			if (s != null) {
				System.out.println(s.getClass().getName());
				System.out.println(s.getName());

				Node node = s.getParent();
				int attempts = 10;
				String st = null;
				while (node != null && attempts > 0 && st == null) {
					st = node.getName();
					node = node.getParent();
					attempts--;
				}
				System.out.println(st + " - " + attempts);
			} else {
				System.out.println("null");
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent event) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(showButton)) {
			GlobalVars.show = !GlobalVars.show;
			if(GlobalVars.show) refresh();
		}
		if (e.getSource().equals(killAll)) {
			List<Entity> temp = core.getEntityList();
			for(Entity ent : temp) {
				ent.setDead();
			}
		}
		if (e.getSource().equals(exitButton) || core.getPopulation() == 0) {
			dispose();
			core.closeOut();
			System.exit(0);
		}
		if (e.getSource().equals(nExitButton)) {
			if (timer.isRunning()) {
				timer.stop();
			} else {
				timer.start();
			}
		}
		if (e.getSource().equals(timer)) {
			timer.stop();
			
			core.nextIter();

			if(GlobalVars.show) refresh();
			
			timer.start();
		}
	}



}