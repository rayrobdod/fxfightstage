/*
 * Copyright 2018 Raymond Dodge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.rayrobdod.fightStage.spellAnimationGroup;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import name.rayrobdod.fightStage.BattlePanAnimations;
import name.rayrobdod.fightStage.ShakeAnimationBiFunction;
import name.rayrobdod.fightStage.SpellAnimationGroup;

/**
 */
public final class Atom implements SpellAnimationGroup {
	
	private static final Duration tickTime = Duration.seconds(1d / 60d);
	private static final Duration particleDuration = Duration.seconds(1d / 8d);
	private static final Duration animationDuration = Duration.seconds(3d);
	private static final Duration headFadeDuration = Duration.millis(800);
	
	private final static List<Vector3D> normals = Stream.of(
			new Vector3D(1, -2, 0.1),
			new Vector3D(1, 2, 0.1),
			new Vector3D(2, -1, 0.1),
			new Vector3D(2, 1, 0.1)
		).collect(Collectors.toList());
	
	private static final double maxRadius = 80;
	private static final double dropDistance = 100;
	
	private static final int particlesPerTick = 2;
	private static final int particlePoolSize = particlesPerTick * (int)(
		Math.ceil(particleDuration.toMillis() / tickTime.toMillis())
	);
	
	private static final Color headColor = Color.rgb(0xFF, 0xFF, 0xDD);
	private static final float headSpeed = 10f;
	private static final Color particleColor = Color.rgb(0xFF, 0xFF, 0xDD, 0.8);
	
	
	private final List<RingOfParticles> rings;
	private final Group backLayer;
	private final Group frontLayer;
	
	public Atom() {
		this.rings = normals.stream()
				.map(x -> new RingOfParticles())
				.collect(Collectors.toList());
		this.backLayer = new Group();
		this.frontLayer = new Group();
		
		this.rings.forEach(ring -> {
			this.frontLayer.getChildren().add(ring.frontHead);
			this.backLayer.getChildren().add(ring.backHead);
			this.frontLayer.getChildren().addAll(ring.frontParticles);
			this.backLayer.getChildren().addAll(ring.backParticles);
		});
	}
	
	public Node objectBehindLayer() { return this.backLayer; }
	public Node objectFrontLayer() { return this.frontLayer; }
	
	public Animation getAnimation(
		Point2D origin,
		Point2D target,
		BattlePanAnimations panAnimation,
		ShakeAnimationBiFunction shakeAnimation,
		Animation hitAnimation
	) {
		final List<Animation> ringAnimations = new java.util.ArrayList<>();
		for (int i = 0; i < normals.size(); i++) {
			ringAnimations.add(rings.get(i).animation(normals.get(i), target, ((double) i) / normals.size()));
		}
		
		
		return new SequentialTransition(
			panAnimation.panToDefender(),
			new ParallelTransition(
				ringAnimations.stream().toArray(Animation[]::new)
			),
			new ParallelTransition(
				shakeAnimation.apply(),
				hitAnimation
			)
		);
	}
	
	private static class RingOfParticles {
		final Circle frontHead;
		final Circle backHead;
		final List<Circle> frontParticles;
		final List<Circle> backParticles;
		
		public RingOfParticles() {
			this.frontHead = new Circle();
			this.frontHead.setFill(headColor);
			
			this.backHead = new Circle();
			this.backHead.setFill(headColor);
			
			this.frontParticles = java.util.Collections.unmodifiableList(
				java.util.stream.Stream.generate(Circle::new)
						.limit(particlePoolSize)
						.collect(Collectors.toList())
			);
			this.frontParticles.forEach(x -> x.setFill(headColor));
			
			this.backParticles = java.util.Collections.unmodifiableList(
				java.util.stream.Stream.generate(Circle::new)
						.limit(particlePoolSize)
						.collect(Collectors.toList())
			);
			this.backParticles.forEach(x -> x.setFill(headColor));
		}
		
