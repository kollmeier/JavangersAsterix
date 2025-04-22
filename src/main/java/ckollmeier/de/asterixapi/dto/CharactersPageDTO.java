package ckollmeier.de.asterixapi.dto;

import java.util.List;

public record CharactersPageDTO(
        List<VillageSelectDTO> villages,
        List<CharacterOutputDTO> characters
) {
}
