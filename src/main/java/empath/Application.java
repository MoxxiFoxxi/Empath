package empath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan({"client", "configuration", "rest", "service"})
public class Application {

	public static void main(String[] args) {
		// Start the application
		SpringApplication.run(Application.class, args);
	}
}