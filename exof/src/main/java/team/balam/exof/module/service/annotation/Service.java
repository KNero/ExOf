package team.balam.exof.module.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * value and name is service name. <br/>
 * schedule that  is registered scheduler auto. (cron expression) <br/>
 * if internal is true then can't call by socket.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Service {
	String value() default "";
	String name() default "";
	String schedule() default "";
	boolean internal() default false;
}
