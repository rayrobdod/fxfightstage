package name.rayrobdod.fightStage.spellAnimationGroup;

import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.SpellAnimationGroupTest;

public class FireballTest implements SpellAnimationGroupTest {
	public SpellAnimationGroup getInstance() {
		return new Fireball();
	}
}
