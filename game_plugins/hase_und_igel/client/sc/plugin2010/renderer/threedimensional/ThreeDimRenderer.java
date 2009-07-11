/**
 * 
 */
package sc.plugin2010.renderer.threedimensional;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import sc.plugin2010.Board;
import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Player;
import sc.plugin2010.gui.GUIGameHandler;
import sc.plugin2010.renderer.Renderer;
import sc.plugin2010.util.GameUtil;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * @author ffi
 * 
 */
public class ThreeDimRenderer extends JPanel implements Renderer
{
	// GUI Components
	private final GUIGameHandler	handler;

	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;

	// only draw the board the first time it updates
	private boolean					boardWasCreated	= false;

	// Strings used for asking Questions to the user
	private String					moveForward		= "Weiter ziehen";
	private String					takeCarrots		= "10 Karotten nehmen";
	private String					dropCarrots		= "10 Karotten abgeben";
	private String					carrotAnswer	= "carrots";

	public ThreeDimRenderer(GUIGameHandler handler)
	{
		super();
		this.handler = handler;
		createInitFrame();
	}

	private void createInitFrame()
	{

		/* Layout des Panels festlegen */
		final BorderLayout bL = new BorderLayout();

		/* Erzeugung von Canvas3D */
		final GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		final Canvas3D canvas3D = new Canvas3D(config);

		/* Layout des Panels setzen */
		setLayout(bL);

		/* Canvas3D zu Frame hinzufuegen */
		this.add(canvas3D, bL.CENTER);

		/* Erzeugung eines SimpleUnviverse-Objektes */
		final SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

		/**
		 * Anpassung des SimpleUniverse-Objektes: getViewingPlatform gibt die
		 * ViewingPlatfrom zurueck. Auf diese wird die Methode setNominal
		 * ViewingTransform angewandt, welche den Augpunkt auf (0,0,2.41)
		 * festlegt. Man schaut in negativer z-Richtung.
		 **/
		final ViewingPlatform viewingPlatform = simpleU.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();

		/**
		 * OrbitBehavior hinzufuegen, so dass Rotation, Skalierung und
		 * Translation der verwendeten Objekte moeglich ist.
		 **/
		final OrbitBehavior orbit = new OrbitBehavior(canvas3D,
				OrbitBehavior.REVERSE_ALL);

		/* Boundes des OrbitBehaviors setzen */
		orbit.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
		/* OrbitBehavior zur ViewingPlatform hinzufuegen */
		viewingPlatform.setViewPlatformBehavior(orbit);
		/* Erzeugung des Content Branch-Graphen */
		final BranchGroup scene = createSceneGraph();

		/* Content Branch-Graphen kompilieren */
		scene.compile();

		/* Content Branch-Graph dem SimpleUniverse-Objekt hinzufuegen */
		simpleU.addBranchGraph(scene);
	}

	private BranchGroup createSceneGraph()
	{
		/* Wurzel des Content Branch-Graphen erzeugen */
		final BranchGroup objRoot = new BranchGroup();

		/* Text3D erzeugen */
		objRoot.addChild(createText3d());

		/* Sphere erzeugen */
		objRoot.addChild(createSphere());

		return objRoot;
	}

	/**
	 * Methode erzeugt einen 3D-Text, der die Willkommensnachricht anzeigt.
	 * Gleichzeitig wird fuer die Beleuchtung des Textes gesorgt. Rueckgabewert
	 * ist die fertige BranchGroup.
	 **/
	private BranchGroup createText3d()
	{
		final BranchGroup objText = new BranchGroup();

		/**
		 * TransformGruppe erzeugen, die spaeter den Text3D skaliert, so dass er
		 * vollstaendig in den Anzeigebereich passt.
		 **/
		final TransformGroup objScale = new TransformGroup();
		final Transform3D t3d = new Transform3D();
		/* Skalierungsmatrix setzen (abhaengig von der Laenge des Textes) */
		t3d.setScale(1.2);

		/* Bounds der Lichtquellen festlegen */
		final BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);

		/* rote Lichtquellen erzeugen */
		final DirectionalLight directionalLight1 = new DirectionalLight(
				new Color3f(1.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));
		directionalLight1.setInfluencingBounds(bounds);

