package mate.academy.rickandmorty.controller;

import io.swagger.oas.annotations.Operation;
import java.util.List;
import mate.academy.rickandmorty.dto.CharacterDto;
import mate.academy.rickandmorty.service.RickAndMortyClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RickAndMortyController {
    private final RickAndMortyClient rickAndMortyClient;

    public RickAndMortyController(RickAndMortyClient rickAndMortyClient) {
        this.rickAndMortyClient = rickAndMortyClient;
    }

    @Operation(summary = "Get random character")
    @GetMapping
    public CharacterDto getRandomCharacter() {
        return rickAndMortyClient.getRandomCharacter();
    }

    @Operation(summary = "Get list of characters matching parameter")
    @GetMapping("/character")
    public List<CharacterDto> getCharactersList(@RequestParam String character) {
        return rickAndMortyClient.getCharactersList(character);
    }
}
