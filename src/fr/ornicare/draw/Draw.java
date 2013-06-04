package fr.ornicare.draw;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
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
import fr.ornicare.util.Location;

public class Draw extends Applet implements MouseListener, MouseMotionListener {
	 
	private static final long serialVersionUID = 1L;
	public MainFrame frame;
	public int imageHeight = 256;
	public int imageWidth = 256;
	private Canvas3D canvas;
	private SimpleUniverse universe;
	private BranchGroup group = new BranchGroup();
	private PickCanvas pickCanvas;
	private BufferedImage frontImage;
	private Shape3D frontShape;
	private Texture texture;
	private Appearance appearance;
	private TextureLoader loader;
	private int lastX=-1;
	private int lastY=-1;
	private int mouseButton = 0;
	private TransformGroup boxTransformGroup;
	private Core core;
	private float factor = 20;
	
	public Draw(Core core) {
		this.core = core;
	}

	
	/**
	 * Returns the point where a line crosses a plane  
	 */
	
	public void startDrawing() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		universe = new SimpleUniverse(canvas);
		add("Center", canvas);
		positionViewer(); //Voir le nom
		getScene();
		universe.addBranchGraph(group);
		pickCanvas = new PickCanvas(canvas, group);
		pickCanvas.setMode(PickInfo.PICK_BOUNDS);
		canvas.addMouseMotionListener(this);
		frame.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		frame.addMouseListener(this);
		
	}
	public void getScene() {
		addLights(group);
		MouseRotate behavior = new MouseRotate();
	    BoundingSphere bounds =
	        new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);	    
	    boxTransformGroup = new TransformGroup();
		boxTransformGroup
				.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		boxTransformGroup
				.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		behavior.setTransformGroup(boxTransformGroup);
		boxTransformGroup.addChild(behavior);

		
		behavior.setSchedulingBounds(bounds);	


		for(Entity ent : core.getEntityList()) {
			Sphere sphere = new Sphere((float) ent.getSize()/factor);
			
			//some color
			Appearance ap = new Appearance();
			Color3f col = new Color3f((float) ent.getSize(), (float)(1- ent.getSize()), 0f);
		    Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
			ap.setMaterial(new Material(col, black, col, black, 1.0f));
			sphere.setAppearance(ap); 
			
			TransformGroup tg = new TransformGroup();

			Transform3D transform = new Transform3D();
			
			Location loc = ent.getLocation();
			Vector3f vector = new Vector3f((float)loc.getX()/factor, (float)loc.getY()/factor, (float)loc.getZ()/factor);
			
			transform.setTranslation(vector);
			tg.setTransform(transform);
			
			tg.addChild(sphere);
			boxTransformGroup.addChild(tg);
			

			///////////////////////////////////////////////////////////Some tests
//		    Appearance app = new Appearance();
//		    ColoringAttributes ca = new ColoringAttributes(black,
//		    ColoringAttributes.SHADE_FLAT);
//		    app.setColoringAttributes(ca);

		    // Plain line
		    Point3f[] plaPts = new Point3f[2];
		    plaPts[0] = new Point3f((float)loc.getX()/factor, (float)loc.getY()/factor, (float)loc.getZ()/factor);
		    
			List<Entity> nearestEntities = core.getMap().getNearestEntities(ent, 2*core.getMap().getMaxRadius());
		    
			Entity entN = nearestEntities.get(0);
			Location locN = entN.getLocation();
		    plaPts[1] = new Point3f((float)locN.getX()/factor, (float)locN.getY()/factor, (float)locN.getZ()/factor);
		    LineArray pla = new LineArray(2, LineArray.COORDINATES);
		    pla.setCoordinates(0, plaPts);
		    Shape3D plShape = new Shape3D(pla, ap);
		    boxTransformGroup.addChild(plShape);
		}
		
		group.addChild(boxTransformGroup);
		
	}
	
	public void positionViewer() {
		ViewingPlatform vp = universe.getViewingPlatform();
		TransformGroup tg1 = vp.getViewPlatformTransform();
		Transform3D t3d = new Transform3D();
		tg1.getTransform(t3d);
		vp.setNominalViewingTransform();

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
	public void mouseClicked(MouseEvent arg0) {
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
}