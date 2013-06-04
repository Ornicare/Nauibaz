package fr.ornicare.draw;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
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
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import fr.ornicare.entity.Entity;
import fr.ornicare.global.Core;
import fr.ornicare.global.GlobalVars;
import fr.ornicare.manager.Launch;
import fr.ornicare.util.Location;

public class DrawNav extends Frame implements ActionListener, MouseListener,
		MouseMotionListener {

	private BranchGroup group;
	private TransformGroup boxTransformGroup;
	private Core core;
	private float factor = 1;
//	private Timer timer;
	private Launch launcher;
	
	protected Canvas3D myCanvas3D;
	protected Locale myLocale;
	
	protected Button exitButton;
	protected Button nExitButton;
	
//	private JPanel canvasContainer;
	
	private Transform3D transform;
	private TransformGroup tg;
	private float xloc;
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


	public DrawNav(Core core, Launch launcher)
	{
		this.launcher = launcher;
		this.core = core;
		

		viewXfm = new Transform3D();
		viewXfm.set(new Vector3f(0.0f, 0.0f, 20.0f));

		
		movingBounds = new BoundingSphere(new Point3d(0.0, 0.0,
				0.0), 100.0);
		myEnvironment = new PhysicalEnvironment();
		


		myView = new View();
		myBody = new PhysicalBody();
		
		myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		
		group = new BranchGroup();
		exitButton = new Button("Exit");
		nExitButton = new Button("Next gen");
		
//		canvasContainer = new JPanel();
//		canvasContainer.setLayout(new BorderLayout());
//		canvasContainer.add(myCanvas3D, BorderLayout.CENTER);
		
		myUniverse = new VirtualUniverse();
		myLocale = new Locale(myUniverse);
		getScene();
		myLocale.addBranchGraph(buildViewBranch(myCanvas3D));
		myLocale.addBranchGraph(group);

		setTitle("SimpleKeyNav");
		setSize(400, 400);
		setLayout(new BorderLayout());
		Panel bottom = new Panel();
		bottom.add(exitButton);
		bottom.add(nExitButton);
//		add(BorderLayout.CENTER, canvasContainer);
		add(BorderLayout.CENTER, myCanvas3D);
		
		add(BorderLayout.SOUTH, bottom);
		exitButton.addActionListener(this);
		nExitButton.addActionListener(this);
		setVisible(true);
//		timer = new Timer(100, this);
//		timer.start();
		
		
		
		
		
	}

	public void startDrawing() {
		/*myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		group = new BranchGroup();
		exitButton = new Button("Exit");
		nExitButton = new Button("Next gen");
		
		myUniverse = new VirtualUniverse();
		Locale myLocale = new Locale(myUniverse);
		getScene();
		myLocale.addBranchGraph(buildViewBranch(myCanvas3D));
		myLocale.addBranchGraph(group);

		setTitle("SimpleKeyNav");
		setSize(400, 400);
		setLayout(new BorderLayout());
		Panel bottom = new Panel();
		bottom.add(exitButton);
		bottom.add(nExitButton);
		add(BorderLayout.CENTER, myCanvas3D);
		add(BorderLayout.SOUTH, bottom);
		exitButton.addActionListener(this);
		nExitButton.addActionListener(this);
		setVisible(true);
//		timer = new Timer(100, this);
//		timer.start();*/

	}

	protected BranchGroup buildViewBranch(Canvas3D c) {
		BranchGroup viewBranch = new BranchGroup();
		
		viewXfmGroup = new TransformGroup(viewXfm);
		viewXfmGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		viewXfmGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		myViewPlatform = new ViewPlatform();
		boundLeaf = new BoundingLeaf(movingBounds);
		
		viewXfmGroup.addChild(myViewPlatform);
		viewXfmGroup.addChild(boundLeaf);
		viewBranch.addChild(viewXfmGroup);
		

		keyNav = new KeyNavigatorBehavior(viewXfmGroup);
		keyNav.setSchedulingBounds(movingBounds);
		
		myView.addCanvas3D(c);
		myView.attachViewPlatform(myViewPlatform);
		myView.setPhysicalBody(myBody);
		myView.setPhysicalEnvironment(myEnvironment);

		viewBranch.addChild(keyNav);

		return viewBranch;
	}


	public void getScene() {
		addLights(group);
		MouseRotate behavior = new MouseRotate();
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		boxTransformGroup = new TransformGroup();
		boxTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		boxTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		behavior.setTransformGroup(boxTransformGroup);
		boxTransformGroup.addChild(behavior);

		behavior.setSchedulingBounds(bounds);

		transform = new Transform3D();

		for (Entity ent : core.getEntityList()) {
			Sphere sphere = new Sphere((float) (ent.getSize() / factor * GlobalVars.particleFactor));

			// some color
			Appearance ap = new Appearance();
			Color3f col = new Color3f((float) ent.getSize(),
					(float) (1 - ent.getSize()), 0f);
			Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
			ap.setMaterial(new Material(col, black, col, black, 1.0f));
			sphere.setAppearance(ap);

			tg = new TransformGroup();
			tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);


			Location loc = ent.getLocation();
			Vector3f vector = new Vector3f((float) loc.getX() / factor,
					(float) loc.getY() / factor, (float) loc.getZ() / factor);

			transform.setTranslation(vector);
			tg.setTransform(transform);

			tg.addChild(sphere);
			boxTransformGroup.addChild(tg);

			// /////////////////////////////////////////////////////////Some
			// tests
			 Appearance app = new Appearance();
			 ColoringAttributes ca = new ColoringAttributes(col,ColoringAttributes.SHADE_FLAT);
			 app.setColoringAttributes(ca);

			// Plain line
			Point3f[] plaPts = new Point3f[2];
			plaPts[0] = new Point3f((float) loc.getX() / factor,
					(float) loc.getY() / factor, (float) loc.getZ() / factor);

			List<Entity> nearestEntities = core.getMap().getNearestEntities(
					ent, 2 * core.getMap().getMaxRadius());

			Entity entN = nearestEntities.get(0);
			Location locN = entN.getLocation();
			plaPts[1] = new Point3f((float) locN.getX() / factor,
					(float) locN.getY() / factor, (float) locN.getZ() / factor);
			LineArray pla = new LineArray(20, LineArray.COORDINATES);
			pla.setCoordinates(0, plaPts);
			Shape3D plShape = new Shape3D(pla, app);
			boxTransformGroup.addChild(plShape);
		}

		group.addChild(boxTransformGroup);

	}

