package mate.academy.rickandmorty.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import mate.academy.rickandmorty.dto.CharacterDto;
import org.springframework.stereotype.Component;

@Component
public class RickAndMortyClient {
    private static final String BASE_URL = "https://rickandmortyapi.com/api";
    private static final String CHARACTER = "/character";
    private static final int MAX = 826;
    private static final int MIN = 1;
    private static final int RANGE = MAX - MIN + 1;

    public CharacterDto getRandomCharacter() {
        int random = (int) (Math.random() * RANGE) + MIN;
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = BASE_URL + CHARACTER + "/" + random;
        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        try {
            HttpResponse<String> httpResponse = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(httpResponse.body(), new TypeReference<>() {
            });
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CharacterDto> getCharactersList(String character) {
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = BASE_URL + CHARACTER + "/" + character;
        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        try {
            HttpResponse<String> httpResponse = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(httpResponse.body(), new TypeReference<>() {
            });
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
