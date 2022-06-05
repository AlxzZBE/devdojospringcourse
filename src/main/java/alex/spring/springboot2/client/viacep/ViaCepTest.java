package alex.spring.springboot2.client.viacep;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class ViaCepTest {
    public static void main(String[] args) {
        Cep forObject = new RestTemplate().getForObject("https://viacep.com.br/ws/{cep}/json/", Cep.class, "06810170");

        ResponseEntity.ok();
        log.info(forObject);
    }
}
