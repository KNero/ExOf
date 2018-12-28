package team.balam.exof.module.service.annotation;

import team.balam.exof.module.service.component.http.RestService;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RestServices {
    RestService[] value();
}
