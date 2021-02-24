package model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class SongAlreadyExistsException extends Exception {
	static final long serialVersionUID = 1L;
	
	public SongAlreadyExistsException(String exception) {
		super(exception);
	}
	
	public SongAlreadyExistsException(String message, Throwable exception) {
		super(message, exception);
	}
}
