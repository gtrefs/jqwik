package net.jqwik.newArbitraries;

import net.jqwik.api.*;

import java.util.*;

import static net.jqwik.newArbitraries.NArbitraryTestHelper.*;
import static org.assertj.core.api.Assertions.*;

class NIntegralShrinkingTests {

	@Group
	class Integers {

		@Example
		void shrinkFrom0DoesNotShrink() {
			NShrinkCandidates<Integer> shrinker = new NIntegerShrinkCandidates(-10, 10);
			assertThat(shrinker.distance(0)).isEqualTo(0);
			assertThat(shrinker.nextCandidates(0)).isEmpty();
		}

		@Example
		void shrinkFromValueOutsideRangeReturnsNothing() {
			NShrinkCandidates<Integer> shrinker = new NIntegerShrinkCandidates(-10, 10);
			assertThat(shrinker.nextCandidates(20)).isEmpty();
		}

		@Example
		void if0isInRangeDistanceIsAbsoluteNumber() {
			NShrinkCandidates<Integer> shrinker = new NIntegerShrinkCandidates(-10000, 10000);
			assertThat(shrinker.distance(10000)).isEqualTo(10000);
			assertThat(shrinker.distance(10)).isEqualTo(10);
			assertThat(shrinker.distance(1)).isEqualTo(1);
			assertThat(shrinker.distance(-10000)).isEqualTo(10000);
			assertThat(shrinker.distance(-10)).isEqualTo(10);
			assertThat(shrinker.distance(-1)).isEqualTo(1);
		}

		@Example
		void if0isOutsideRangeDistanceIsDistanceToShrinkTarget() {
			NShrinkCandidates<Integer> shrinkerAboveZero = new NIntegerShrinkCandidates(10, 100);
			assertThat(shrinkerAboveZero.distance(20)).isEqualTo(10);
			assertThat(shrinkerAboveZero.distance(10)).isEqualTo(0);

			NShrinkCandidates<Integer> shrinkerBelowZero = new NIntegerShrinkCandidates(-100, -10);
			assertThat(shrinkerBelowZero.distance(-20)).isEqualTo(10);
			assertThat(shrinkerBelowZero.distance(-10)).isEqualTo(0);
		}

		@Example
		void shrinkPositiveValueTowards0If0isInRange() {
			NShrinkCandidates<Integer> shrinker = new NIntegerShrinkCandidates(-10, 20);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, 10);
			assertThat(allShrunkValues).containsExactly(5, 3, 2, 1, 0);
		}

		@Example
		void shrinkNegativeValueTowards0If0isInRange() {
			NShrinkCandidates<Integer> shrinker = new NIntegerShrinkCandidates(-10, 20);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, -10);
			assertThat(allShrunkValues).containsExactly(-5, -3, -2, -1, 0);
		}

		@Example
		void shrinkNegativeValueTowardMaxIf0IsOutsideRange() {
			NShrinkCandidates<Integer> shrinker = new NIntegerShrinkCandidates(-20, -5);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, -10);
			assertThat(allShrunkValues).containsExactly(-8, -7, -6, -5);
		}

		@Example
		void shrinkPositiveValueTowardMinIf0IsOutsideRange() {
			NShrinkCandidates<Integer> shrinker = new NIntegerShrinkCandidates(5, 20);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, 10);
			assertThat(allShrunkValues).containsExactly(8, 7, 6, 5);
		}

	}

	@Group
	class Longs {

		@Example
		void distanceOfLongIsIntMaxIfLongLargerThanIntMax() {
			NShrinkCandidates<Long> shrinker = new NLongShrinkCandidates(Long.MIN_VALUE, Long.MAX_VALUE);
			assertThat(shrinker.distance(10000L)).isEqualTo(10000);
			assertThat(shrinker.distance(-10000L)).isEqualTo(10000);
			assertThat(shrinker.distance(100_000_000_000L)).isEqualTo(Integer.MAX_VALUE);
			assertThat(shrinker.distance(-100_000_000_000L)).isEqualTo(Integer.MAX_VALUE);
		}

		@Example
		void longsAreShrunkEvenAboveIntMax() {
			NShrinkCandidates<Long> shrinker = new NLongShrinkCandidates(Long.MIN_VALUE, Long.MAX_VALUE);
			assertThat(shrinker.nextCandidates(128_000_000_000L)).containsExactly(64_000_000_000L);
			List<Long> allShrunkValues = shrinkToEnd(shrinker, 128_000_000_000L);
			assertThat(allShrunkValues).startsWith( //
					64_000_000_000L, //
					32_000_000_000L, //
					16_000_000_000L, //
					8_000_000_000L, //
					4_000_000_000L, //
					2_000_000_000L, //
					1_000_000_000L //
			);
			assertThat(allShrunkValues).endsWith(15L, 8L, 4L, 2L, 1L, 0L);
		}

		@Example
		void longsAreShrunkEvenBelowIntMin() {
			NShrinkCandidates<Long> shrinker = new NLongShrinkCandidates(Long.MIN_VALUE, Long.MAX_VALUE);
			List<Long> allShrunkValues = shrinkToEnd(shrinker, -128_000_000_000L);
			assertThat(allShrunkValues).startsWith( //
					-64_000_000_000L, //
					-32_000_000_000L, //
					-16_000_000_000L, //
					-8_000_000_000L, //
					-4_000_000_000L, //
					-2_000_000_000L, //
					-1_000_000_000L //
			);
			assertThat(allShrunkValues).endsWith(-15L, -8L, -4L, -2L, -1L, 0L);
		}

	}

}