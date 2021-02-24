package rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import model.Song;
import model.exceptions.DatabaseException;
import model.exceptions.InvalidSongException;
import service.EmpathService;

@RestController
@RequestMapping("empath")
public class EmpathController {
    private static final Logger logger = LoggerFactory.getLogger(EmpathController.class);
	
	@Autowired EmpathService empathService;
	
	@PostMapping(value = "uploadSongFiles/{id}", produces = "application/json")
	public void uploadSongFiles(@PathVariable int id, @RequestPart MultipartFile songFile, @RequestPart MultipartFile artFile) throws DatabaseException, InvalidSongException {
		empathService.addSongFiles(id, songFile, artFile);
	}

	@GetMapping(value = "playlist", produces = "application/json")
	public List<Song> createPlaylist(String url) throws DatabaseException, URISyntaxException {
		return empathService.createPlaylist(url);
	}
	
	@GetMapping(value = "song/{id}")
	public ResponseEntity<Resource> getSong(@PathVariable int id, HttpServletRequest request) throws FileNotFoundException, SQLException {
		Resource songFile = empathService.getSong(id);
		
		// Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(songFile.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + songFile.getFilename() + "\"")
                .body(songFile);
	}
	
	@GetMapping(value = "song/art/{id}")
	public ResponseEntity<Resource> getSongArt(@PathVariable int id, HttpServletRequest request) throws FileNotFoundException, SQLException {
		Resource artFile = empathService.getSongArt(id);
		
		// Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(artFile.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + artFile.getFilename() + "\"")
                .body(artFile);
	}
}
