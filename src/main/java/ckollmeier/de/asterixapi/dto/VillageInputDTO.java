package ckollmeier.de.asterixapi.dto;

import java.util.List;

public record VillageInputDTO(
        String name,
        List<String> characterIds
) {
}
