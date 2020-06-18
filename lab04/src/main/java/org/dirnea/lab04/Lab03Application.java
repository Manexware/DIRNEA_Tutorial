package org.dirnea.lab04;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab03Application {

	static {
		SpringDocUtils.getConfig().addHiddenRestControllers(BasicErrorController.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Lab03Application.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components().addSecuritySchemes("basicScheme", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"))).info(new Info().title("Custom API").version("100")).addTagsItem(new Tag().name("mytag"));
	}

	@Bean
	public OpenAPI customOpenAPI(BuildProperties buildProperties) {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("basicScheme",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
				.info(new Info().title("SpringShop API").version(buildProperties.getVersion())
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}
