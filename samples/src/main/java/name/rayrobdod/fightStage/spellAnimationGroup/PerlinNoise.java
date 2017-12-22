package name.rayrobdod.fightStage.spellAnimationGroup;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Standard Perlin noise implementation.
 */
final class PerlinNoise {
	private static final int max = 0xFF;
	private final List<Integer> permutation;
	private final List<Double> g1;
	private final List<Double> g2;
	
	public PerlinNoise(Random seed) {
		final List<Integer> range = IntStream.rangeClosed(0, max)
				.boxed().collect(Collectors.toList());
		permutation = new java.util.ArrayList<>(range);
		Collections.shuffle(permutation);
		g1 = seed.doubles(max + 1)
				.boxed().collect(Collectors.toList());
		g2 = seed.doubles(max + 1)
				.boxed().collect(Collectors.toList());
	}
	
	// domain of -0.2 to 0.2 ???
	public double raw1D(double arg) {
		final int leftInt = ((int) Math.floor(arg)) & max;
		final int rightInt = ((int) Math.ceil(arg)) & max;
		final double leftFrac = arg - Math.floor(arg);
		final double rightFrac = leftFrac - 1.0;
		
		final double sx = leftFrac * leftFrac * (3d - 2d * leftFrac);
		final double u = leftFrac * g1.get(permutation.get( leftInt ));
		final double v = rightFrac * g1.get(permutation.get( rightInt ));
		
		return u + sx * (v - u);
	}
	
	// domain of -0.2 to 0.2 ???
	public double raw2D(double x, double y) {
		final int ix0 = ((int) Math.floor(x)) & max;
		final int ix1 = ((int) Math.ceil(x)) & max;
		final double fx0 = x - Math.floor(x);
		final double fx1 = fx0 - 1.0;
		final double sx = fx0 * fx0 * (3d - 2d * fx0);
		
		final int iy0 = ((int) Math.floor(y)) & max;
		final int iy1 = ((int) Math.ceil(y)) & max;
		final double fy0 = y - Math.floor(y);
		final double fy1 = fy0 - 1.0;
		final double sy = fy0 * fy0 * (3d - 2d * fy0);
		
		
		double a;
		{
			final double u = fx0 * g1.get(permutation.get(ix0)) + fy0 * g2.get(permutation.get(iy0));
			final double v = fx1 * g1.get(permutation.get(ix1)) + fy0 * g2.get(permutation.get(iy0));
			a = u + sx * (v - u);
		}
		double b;
		{
			final double u = fx0 * g1.get(permutation.get(ix0)) + fy1 * g2.get(permutation.get(iy1));
			final double v = fx1 * g1.get(permutation.get(ix1)) + fy1 * g2.get(permutation.get(iy1));
			b = u + sx * (v - u);
		}
		return a + sy * (b - a);
	}
	
	// domain of -0.2 to 0.2 ???
	public double sum1D(double arg, double a, double b, int n) {
		final List<Double> scale = DoubleStream.iterate(1d, (param) -> param * a).limit(n)
				.boxed().collect(Collectors.toList());
		final List<Double> p = DoubleStream.iterate(arg, (param) -> param * b).limit(n)
				.boxed().collect(Collectors.toList());
		
		// scale.zip(p).map{case (s, p) => this.raw1D(p) / s}.sum
		// Java can't do `zip`, apparently.
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			sum += this.raw1D(p.get(i)) / scale.get(i);
		}
		return sum;
	}
	
	// domain of -0.2 to 0.2 ???
	public double sum2D(double x, double y, double a, double b, int n) {
		final List<Double> scale = DoubleStream.iterate(1d, (param) -> param * a).limit(n)
				.boxed().collect(Collectors.toList());
		final List<Double> p1 = DoubleStream.iterate(x, (param) -> param * b).limit(n)
				.boxed().collect(Collectors.toList());
		final List<Double> p2 = DoubleStream.iterate(y, (param) -> param * b).limit(n)
				.boxed().collect(Collectors.toList());
		
		// scale.zip(p1).zip(p2).map{case ((s, p1), p2) => this.raw2D(p1, p2) / s}.sum
		// Java can't do `zip`, apparently.
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			sum += this.raw2D(p1.get(i), p2.get(i)) / scale.get(i);
		}
		return sum;
	}
}
