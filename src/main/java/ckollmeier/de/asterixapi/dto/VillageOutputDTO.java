package ckollmeier.de.asterixapi.dto;

import java.util.List;

public record VillageOutputDTO(
        String id,
        String name,
        List<MinimalCharacterOutputDTO> characters
) {
}
