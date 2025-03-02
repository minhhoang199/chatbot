package com.example.chatwebproject.aop.validation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})  // Can be applied to methods only
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface ValidationRequest {
}
