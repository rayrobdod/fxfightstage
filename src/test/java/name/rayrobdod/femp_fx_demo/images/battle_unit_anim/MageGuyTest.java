package name.rayrobdod.femp_fx_demo.images.battle_unit_anim;

import org.junit.jupiter.api.Disabled;

import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.UnitAnimationGroupTest;

@Disabled("Image without JavaFX initialization")
public class MageGuyTest implements UnitAnimationGroupTest {
	
	public UnitAnimationGroup getInstance() {
		return new MageGuy();
	}
}
