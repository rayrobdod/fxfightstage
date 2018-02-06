# fx_fightStage

[![Build Status](https://travis-ci.org/rayrobdod/fxfightstage.svg?branch=master)](https://travis-ci.org/rayrobdod/fxfightstage)
[![Build status](https://ci.appveyor.com/api/projects/status/1x30vq22tqeo7l4k/branch/master?svg=true)](https://ci.appveyor.com/project/rayrobdod/fxfightstage/branch/master)

This creates an animation of two units attacking each other. In javafx and with
heavy use of `javafx.animation.Animation`s.

The `core` subproject contains the things needed by a dependent project. The
entry point of the library is `name.rayrobdod.fightStage.BattleAnimation::buildAnimation`;
all that method's inputs are its parameters, and it returns a Node and Animation
which can be added to a Scene and played, respectively. The primary extension
point is implementing `UnitAnimationGroup` and `SpellAnimationGroup` and
providing those custom implementations in `buildAnimation`'s parameters.

The `demo` subproject contains an application that allows a user to set
parameters to a BattleAnimation, then play that animation. The main class for
this application is `name.rayrobdod.fightStage.previewer.Main`, and it uses two
Service Providers to find implementations of `UnitAnimationGroup` and
`SpellAnimationGroup`, so one could use this with a different classpath to view
custom animation groups.

The `samples` subproject contains a few implementations of `UnitAnimationGroup`
and `SpellAnimationGroup`


I imagine the primary use would be to be played during an attack in a turn-based tactics game.
