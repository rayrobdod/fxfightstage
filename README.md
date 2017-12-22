# fx_fightStage

This creates an animation of two units attacking each other. In javafx and with
heavy use of `javafx.animation.Animation`s.

The entry point is `name.rayrobdod.fightStage.BattleAnimation::buildAnimation`.

The primary extension point is implementing `UnitAnimationGroup` and `SpellAnimationGroup`.

There is also a `name.rayrobdod.fightStage.previewer.Main` which can show the animation
in isolation.


I imagine the primary use would be to be played during an attack in a turn-based tactics game.
