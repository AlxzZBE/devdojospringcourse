package alex.spring.springboot2.client;

import alex.spring.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/2", Anime.class);
        log.info(entity);

        Anime forObject = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class, 2);
        log.info(forObject);

        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(animes));

        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        log.info(exchange.getBody());

//        Anime kingdom = Anime.builder().name("kingdom").build();
//        Anime animeSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class);
//        log.info(animeSaved);

        Anime samurai = Anime.builder().name("Samurai").build();
        ResponseEntity<Anime> samuraiSaved = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.POST,
                new HttpEntity<>(samurai, createJsonHeader()),
                Anime.class);

        log.info(samuraiSaved);

        Anime animeToBeUpdated = samuraiSaved.getBody();
        animeToBeUpdated.setName("New Samurai");

        ResponseEntity<Void> samuraiReplace = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.PUT,
                new HttpEntity<>(animeToBeUpdated, createJsonHeader()),
                Void.class);

        log.info(samuraiReplace);

        ResponseEntity<Void> samuraiDelete = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                animeToBeUpdated.getId());

        log.info(samuraiDelete);

    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
