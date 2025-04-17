package ckollmeier.de.asterixapi.dto;

import ckollmeier.de.asterixapi.model.Character;

public record CharacterInputDTO(
        String name,
        int age,
        String profession
) {
    public Character toCharacter() {
        return new Character(null, name, age, profession);
    }
}
