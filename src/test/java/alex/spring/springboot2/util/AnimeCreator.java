package alex.spring.springboot2.util;

import alex.spring.springboot2.domain.Anime;

public class AnimeCreator {

    public static Anime createdAnimeToBeSaved() {
        return Anime.builder()
                .name("Alex")
                .build();
    }

    public static Anime createdValidAnime() {
        return Anime.builder()
                .name("Alex")
                .id(1L)
                .build();
    }

    public static Anime createdValidUpdatedAnime() {
        return Anime.builder()
                .name("Alex 2")
                .id(1L)
                .build();
    }
}
