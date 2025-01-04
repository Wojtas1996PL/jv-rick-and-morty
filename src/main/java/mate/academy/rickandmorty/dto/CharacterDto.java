package mate.academy.rickandmorty.dto;

public record CharacterDto(Long id,
                           Long externalId,
                           String name,
                           String status,
                           String gender) {
}
