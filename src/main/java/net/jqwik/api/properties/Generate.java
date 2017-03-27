package net.jqwik.api.properties;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Generate {
	String value() default "";
}