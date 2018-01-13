package name.rayrobdod.fightStage;



import javafx.animation.Animation;
import javafx.scene.Node;

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
