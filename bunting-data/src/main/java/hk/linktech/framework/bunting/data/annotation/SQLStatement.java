package hk.linktech.framework.bunting.data.annotation;


public @interface SQLStatement {
	String value() default "";
	String check() default "";
}
