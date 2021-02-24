package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.microsoft.azure.cognitiveservices.vision.faceapi.models.DetectedFace;
import com.microsoft.azure.cognitiveservices.vision.faceapi.models.Emotion;
import com.microsoft.azure.cognitiveservices.vision.faceapi.models.ImageUrl;

@Component
public class FaceClient {
	
	@Autowired RestTemplate restTempalte;
	
	// Replace <Subscription Key> with your valid subscription key.
	@Value("${face.subscriptionKey}")
	private String subscriptionKey;

	@Value("${face.host}")
	private String uriBase;
	
	private static final String faceAttributes = "emotion";
	//private static final String fullFaceAttributes = "emotion,hair,makeup,occlusion,accessories,blur,exposure,noise";
	
	public DetectedFace[] getFace(String imageUrlStr) throws URISyntaxException {
		URIBuilder builder = new URIBuilder(uriBase);

		// Request parameters. All of them are optional.
		builder.setParameter("returnFaceId", "true");
		
		// Prepare the URI for the REST API call.
		URI uri = builder.build();
		
		// Prepare headers that include Face API Key
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Ocp-Apim-Subscription-Key", subscriptionKey);
		
		// Prepare request entity and submit the call
		ImageUrl imageUrl = new ImageUrl().withUrl(imageUrlStr);
		HttpEntity<ImageUrl> entity = new HttpEntity<>(imageUrl, headers);
		DetectedFace[] faces = restTempalte.postForObject(uri, entity, DetectedFace[].class);
		
		return faces;
	}
	
	
	public Emotion getFaceEmotions(String imageUrlStr) throws URISyntaxException {
		URIBuilder builder = new URIBuilder(uriBase);

		// Request parameters. All of them are optional.
		builder.setParameter("returnFaceId", "true");
		builder.setParameter("returnFaceLandmarks", "false");
		builder.setParameter("returnFaceAttributes", faceAttributes);
		
		// Prepare the URI for the REST API call.
		URI uri = builder.build();
		
		// Prepare headers that include Face API Key
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Ocp-Apim-Subscription-Key", subscriptionKey);
		
		// Prepare request entity and submit the call
		ImageUrl imageUrl = new ImageUrl().withUrl(imageUrlStr);
		HttpEntity<ImageUrl> entity = new HttpEntity<>(imageUrl, headers);
		DetectedFace[] faces = restTempalte.postForObject(uri, entity, DetectedFace[].class);
		
		return faces[0].faceAttributes().emotion();
	}
}
