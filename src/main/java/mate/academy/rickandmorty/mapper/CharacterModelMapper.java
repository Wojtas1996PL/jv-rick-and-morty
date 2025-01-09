package mate.academy.rickandmorty.mapper;

import mate.academy.rickandmorty.dto.CharacterModelDto;
import mate.academy.rickandmorty.model.CharacterModel;

public class CharacterModelMapper {
    public static CharacterModel toCharacterModel(CharacterModelDto characterModelDto) {
        CharacterModel characterModel = new CharacterModel();
        characterModel.setExternalId(characterModelDto.id());
        characterModel.setName(characterModelDto.name());
        characterModel.setGender(characterModelDto.gender());
        characterModel.setStatus(characterModelDto.status());
        return characterModel;
    }
}
