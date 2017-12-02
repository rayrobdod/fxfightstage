package name.rayrobdod.femp_fx_demo.images.battle_spell_anim;

import javafx.scene.paint.Color;

import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroup;
import name.rayrobdod.femp_fx_demo.images.SpellAnimationGroupTest;

public class LazorTest implements SpellAnimationGroupTest {
	
	public SpellAnimationGroup getInstance() {
		return new Lazor(Color.LIME);
	}
}
