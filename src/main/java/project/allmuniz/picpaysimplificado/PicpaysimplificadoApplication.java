package project.allmuniz.picpaysimplificado;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "PicPay",
				description = "Desafio Back-End PicPay",
				version = "1"
		)
)
public class PicpaysimplificadoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicpaysimplificadoApplication.class, args);
	}

}
