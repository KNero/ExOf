package team.balam.exof.module.service.component.http;

import team.balam.exof.module.service.annotation.RestServices;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(RestServices.class)
public @interface RestService {
	HttpMethod method();
	String name() default "";
}
