package rest;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.cognitiveservices.vision.faceapi.models.DetectedFace;
import com.microsoft.azure.cognitiveservices.vision.faceapi.models.Emotion;

import client.FaceClient;

@RestController
@RequestMapping("face")
public class FaceController {
	@Autowired FaceClient faceClient;
	
	@GetMapping(value = "/detect", produces = "application/json")
	public String detect(@RequestParam(value = "url", required = true) String url) throws URISyntaxException, JsonProcessingException {
		DetectedFace[] faces = faceClient.getFace(url);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(faces[0]);
	}
	
	@GetMapping(value = "/emotion", produces = "application/json")
	public String emotions(@RequestParam(value = "url", required = true) String url) throws URISyntaxException, JsonProcessingException {
		Emotion emotions = faceClient.getFaceEmotions(url);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(emotions);
	}
}
