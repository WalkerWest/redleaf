package redleaf;

public class QueryTimeoutException extends Exception  {

	private static final long serialVersionUID = -8016984418496671999L;
	
	public QueryTimeoutException(String message) {
		super(message);
	}

}