		/* gruene Lichtquellen erzeugen */
		final DirectionalLight directionalLight2 = new DirectionalLight(
				new Color3f(0.0f, 1.0f, 0.0f),
				new Vector3f(-1.0f, -1.0f, -1.0f));
		directionalLight2.setInfluencingBounds(bounds);

		/* Alle Komponenten zu SceneGraph hinzufuegen */
		objScale.setTransform(t3d);
		objText.addChild(objScale);
		objText.addChild(directionalLight1);
		objText.addChild(directionalLight2);

		return objText;
	}

	/**
	 * Methode erzeugt eine Kugel und belegt sie mit einer Textur. Gleichzeitig
	 * wird ein Interpolator hinzugefuegt, der die Kugel dreht. Rueckgabewert
	 * ist die fertige BranchGroup.
	 **/
	private BranchGroup createSphere()
	{
		final BranchGroup objSphere = new BranchGroup();

		/* Standard Appearance-Objekt erzeugen */
		final Appearance sphereAppear = new Appearance();

		/**
		 * Vorbereitung der Textur (Bild) wird in externem Programm erledigt -
		 * hier wird nur die Datei spezifiziert
		 **/
		final String filename = "resource/background.png";

		/* Laden der Textur mit TextureLoader (Utility-Klasse) */
		final TextureLoader loader = new TextureLoader(filename, this);

		if (loader.getTexture() != null)
		{
			/* Laden der Textur - Textur in ImageComponent2D uebergeben */
			final ImageComponent2D image = loader.getImage();

			/* Textur-Objekt erzeugen */
			final Texture2D texture = new Texture2D(Texture.BASE_LEVEL,
					Texture.RGBA, image.getWidth(), image.getHeight());
			texture.setImage(0, image);

			/**
			 * Textur zu Appearance-Objekt hinzufuegen Instanz von Appearance
			 * wurde bereits erzeugt
			 **/
			sphereAppear.setTexture(texture);
		}
		else
		{
			/* Fehler beim Laden */
			JOptionPane.showMessageDialog(this, "Textur \"" + filename
					+ "\" konnte nicht geladen werden !", "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
		;

		/**
		 * Transformgruppe mit geeignetem Capability erzeugen, die spaeter die
		 * Kugel dreht
		 **/
		final TransformGroup objSpin = new TransformGroup();
		objSpin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		/**
		 * Alpha-Objekt erzeugen, dass sich unendlich oft dreht Eine Umdrehung
		 * in 10 Sekunden
		 **/
		final Alpha rotationAlpha = new Alpha(-1, 10000);

		/* Ziel-Objekt erzeugen */
		objSpin.addChild(new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS,
				sphereAppear));

		/**
		 * Interpolator fuer Drehung mit Referenz auf Ziel- und Alpha-Objekt
		 * erzeugen
		 **/
		final RotationInterpolator rotator = new RotationInterpolator(
				rotationAlpha, objSpin);

		/* Bounds fuer Interpolator setzen */
		rotator.setSchedulingBounds(new BoundingSphere());

		/* Komponenten zu SceneGraph hinzufuegen */
		objSpin.addChild(rotator);
		objSphere.addChild(objSpin);

		return objSphere;
	}

	@Override
	public void updateAction(final String doneAction)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoard(BoardUpdated bu)
	{
		board = bu.getBoard();
	}

	@Override
	public void updateChat(final String chatMsg)
	{
		//
	}

	@Override
	public void updatePlayer(final Player player, final boolean own)
	{
		if (own)
		{
			this.player = player;

			// setReachableFields(player.getPosition(), player
			// .getCarrotsAvailable());

			if (GameUtil.isValidToTakeCarrots(board, player))
			{
				List<String> answers = new LinkedList<String>();
				answers.add(moveForward);
				answers.add(takeCarrots);
				if (GameUtil.isValidToDropCarrots(board, player))
				{
					answers.add(dropCarrots);
				}
				// askQuestion("Was wollen Sie tun?", answers, "carrots");
			}
			else if (GameUtil.isValidToEat(board, player))
			{
				// TODO send move
			}
		}
		else
		{
			enemy = player;
		}
	}

	@Override
	public Image getImage()
	{
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}
}
