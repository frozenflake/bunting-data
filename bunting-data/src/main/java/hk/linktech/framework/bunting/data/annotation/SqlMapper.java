package hk.linktech.framework.bunting.data.annotation;

import hk.linktech.framework.bunting.data.EntityWrapper;
import hk.linktech.framework.bunting.data.ExceptionHandler;
import hk.linktech.framework.bunting.data.SQLType;
import hk.linktech.framework.bunting.data.SqlParser;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SqlMapper {
	String value() default "";
	SQLStatement[] statements() default {};
	SQLType type() default SQLType.UNDIFINED;
	String name() default "";
	Class<? extends SqlParser> parser() default SqlParser.class;
	Class<?> entityClass() default Void.class;
	Class<? extends EntityWrapper> entityWrapper() default EntityWrapper.class;
	Class<? extends ExceptionHandler> exceptionHandler() default ExceptionHandler.class;
}
