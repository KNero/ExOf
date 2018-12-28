package team.balam.exof.module.service.annotation;

import java.lang.annotation.*;

/**
 * value and name is service name. <br/>
 * schedule that  is registered scheduler auto. (cron expression) <br/>
 * if internal is true then can't call by socket.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(Services.class)
public @interface Service {
	String value() default "";
	String name() default "";
	String schedule() default "";

	String groupId() default "";
}
