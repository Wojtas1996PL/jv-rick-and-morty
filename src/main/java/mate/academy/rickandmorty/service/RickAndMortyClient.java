package mate.academy.rickandmorty.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import mate.academy.rickandmorty.dto.CharacterModelDto;
import mate.academy.rickandmorty.mapper.CharacterModelMapper;
import mate.academy.rickandmorty.model.CharacterModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
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

    public CharacterModel getRandomCharacter() {
        Transaction transaction = null;
        int random = (int) (Math.random() * RANGE) + MIN;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<CharacterModel> query = session.createQuery("FROM User u "
                    + "WHERE u.externalId = :random", CharacterModel.class);
            query.setParameter("externalId", random);
            transaction.commit();
            return query.getSingleResult();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public List<CharacterModel> getCharactersList(String name) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<CharacterModel> query = session.createQuery("FROM User u "
                    + "WHERE u.name = :%name%", CharacterModel.class);
            query.setParameter("%name%", name);
            transaction.commit();
            return query.getResultList();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public List<CharacterModelDto> fetchAllCharacters() {
        StringBuilder builder = new StringBuilder();
        for (int i = MIN; i <= MAX; i++) {
            builder.append(i);
            if (i < MAX) {
                builder.append(",");
            }
        }
        Transaction transaction = null;
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = BASE_URL + CHARACTER + "/" + builder;
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            HttpResponse<String> httpResponse = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            List<CharacterModelDto> characters = objectMapper
                    .readValue(httpResponse.body(), new TypeReference<>() {});
            for (CharacterModelDto character : characters) {
                session.persist(CharacterModelMapper.toCharacterModel(character));
            }
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
