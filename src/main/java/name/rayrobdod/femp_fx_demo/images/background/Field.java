package name.rayrobdod.femp_fx_demo.images.background;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public final class Field {
	
	public static Node buildGroup(double width, double height) {
		Rectangle r1 = new Rectangle(0, 0, width, height);
		r1.setFill(Color.CYAN);
		Rectangle r2 = new Rectangle(0, height * 2 / 3, width, height / 3);
		r2.setFill(Color.LAWNGREEN);
		
		// marks the foot targets
		Circle cl = new Circle(width * 2 / 8, height * 7 / 8, 10);
		cl.setFill(Color.MAGENTA);
		Circle cr = new Circle(width * 6 / 8, height * 7 / 8, 10);
		cr.setFill(Color.MAGENTA);
		
		return new Group(r1, r2, cl, cr);
	}
}
