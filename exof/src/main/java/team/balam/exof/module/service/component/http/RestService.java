package team.balam.exof.module.service.component.http;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(RestServices.class)
public @interface RestService {
	HttpMethod method();
	String name() default "";

    /**
     * json body 의 내용을 Object 로 변환해 준다.
     */
	Class<?> bodyToObject() default Object.class;
}
