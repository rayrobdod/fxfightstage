package name.rayrobdod.fightStage.spellAnimationGroup;

import javafx.scene.paint.Color;

import name.rayrobdod.fightStage.SpellAnimationGroup;
import name.rayrobdod.fightStage.SpellAnimationGroupTest;

public class LazorTest implements SpellAnimationGroupTest {
	public SpellAnimationGroup getInstance() {
		return new Lazor(Color.LIME);
	}
}