//	public void positionViewer() {
//		ViewingPlatform vp = ((SimpleUniverse) myUniverse).getViewingPlatform();
//		TransformGroup tg1 = vp.getViewPlatformTransform();
//		Transform3D t3d = new Transform3D();
//		tg1.getTransform(t3d);
//		vp.setNominalViewingTransform();
//
//	}

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
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("fdsfds");
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
		if(e.getSource().equals(nExitButton)) {
			//dispose();
			core.nextIter();
//			for(int i = 0;i<100;i++) core.nextIter();
			

			
			//---------------

//			myView = new View();

			//myBody = new PhysicalBody();
			
			//---------------
			
			
//			myUniverse = new VirtualUniverse();
//			myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
//			group = new BranchGroup();
//			Locale myLocale = new Locale(myUniverse);
//			getScene();
//			myLocale.addBranchGraph(buildViewBranch(myCanvas3D));
//			myLocale.addBranchGraph(group);
			
			myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
			group = new BranchGroup();
			//exitButton = new Button("Exit");
			//nExitButton = new Button("Next gen");
			
			//myUniverse = new VirtualUniverse();
			//myUniverse.
			myUniverse.removeLocale(myLocale);
			myLocale = new Locale(myUniverse);
			
			getScene();
			myLocale.addBranchGraph(buildViewBranch(myCanvas3D));
			myLocale.addBranchGraph(group);

//			setTitle("SimpleKeyNav");
//			setSize(400, 400);
//			setLayout(new BorderLayout());
			//this.removeAll();

			//Panel bottom = new Panel();
		
			//bottom.add(exitButton);
			//bottom.add(nExitButton);
			
			//add(BorderLayout.CENTER, myCanvas3D);
			//canvasContainer.removeAll();
			//canvasContainer.setLayout(new BorderLayout());
			//canvasContainer.add(myCanvas3D, BorderLayout.CENTER);

			
			//add(BorderLayout.SOUTH, bottom);
			//exitButton.addActionListener(this);
			//nExitButton.addActionListener(this);
			

			viewXfm.set(new Vector3f(0.0f, 0.0f, 20.0f));
			
			
			
			setVisible(true);
		}
		if(e.getSource().equals(exitButton)) {
			dispose();
			System.exit(0);
		}
//		xloc++;
//		System.out.println("ezdq");
//		transform.setScale(new Vector3d(1.0, .8, 1.0));
//		transform.setTranslation(new Vector3f(xloc, 10f, 0.0f));
//		tg.setTransform(transform);
	}
}