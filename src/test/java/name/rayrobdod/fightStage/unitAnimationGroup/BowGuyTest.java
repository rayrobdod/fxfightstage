package name.rayrobdod.fightStage.unitAnimationGroup;

import org.junit.jupiter.api.Disabled;

import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.UnitAnimationGroupTest;

@Disabled("Image without JavaFX initialization")
public class BowGuyTest implements UnitAnimationGroupTest {
	public UnitAnimationGroup getInstance() {
		return new BowGuy();
	}
}
