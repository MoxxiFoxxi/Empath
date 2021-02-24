package model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidSongException extends Exception {
	static final long serialVersionUID = 1L;
	
	public InvalidSongException(String exception) {
		super(exception);
	}
	
	public InvalidSongException(String message, Throwable exception) {
		super(message, exception);
	}
}
