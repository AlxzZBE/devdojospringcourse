package alex.spring.springboot2.controller;

import alex.spring.springboot2.domain.Anime;
import alex.spring.springboot2.requests.AnimePostRequestBody;
import alex.spring.springboot2.requests.AnimePutRequestBody;
import alex.spring.springboot2.services.AnimeService;
import alex.spring.springboot2.util.AnimeCreator;
import alex.spring.springboot2.util.AnimePostRequestBodyCreator;
import alex.spring.springboot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
    @InjectMocks // Test Class
    private AnimeController animeController;

    @Mock // Inside Classes
    private AnimeService animeServiceMock;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createdValidAnime()));
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createdValidAnime()));

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createdValidAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createdValidAnime()));

        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createdValidAnime());

        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("List returns list of Anime inside page object when successful")
    void list_ReturnsListListOfAnimeInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createdValidAnime().getName();
        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertThat(animePage).isNotEmpty();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAll returns list of Anime inside page object when successful")
    void list_ReturnsListListOfAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createdValidAnime().getName();
        List<Anime> animeList = animeController.listAll().getBody();

        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns Anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createdValidAnime().getId();

        Anime anime = animeController.findById(1).getBody();

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns list of Anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createdValidAnime().getName();

        List<Anime> anime = animeController.findByName("dwadwa").getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(anime.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns empty list when anime is not found")
    void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> anime = animeController.findByName("dwadwadaw").getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns Anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeController.save(AnimePostRequestBodyCreator.createdAnimePostRequestBody()).getBody();

        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createdValidAnime());
    }

    @Test
    @DisplayName("replace update Anime when successful")
    void replace_UpdateAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeController.replace(AnimePutRequestBodyCreator.createdAnimePutRequestBody()))
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createdAnimePutRequestBody());

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete Anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeController.delete(1))
                .doesNotThrowAnyException();

        ResponseEntity<Void> entity = animeController.delete(1);

        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}