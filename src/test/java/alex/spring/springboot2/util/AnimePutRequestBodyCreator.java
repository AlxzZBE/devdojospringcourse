package alex.spring.springboot2.util;

import alex.spring.springboot2.requests.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {
    public static AnimePutRequestBody createdAnimePutRequestBody() {
        return AnimePutRequestBody.builder()
                .name(AnimeCreator.createdValidUpdatedAnime().getName())
                .id(AnimeCreator.createdValidUpdatedAnime().getId())
                .build();
    }
}
