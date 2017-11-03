package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public final class SwordGuy {
	
	private static final String filename = "/name/rayrobdod/femp_fx_demo/images/battle_unit_anim/swordguy.png";
	private static final Rectangle2D standingViewport = new Rectangle2D(0,0,150,150);
	private static final Duration frameLength = Duration.seconds(1.0 / 4.0);
	private static final Rectangle2D[] attackViewports = {
		standingViewport,
		new Rectangle2D(150,0,150,150),
		new Rectangle2D(300,0,150,150),
		new Rectangle2D(450,0,150,150),
		standingViewport
	};
	
	/*
	 * Sounds are played at the end of a frame, whereas one might expect them to
	 * be played at the beginning of a frame.
	 */
	private static final String[] soundEffectFilenames = {
		null,
		"name/rayrobdod/femp_fx_demo/sounds/swing.wav",
		null,
		null,
		null
	};
	
	
	private final ImageView node;
	
	public SwordGuy() {
		final Image img = new Image(filename);
		this.node = new ImageView(img);
		this.node.setViewport(standingViewport);
	}
	
	/**
	 * Returns the node associated with this object.
	 */
	public Node getNode() { return this.node; }
	
	/**
	 * Returns an animation to be used for an attack animation
	 */
	public Animation getAttackAnimation() {
		final Timeline retval = new Timeline();
		
		for (int i = 0; i < attackViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i);
			
			retval.getKeyFrames().add(new KeyFrame(thisTime,
				soundEffectEventHandler(soundEffectFilenames[i]),
				new KeyValue(node.viewportProperty(), attackViewports[i], Interpolator.DISCRETE)
			));
		}
		retval.setCycleCount(1);
		return retval;
	}
	
	/**
	 * Returns an EventHandler which plays the specified sound upon being invoked.
	 * @param filename the url of the sound file. Nullable.
	 * @return an event handler which plays the sound effect
	 */
	private static EventHandler<ActionEvent> soundEffectEventHandler(String filename) {
		if (null == filename) {
			return null;
		} else {
			final java.net.URL fileurl = SwordGuy.class.getClassLoader().getResource(filename);
			if (null == fileurl) {
				System.out.println("Resource not found: " + filename);
				return null;
			} else {
				final AudioClip clip = new AudioClip(fileurl.toString());
				final EventHandler<ActionEvent> handler = (x -> clip.play());
				return handler;
			}
		}
	}
	
}
