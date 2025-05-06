import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI appointmentAPI() {
        return new OpenAPI()
                .info(new Info().title("Hospital Appointment API")
                        .description("APIs for Appointment Management")
                        .version("1.0"));
    }
}
