package mate.academy.rickandmorty.mapper;

import mate.academy.rickandmorty.dto.CharacterModelDto;
import mate.academy.rickandmorty.model.CharacterModel;

public interface CharacterModelMapper {
    CharacterModelDto toDto(CharacterModel characterModel);

    CharacterModel toModel(CharacterModelDto characterModelDto);
}
