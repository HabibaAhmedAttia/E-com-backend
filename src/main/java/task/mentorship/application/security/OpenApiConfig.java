package task.mentorship.application.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
	    info = @Info(
	    		contact=@Contact(
	    				name="Habiba Ahmed",
	    				email="habibaahmedattia5@gmail.com"
	    				),
	        title = "E-commerce",
	        version = "1.0",
	        description = "API Documentation for fruitkha project",
	        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")
	    ),
	    servers = {
	        @Server(url = "http://localhost:8080", description = "Local server"),
	    })
@SecurityScheme(
	    name = "bearerAuth", 
	    description = "JWT auth description",
	    type = SecuritySchemeType.HTTP,
	    scheme = "bearer",
	    bearerFormat = "JWT",
	    in=SecuritySchemeIn.HEADER
	)
public class OpenApiConfig {

}
