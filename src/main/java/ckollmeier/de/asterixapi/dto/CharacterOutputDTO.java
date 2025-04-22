package ckollmeier.de.asterixapi.dto;

public record CharacterOutputDTO(
        String id,
        String name,
        int age,
        String profession,
        MinimalVillageOutputDTO village
) {
}
