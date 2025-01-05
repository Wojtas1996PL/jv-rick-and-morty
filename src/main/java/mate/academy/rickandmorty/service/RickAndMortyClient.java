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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Component
public class RickAndMortyClient {
    private static final String BASE_URL = "https://rickandmortyapi.com/api";
    private static final String CHARACTER = "/character";
    private static final int MAX = 826;
    private static final int MIN = 1;
    private static final int RANGE = MAX - MIN + 1;
    private final SessionFactory sessionFactory;

    public RickAndMortyClient(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CharacterDto getRandomCharacter() {
        Transaction transaction = null;
        int random = (int) (Math.random() * RANGE) + MIN;
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = BASE_URL + CHARACTER + "/" + random;
        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            HttpResponse<String> httpResponse = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            CharacterDto characterDto = objectMapper.readValue(httpResponse.body(),
                    new TypeReference<>() {});
            session.persist(characterDto);
            transaction.commit();
            return characterDto;
        } catch (IOException | InterruptedException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public List<CharacterDto> getCharactersList(String character) {
        Transaction transaction = null;
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = BASE_URL + CHARACTER + "?name=" + character;
        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            HttpResponse<String> httpResponse = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            List<CharacterDto> characters = objectMapper
                    .readValue(httpResponse.body(), new TypeReference<>() {});
            transaction.commit();
            return characters;
        } catch (IOException | InterruptedException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
