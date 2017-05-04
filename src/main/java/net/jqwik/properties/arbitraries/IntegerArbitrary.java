package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;
import net.jqwik.properties.shrinking.*;

public class IntegerArbitrary extends NullableArbitrary<Integer> {

	private int min;
	private int max;

	public IntegerArbitrary(int min, int max) {
		super(Integer.class);
		this.min = min;
		this.max = max;
	}

	public IntegerArbitrary() {
		this(0, 0);
	}

	@Override
	protected RandomGenerator<Integer> baseGenerator(int tries) {
		if (min == 0 && max == 0) {
			int max = Arbitrary.defaultMaxFromTries(tries);
			return RandomGenerators.choose(-max, max).withSamples(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		return RandomGenerators.choose(min, max);
	}

	@Override
	public Shrinkable<Integer> shrinkableFor(Integer value) {
		int shrinkingMin = this.min != 0 ? this.min : Integer.MIN_VALUE;
		int shrinkingMax = this.max != 0 ? this.min : Integer.MAX_VALUE;
		return new IntegerShrinker(shrinkingMin, shrinkingMax).shrink(value);
	}

	public void configure(IntRange intRange) {
		min = intRange.min();
		max = intRange.max();
	}


}