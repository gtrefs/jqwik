package net.jqwik.execution;

import net.jqwik.properties.*;
import net.jqwik.support.*;
import org.junit.platform.commons.support.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class CheckedProperty {

	private static Supplier<Random> RNG = ThreadLocalRandom::current;

	private final static String CONFIG_METHOD_NAME = "configure";

	public final String propertyName;
	public final CheckedFunction forAllFunction;
	public final List<Parameter> forAllParameters;
	public final ArbitraryProvider arbitraryProvider;
	public final int tries;
	public final long randomSeed;

	public CheckedProperty(String propertyName, CheckedFunction forAllFunction,
						   List<Parameter> forAllParameters, ArbitraryProvider arbitraryProvider, int tries, long randomSeed) {
		this.propertyName = propertyName;
		this.forAllFunction = forAllFunction;
		this.forAllParameters = forAllParameters;
		this.arbitraryProvider = arbitraryProvider;
		this.tries = tries;
		this.randomSeed = randomSeed;
	}

	public PropertyCheckResult check() {
		// Long.MIN_VALUE is the default for Property.seed() annotation property
		long effectiveSeed = randomSeed == Long.MIN_VALUE ? RNG.get().nextLong() : randomSeed;
		try {
			return createGenericProperty().check(tries, effectiveSeed);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(propertyName, 0, 0, effectiveSeed, Collections.emptyList(), cannotFindArbitraryException);
		}
	}

	private Arbitrary<Object> findArbitrary(Parameter parameter) {
		Optional<Arbitrary<Object>> arbitraryOptional = arbitraryProvider.forParameter(parameter);
		Arbitrary<Object> arbitrary = arbitraryOptional.orElseThrow(() -> new CannotFindArbitraryException(parameter));
		configureArbitrary(arbitrary, parameter);
		return arbitrary;
	}

	private void configureArbitrary(Arbitrary<Object> objectArbitrary, Parameter parameter) {
		Arrays.stream(parameter.getDeclaredAnnotations()).forEach(annotation -> {
			try {
				Method configureMethod = objectArbitrary.inner().getClass().getMethod(CONFIG_METHOD_NAME, annotation.annotationType());
				JqwikReflectionSupport.invokeMethod(configureMethod, objectArbitrary.inner(), annotation);
			} catch (NoSuchMethodException ignore) {
			}
		});
	}

	private GenericProperty createGenericProperty() {
		List<Arbitrary> arbitraries = forAllParameters.stream().map(this::findArbitrary).collect(Collectors.toList());
		return new GenericProperty(propertyName, arbitraries, forAllFunction);
	}

	public int getTries() {
		return tries;
	}

}