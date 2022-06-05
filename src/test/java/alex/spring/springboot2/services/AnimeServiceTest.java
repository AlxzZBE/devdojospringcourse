package alex.spring.springboot2.services;

import alex.spring.springboot2.domain.Anime;
import alex.spring.springboot2.exception.BadRequestException;
import alex.spring.springboot2.repositories.AnimeRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {
    @InjectMocks // Test Class
    private AnimeService animeService;

    @Mock // Inside Classes
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createdValidAnime()));
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(List.of(AnimeCreator.createdValidAnime()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createdValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createdValidAnime()));

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createdValidAnime());

        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("ListAll returns list of Anime inside page object when successful")
    void listAll_ReturnsListOfAnimeInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createdValidAnime().getName();
        Long expectedId = AnimeCreator.createdValidUpdatedAnime().getId();

        Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

        Assertions.assertThat(animePage).isNotEmpty();
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .isNotNull()
                .hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
        Assertions.assertThat(animePage.toList().get(0).getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("ListAll returns list of Anime when successful")
    void listAllNonPageable_ReturnsListOfAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createdValidAnime().getName();
        Long expectedId = AnimeCreator.createdValidUpdatedAnime().getId();
        List<Anime> animeList = animeService.listAllNonPageable();

        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
        Assertions.assertThat(animeList.get(0).getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns Anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createdValidAnime().getId();

        Anime anime = animeService.findByIdOrThrowBadRequestException(1);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws BadRequestException When anime is not found")
    void findById_ThrowsBadRequestException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> this.animeService.findByIdOrThrowBadRequestException(2))
                .withMessageContaining("Anime Not Found");
    }

    @Test
    @DisplayName("findByName returns list of Anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        String expectedName = AnimeCreator.createdValidAnime().getName();

        List<Anime> anime = animeService.findByName("dwadwa");

        Assertions.assertThat(anime)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(anime.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns empty list when anime is not found")
    void findByName_ReturnsEmptyList_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeEmptyList = animeService.findByName("dwadwadaw");

        Assertions.assertThat(animeEmptyList)
                .isNotNull()
                .hasSize(0)
                .isEmpty();
    }

    @Test
    @DisplayName("save returns Anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeService.save(AnimePostRequestBodyCreator.createdAnimePostRequestBody());

        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createdValidAnime());
    }

    @Test
    @DisplayName("replace updates Anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createdAnimePutRequestBody()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("delete Anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeService.delete(1))
                .doesNotThrowAnyException();
    }
}