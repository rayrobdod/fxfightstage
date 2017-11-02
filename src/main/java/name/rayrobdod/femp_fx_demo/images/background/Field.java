package name.rayrobdod.femp_fx_demo.images.background;

import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public final class Field {
	
	public static Node buildGroup(double width, double height) {
		Rectangle r1 = new Rectangle(0, 0, width, height);
		r1.setFill(Color.CYAN);
		Rectangle r2 = new Rectangle(0, height * 2 / 3, width, height / 3);
		r2.setFill(Color.LAWNGREEN);
		
		return new Group(r1, r2);
	}
}
