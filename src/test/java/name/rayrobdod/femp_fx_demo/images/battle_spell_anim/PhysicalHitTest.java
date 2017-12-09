package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroupTest;

public class PhysicalHitTest implements SpellAnimationGroupTest {
	
	public SpellAnimationGroup getInstance() {
		return new PhysicalHit();
	}
}
