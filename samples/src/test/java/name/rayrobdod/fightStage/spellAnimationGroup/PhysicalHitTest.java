package name.rayrobdod.fightStage.spellAnimationGroup;

import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.SpellAnimationGroupTest;

public class PhysicalHitTest implements SpellAnimationGroupTest {
	public SpellAnimationGroup getInstance() {
		return new PhysicalHit();
	}
}
