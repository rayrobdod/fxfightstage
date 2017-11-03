package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
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
	private static final Rectangle2D[] beforeSpellViewports = {
		standingViewport,
		new Rectangle2D(150,0,150,150),
		new Rectangle2D(300,0,150,150),
		new Rectangle2D(450,0,150,150)
	};
	private static final Rectangle2D[] afterSpellViewports = {
		new Rectangle2D(450,0,150,150),
		standingViewport
	};
	
	/*
	 * Sounds are played at the end of a frame, whereas one might expect them to
	 * be played at the beginning of a frame.
	 */
	private static final String[] beforeSpellSoundEffectFilenames = {
		null,
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
	
	public Point2D getFootPoint() { return new Point2D(120, 150); }
	
	/**
	 * Returns an animation to be used for an attack animation
	 */
	public Animation getAttackAnimation(Animation hitAnimation) {
		final Timeline beforeSpellAnimation = new Timeline();
		for (int i = 0; i < beforeSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i);
			beforeSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				soundEffectEventHandler(beforeSpellSoundEffectFilenames[i]),
				new KeyValue(node.viewportProperty(), beforeSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		final Timeline afterSpellAnimation = new Timeline();
		for (int i = 0; i < afterSpellViewports.length; i++) {
			final Duration thisTime = frameLength.multiply(i);
			afterSpellAnimation.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), afterSpellViewports[i], Interpolator.DISCRETE)
			));
		}
		
		return new SequentialTransition(
			beforeSpellAnimation,
			hitAnimation,
			afterSpellAnimation
		);
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
