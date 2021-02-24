package client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileClient {
	
	public void addFile(MultipartFile file, Path filePath) throws IOException {
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	public Resource getFile(String fileLocation, Path basePath) throws MalformedURLException {
		//Build path
		Path filePath = basePath.resolve(fileLocation).normalize();

		// Get song from disk
		Resource file = new UrlResource(filePath.toUri());

		return file;
	}
	
}
