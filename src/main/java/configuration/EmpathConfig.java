package configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import com.mysql.cj.jdbc.MysqlDataSource;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@PropertySource("classpath:application.properties")
public class EmpathConfig {
	
	@Value("${database.host}")
	private String databaseHost;
	
	@Value("${database.port}")
	private int databasePort;
	
	@Value("${database.username}")
	private String username;
	
	@Value("${database.password}")
	private String password;
	
	@Value("${database.schema}")
	private String dbName;
	
	
	//Create Bean for RestTemplate to do REST calls
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	// Create Bean for database to initialize it once
	@Bean
	public MysqlDataSource mysqlDataSource() {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("admin");
		dataSource.setPassword("CMSC495T3am3");
		dataSource.setServerName("team3-db.c9f9vjqkabc3.us-east-1.rds.amazonaws.com");
		dataSource.setPort(databasePort);
		dataSource.setDatabaseName(dbName);
		
		return dataSource;
	}
	
	// Create bean for swagger api
	 @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())                          
          .build();                                           
    }
	 
	 @Bean
	 public ServletWebServerFactory servletContainer() {
		 TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		 return tomcat;
	 }
}
