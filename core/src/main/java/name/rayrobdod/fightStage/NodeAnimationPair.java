package name.rayrobdod.fightStage;

import javafx.animation.Animation;
import javafx.scene.Node;

/**
 * A pair with one Node and one Animation
 */
public final class NodeAnimationPair {
	public final Node node;
	public final Animation animation;
	
	public NodeAnimationPair(
		  Node node
		, Animation animation
	) {
		this.node = node;
		this.animation = animation;
	}
}