		public Animation animation(Vector3D normal, Point2D target, double offset) {
			final Random rng = new Random();
			
			final Timeline retval = new Timeline();
			double radialOffset = 2 * Math.PI * offset;
			int poolIndex = 0;
			for (Duration i = Duration.ZERO; i.lessThan(animationDuration); i = i.add(tickTime)) {
				final Point2D center = target;
				final double radius = (i.lessThan(animationDuration.multiply(0.6)) ? maxRadius :
					maxRadius * ((animationDuration.toMillis() - i.toMillis()) / (0.4 * animationDuration.toMillis())));
				
				radialOffset += headSpeed / radius;
				final Point3D headLocation = pointOnRing(normal, center, radius, radialOffset);
				final double opacity = (i.lessThan(headFadeDuration) ? i.toMillis() / headFadeDuration.toMillis() : 1.0);
				
				retval.getKeyFrames().add(new KeyFrame(i,
					new KeyValue(this.frontHead.opacityProperty(), (headLocation.z >= 0 ? opacity : 0.0), Interpolator.LINEAR),
					new KeyValue(this.backHead.opacityProperty(), (headLocation.z >= 0 ? 0.0 : opacity), Interpolator.LINEAR),
					new KeyValue(this.frontHead.centerXProperty(), headLocation.x, Interpolator.LINEAR),
					new KeyValue(this.backHead.centerXProperty(), headLocation.x, Interpolator.LINEAR),
					new KeyValue(this.frontHead.centerYProperty(), headLocation.y, Interpolator.LINEAR),
					new KeyValue(this.backHead.centerYProperty(), headLocation.y, Interpolator.LINEAR),
					new KeyValue(this.frontHead.radiusProperty(), 6 + headLocation.z / 30, Interpolator.LINEAR),
					new KeyValue(this.backHead.radiusProperty(), 6 + headLocation.z / 30, Interpolator.LINEAR)
				));
				
				// now for the particles
				
				poolIndex = (poolIndex + particlesPerTick) % particlePoolSize;
				for (int j = 0; j < particlesPerTick; j++) {
					Circle frontParticle = this.frontParticles.get(poolIndex + j);
					Circle backParticle = this.backParticles.get(poolIndex + j);
					
					retval.getKeyFrames().add(new KeyFrame(i,
						new KeyValue(frontParticle.opacityProperty(), (headLocation.z >= 0 ? opacity * 0.8 : 0.0), Interpolator.DISCRETE),
						new KeyValue(backParticle.opacityProperty(), (headLocation.z >= 0 ? 0.0 : opacity * 0.8), Interpolator.DISCRETE),
						new KeyValue(frontParticle.centerXProperty(), headLocation.x, Interpolator.DISCRETE),
						new KeyValue(backParticle.centerXProperty(), headLocation.x, Interpolator.DISCRETE),
						new KeyValue(frontParticle.centerYProperty(), headLocation.y, Interpolator.DISCRETE),
						new KeyValue(backParticle.centerYProperty(), headLocation.y, Interpolator.DISCRETE),
						new KeyValue(frontParticle.radiusProperty(), 6 + headLocation.z / 30, Interpolator.DISCRETE),
						new KeyValue(backParticle.radiusProperty(), 6 + headLocation.z / 30, Interpolator.DISCRETE)
					));
					retval.getKeyFrames().add(new KeyFrame(min(animationDuration, i.add(particleDuration)),
						new KeyValue(frontParticle.opacityProperty(), 0.0, Interpolator.LINEAR),
						new KeyValue(backParticle.opacityProperty(), 0.0, Interpolator.LINEAR),
						new KeyValue(frontParticle.centerXProperty(), headLocation.x + 5 - rng.nextDouble() * 10, Interpolator.LINEAR),
						new KeyValue(backParticle.centerXProperty(), headLocation.x + 5 - rng.nextDouble() * 10, Interpolator.LINEAR),
						new KeyValue(frontParticle.centerYProperty(), headLocation.y + 5 - rng.nextDouble() * 10, Interpolator.LINEAR),
						new KeyValue(backParticle.centerYProperty(), headLocation.y + 5 - rng.nextDouble() * 10, Interpolator.LINEAR),
						new KeyValue(frontParticle.radiusProperty(), 0.0, Interpolator.LINEAR),
						new KeyValue(backParticle.radiusProperty(), 0.0, Interpolator.LINEAR)
					));
					
				}
			}
			
			retval.getKeyFrames().add(new KeyFrame(animationDuration.add(Duration.ONE),
				new KeyValue(this.frontHead.opacityProperty(), 0.0, Interpolator.LINEAR),
				new KeyValue(this.backHead.opacityProperty(), 0.0, Interpolator.LINEAR)
			));
			
			return retval;
		}
		
		/**
		 * @param normal a vector normal to the plane containing the ring
		 * @param center the center of the ring. The center always has (z = 0), so center is point2D to set the x and y
		 * @param radius the radius of the ring
		 * @param index the particle index. (index = 0) will be on the (z = 0) plane.
		 */
		private static Point3D pointOnRing(Vector3D normal, Point2D center, double radius, double rads) {
			final double sin = Math.sin(rads);
			final double cos = Math.cos(rads);
			
			final Vector3D arbitrary = (normal.x == 0 && normal.y == 0 ? new Vector3D(0,1,0) : new Vector3D(0,0,1));
			
			final Vector3D perp1 = normal.cross(new Vector3D(0,0,1));
			final Vector3D perp2 = perp1.cross(normal);
			
			return new Point3D(center, 0)
					.add(perp1.withLength(radius * cos))
					.add(perp2.withLength(radius * sin));
		}
	}
	
	
	private static final class Point3D {
		public final double x;
		public final double y;
		public final double z;
		public Point3D(double x, double y, double z) {this.x = x; this.y = y; this.z = z;}
		public Point3D(Point2D xy, double z) {this.x = xy.getX(); this.y = xy.getY(); this.z = z;}
		public Point3D add(Vector3D rhs) {return new Point3D(this.x + rhs.x, this.y + rhs.y, this.z + rhs.z);}
	}
	private static final class Vector3D {
		public final double x;
		public final double y;
		public final double z;
		public Vector3D(double x, double y, double z) {this.x = x; this.y = y; this.z = z;}
		/** Cross product */
		public Vector3D cross(Vector3D other) {return new Vector3D(
			this.y * other.z - this.z * other.y,
			this.z * other.x - this.x * other.z,
			this.x * other.y - this.y * other.x
		);}
		/** this vector's length squared */
		public double lengthSquared() {return x * x + y * y + z * z;}
		/** this vector's length */
		public double length() {return Math.sqrt(this.lengthSquared());}
		/** A vector with the same direction but the given length */
		public Vector3D withLength(double newLength) {
			final double factor = newLength / this.length();
			return new Vector3D(this.x * factor, this.y * factor, this.z * factor);
		}
	}
	
	private static Duration min(Duration a, Duration b) {return (a.lessThan(b) ? a : b);}
	
}
