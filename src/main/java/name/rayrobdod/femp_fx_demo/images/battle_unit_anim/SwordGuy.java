package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.animation.Animation;
import javafx.animation.*;
import javafx.util.Duration;

public final class SwordGuy {
	
	private static final String filename = "/name/rayrobdod/femp_fx_demo/images/battle_unit_anim/swordguy.png";
	private static final Rectangle2D standingViewport = new Rectangle2D(0,0,150,150);
	private static final Rectangle2D[] attackViewports = {
		new Rectangle2D(150,0,150,150),
		new Rectangle2D(300,0,150,150),
		new Rectangle2D(450,0,150,150)
	};
	
	private final ImageView node;
	
	public SwordGuy() {
		final Image img = new Image(filename);
		this.node = new ImageView(img);
		this.node.setViewport(standingViewport);
	}
	
	public Node getNode() { return this.node; }
	
	public Animation getAttackAnimation() {
		final Timeline retval = new Timeline();
		final Duration fullTime = Duration.seconds(1);
		
		for (int i = 0; i < attackViewports.length; i++) {
			final Duration thisTime = fullTime.divide(attackViewports.length).multiply(i);
			
			retval.getKeyFrames().add(new KeyFrame(thisTime,
				new KeyValue(node.viewportProperty(), attackViewports[i], Interpolator.DISCRETE)
			));
		}
		retval.getKeyFrames().add(new KeyFrame(fullTime,
			new KeyValue(node.viewportProperty(), standingViewport, Interpolator.DISCRETE)
		));
		retval.setCycleCount(1);
		return retval;
	}
	
	
}
