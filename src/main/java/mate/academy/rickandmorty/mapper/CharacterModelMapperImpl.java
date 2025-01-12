package mate.academy.rickandmorty.mapper;

import mate.academy.rickandmorty.dto.CharacterModelDto;
import mate.academy.rickandmorty.model.CharacterModel;
import org.springframework.stereotype.Component;

@Component
public class CharacterModelMapperImpl implements CharacterModelMapper {

    @Override
    public CharacterModelDto toDto(CharacterModel characterModel) {
        if (characterModel == null) {
            return null;
        }
        return new CharacterModelDto(characterModel.getExternalId(),
                characterModel.getName(),
                characterModel.getGender(),
                characterModel.getStatus());
    }

    @Override
    public CharacterModel toModel(CharacterModelDto characterModelDto) {
        if (characterModelDto == null) {
            return null;
        }
        CharacterModel characterModel = new CharacterModel();
        characterModel.setExternalId(characterModelDto.id());
        characterModel.setName(characterModelDto.name());
        characterModel.setGender(characterModelDto.gender());
        characterModel.setStatus(characterModelDto.status());
        return characterModel;
    }
}
