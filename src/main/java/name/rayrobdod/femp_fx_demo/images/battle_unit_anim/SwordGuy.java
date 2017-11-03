package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.animation.Animation;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.media.AudioClip;

public final class SwordGuy {
	
	private static final String filename = "/name/rayrobdod/femp_fx_demo/images/battle_unit_anim/swordguy.png";
	private static final Rectangle2D standingViewport = new Rectangle2D(0,0,150,150);
	private static final Rectangle2D[] attackViewports = {
		new Rectangle2D(150,0,150,150),
		new Rectangle2D(300,0,150,150),
		new Rectangle2D(450,0,150,150)
	};
	
	/*
	 * Sounds are played at the end of a frame, whereas one might expect them to
	 * be played at the beginning of a frame.
	 */
	private static final String[] soundEffectFilenames = {
		"name/rayrobdod/femp_fx_demo/sounds/swing.wav",
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
		final Duration fullTime = Duration.seconds(1);
		
		for (int i = 0; i < attackViewports.length; i++) {
			final Duration thisTime = fullTime.divide(attackViewports.length).multiply(i);
			
			retval.getKeyFrames().add(new KeyFrame(thisTime,
				soundEffectEventHandler(soundEffectFilenames[i]),
				new KeyValue(node.viewportProperty(), attackViewports[i], Interpolator.DISCRETE)
			));
		}
		retval.getKeyFrames().add(new KeyFrame(fullTime,
			new KeyValue(node.viewportProperty(), standingViewport, Interpolator.DISCRETE)
		));
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
