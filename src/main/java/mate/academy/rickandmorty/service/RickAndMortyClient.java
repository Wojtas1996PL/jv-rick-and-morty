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
import java.util.Map;
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
    private static final int MIN = 1;
    private final SessionFactory sessionFactory;
    private final CharacterModelMapper characterModelMapper;
    private int count;

    public RickAndMortyClient(SessionFactory sessionFactory,
                              CharacterModelMapper characterModelMapper) {
        this.sessionFactory = sessionFactory;
        this.characterModelMapper = characterModelMapper;
    }

    public CharacterModelDto getRandomCharacter() {
        Transaction transaction = null;
        int random = (int) (Math.random() * count) + MIN;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<CharacterModel> query = session.createQuery("FROM User u "
                    + "WHERE u.externalId = :random", CharacterModel.class);
            query.setParameter("externalId", random);
            transaction.commit();
            return characterModelMapper.toDto(query.getSingleResult());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public List<CharacterModelDto> getCharactersListByNameSegment(String nameSegment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<CharacterModel> query = session.createQuery("FROM User u "
                    + "WHERE u.name = :%nameSegment%", CharacterModel.class);
            query.setParameter("%name%", nameSegment);
            transaction.commit();
            return query.getResultList()
                    .stream()
                    .map(characterModelMapper::toDto)
                    .toList();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public List<CharacterModelDto> fetchAllCharacters() {
        Transaction transaction = null;
        HttpClient httpClient = HttpClient.newHttpClient();
        String infoUrl = BASE_URL + CHARACTER;
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(infoUrl))
                .build();
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            HttpResponse<String> httpResponse = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> website = objectMapper
                    .readValue(httpResponse.body(), new TypeReference<>() {});
            String[] info = website.get("info").toString().split(",");
            for (String inf : info) {
                if (inf.contains("count")) {
                    count = Integer.parseInt(inf.substring(inf.indexOf("=") + 1));
                }
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= count; i++) {
                builder.append(i);
                if (i < count) {
                    builder.append(",");
                }
            }
            String url = BASE_URL + CHARACTER + "/" + builder;
            HttpRequest httpRequest2 = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> httpResponse2 = httpClient
                    .send(httpRequest2, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper2 = new ObjectMapper();
            List<CharacterModelDto> characters = objectMapper2
                    .readValue(httpResponse2.body(), new TypeReference<>() {});
            for (CharacterModelDto character : characters) {
                session.persist(characterModelMapper.toModel(character));
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
