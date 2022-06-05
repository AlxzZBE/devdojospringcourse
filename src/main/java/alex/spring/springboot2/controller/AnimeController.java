package alex.spring.springboot2.controller;

import alex.spring.springboot2.domain.Anime;
import alex.spring.springboot2.requests.AnimePostRequestBody;
import alex.spring.springboot2.requests.AnimePutRequestBody;
import alex.spring.springboot2.services.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

// SOMENTE os ENDPOINTS
@RestController
@RequestMapping("animes")
@Log4j2
@RequiredArgsConstructor
public class AnimeController {
    private final AnimeService animeservice;

    @GetMapping
    @Operation(summary = "List all animes paginated", description = """
             The default size is 20, use the parameter size
             to change the default value""", tags = {"anime"})
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(animeservice.listAll(pageable));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Anime>> listAll() {
        return ResponseEntity.ok(animeservice.listAllNonPageable());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable long id) {
        return ResponseEntity.ok(animeservice.findByIdOrThrowBadRequestException(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/by-id/{id}")
    public ResponseEntity<Anime> findByIdAuthenticationPrincipal(@PathVariable long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails);
        return ResponseEntity.ok(animeservice.findByIdOrThrowBadRequestException(id));
    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(animeservice.findByName(name));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody) {
        return new ResponseEntity<>(animeservice.save(animePostRequestBody), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/admin/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "When Anime Does Not Exist in The Database")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {
        animeservice.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping()
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody) {
        animeservice.replace(animePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
