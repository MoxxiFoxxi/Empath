package model.exceptions;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseException extends SQLException {
	private static final long serialVersionUID = 1L;
	
	public DatabaseException(String e) {
		super(e);
	}
	
	public DatabaseException(Throwable e) {
		super(e);
	}
	
	public DatabaseException(String message, Throwable exception) {
		super(message, exception);
	}
}
