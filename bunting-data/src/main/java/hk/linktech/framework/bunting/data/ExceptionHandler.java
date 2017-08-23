package hk.linktech.framework.bunting.data;

public interface ExceptionHandler {
	Throwable wrapException( Throwable t ) throws Throwable;
}
