package ckollmeier.de.asterixapi.dto;

import java.util.List;

public record VillagesPageDTO(
        List<VillageOutputDTO> villages,
        List<CharacterSelectDTO> characters
) {
}
