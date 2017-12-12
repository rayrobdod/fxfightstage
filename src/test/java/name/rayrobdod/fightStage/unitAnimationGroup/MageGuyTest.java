package name.rayrobdod.fightStage.unitAnimationGroup;

import org.junit.jupiter.api.Disabled;

import name.rayrobdod.fightStage.UnitAnimationGroup;
import name.rayrobdod.fightStage.UnitAnimationGroupTest;

@Disabled("Image without JavaFX initialization")
public class MageGuyTest implements UnitAnimationGroupTest {
	public UnitAnimationGroup getInstance() {
		return new MageGuy();
	}
}
