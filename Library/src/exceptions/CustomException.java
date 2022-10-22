package exceptions;

public class CustomException extends Exception {
	
	int statusCode;
	String message;
	
	public CustomException(String message)
	{
		super(message);
	}
}