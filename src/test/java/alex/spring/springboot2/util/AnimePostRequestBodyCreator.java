package alex.spring.springboot2.util;

import alex.spring.springboot2.requests.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {
    public static AnimePostRequestBody createdAnimePostRequestBody() {
        return AnimePostRequestBody.builder()
                .name(AnimeCreator.createdAnimeToBeSaved().getName())
                .build();
    }
}
