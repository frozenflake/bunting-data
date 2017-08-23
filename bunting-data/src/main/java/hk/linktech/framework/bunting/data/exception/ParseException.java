package hk.linktech.framework.bunting.data.exception;

public class ParseException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParseException() {
		super();
	}
	public ParseException( String msg ) {
		super(msg);
	}
	public ParseException( String msg, Throwable t ) {
		super(msg,t);
	}
}
