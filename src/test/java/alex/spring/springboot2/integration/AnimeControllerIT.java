package alex.spring.springboot2.integration;

import alex.spring.springboot2.domain.Anime;
import alex.spring.springboot2.domain.DevDojoUser;
import alex.spring.springboot2.repositories.AnimeRepository;
import alex.spring.springboot2.repositories.DevDojoUserRepository;
import alex.spring.springboot2.requests.AnimePostRequestBody;
import alex.spring.springboot2.util.AnimeCreator;
import alex.spring.springboot2.util.AnimePostRequestBodyCreator;
import alex.spring.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private DevDojoUserRepository devDojoUserRepository;

    private static final DevDojoUser USER = DevDojoUser.builder()
            .name("DevDojo")
            .password("{bcrypt}$2a$10$kMDfghlsxmMR4XdW29wNvuA6NxuzPIETTLbdhAVvW0f6hMDMkllOy")
            .username("devdojo")
            .authorities("ROLE_USER")
            .build();

    private static final DevDojoUser ADMIN = DevDojoUser.builder()
            .name("Alex")
            .password("{bcrypt}$2a$10$kMDfghlsxmMR4XdW29wNvuA6NxuzPIETTLbdhAVvW0f6hMDMkllOy")
            .username("alex")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("devdojo", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("alex", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("List returns list of Anime inside page object when successful")
    void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createdAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();
        Long expectedId = savedAnime.getId();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotEmpty().isNotNull();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
        Assertions.assertThat(animePage.toList().get(0).getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("ListAll returns list of Anime when successful")
    void list_ReturnsListOfAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createdAnimeToBeSaved());

        devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();
        Long expectedId = savedAnime.getId();

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes)
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
        Assertions.assertThat(animes.get(0).getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findById returns Anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createdAnimeToBeSaved());

        devDojoUserRepository.save(USER);

        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns list of Anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createdAnimeToBeSaved());

        devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();
        Long expectedId = savedAnime.getId();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> anime = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(anime.get(0).getName()).isEqualTo(expectedName);
        Assertions.assertThat(anime.get(0).getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns empty list when anime is not found")
    void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
        devDojoUserRepository.save(USER);

        List<Anime> anime = testRestTemplateRoleUser.exchange("/animes/find?name=manguito", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(anime).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save returns Anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createdAnimePostRequestBody();
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isEqualTo(AnimeCreator.createdValidAnime().getId());
        Assertions.assertThat(animeResponseEntity.getBody().getName()).isEqualTo(animePostRequestBody.getName());
    }

    @Test
    @DisplayName("replace update Anime when successful")
    void replace_UpdateAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createdAnimeToBeSaved());

        devDojoUserRepository.save(USER);

        savedAnime.setName("new name");
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes",
                HttpMethod.PUT, new HttpEntity<>(savedAnime), void.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete Anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createdAnimeToBeSaved());

        devDojoUserRepository.save(ADMIN);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
                HttpMethod.DELETE, null, void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createdAnimeToBeSaved());

        devDojoUserRepository.save(USER);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}",
                HttpMethod.DELETE, null, void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
