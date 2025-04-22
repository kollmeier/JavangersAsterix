package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.CharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.MinimalCharacterOutputDTO;
import ckollmeier.de.asterixapi.model.Character;

import java.util.List;

/**
 * A Spring service component responsible for converting {@link Character} model entities
 * into different character-related DTOs like {@link CharacterOutputDTO} and {@link MinimalCharacterOutputDTO}.
 * This is typically used to prepare character data for presentation layers or API responses,
 * exposing different levels of detail depending on the target DTO. It utilizes other converters
 * (like {@link VillageOutputDTOConverter}) to handle nested object conversions.
 */
public final class CharacterOutputDTOConverter {
    private CharacterOutputDTOConverter() {
        // Private constructor to prevent instantiation
        throw new IllegalStateException("Utility class");
    }
    /**
     * Converts a single {@link Character} entity into a detailed {@link CharacterOutputDTO}.
     * This DTO includes the character's ID, name, age, profession, and associated village information
     * converted into a minimal village DTO.
     * If the input character is {@code null}, this method will likely throw a {@link NullPointerException}.
     *
     * @param character The {@link Character} entity to convert. Should not be null.
     * @return A new {@link CharacterOutputDTO} containing detailed information including the minimally represented village.
     */
    public static CharacterOutputDTO convert(final Character character) {
        // Creates a DTO using character details and the minimally converted village DTO.
        return new CharacterOutputDTO(
                character.id(),
                character.name(),
                character.age(),
                character.profession(),
                // Convert the associated Village entity to its minimal DTO representation
                character.village() != null ? VillageOutputDTOConverter.convertMinimal(character.village()) : null
        );
    }

    /**
     * Converts a single {@link Character} entity into a minimal {@link MinimalCharacterOutputDTO}.
     * This DTO includes the character's ID, name, age, and profession, but omits village details.
     * If the input character is {@code null}, this method will likely throw a {@link NullPointerException}.
     *
     * @param character The {@link Character} entity to convert. Should not be null.
     * @return A new {@link MinimalCharacterOutputDTO} containing basic character information without the village.
     */
    public static MinimalCharacterOutputDTO convertMinimal(final Character character) {
        // Creates a minimal DTO using only the character's core attributes.
        return new MinimalCharacterOutputDTO(
                character.id(),
                character.name(),
                character.age(),
                character.profession()
        );
    }

    /**
     * Converts a list of {@link Character} entities into a list of detailed {@link CharacterOutputDTO} objects.
     * It iterates over the input list and applies the single-entity conversion method {@link #convert(Character)}
     * to each element.
     *
     * @param characters The list of {@link Character} entities to convert. Can be empty, but should not be null
     *                   if the stream operation is to be avoided on nulls.
     * @return A new {@link List} containing {@link CharacterOutputDTO} objects corresponding to the input characters.
     *         Returns an empty list if the input list is empty.
     */
    public static List<CharacterOutputDTO> convert(final List<Character> characters) {
        // Uses Java Streams to map each Character in the list to its detailed DTO representation.
        return characters.stream()
                .map(CharacterOutputDTOConverter::convert) // Calls the convert(Character) method for each element
                .toList(); // Collects the results into a new List
    }

    /**
     * Converts a list of {@link Character} entities into a list of minimal {@link MinimalCharacterOutputDTO} objects.
     * It iterates over the input list and applies the single-entity minimal conversion method {@link #convertMinimal(Character)}
     * to each element.
     *
     * @param characters The list of {@link Character} entities to convert. Can be empty, but should not be null
     *                   if the stream operation is to be avoided on nulls.
     * @return A new {@link List} containing {@link MinimalCharacterOutputDTO} objects corresponding to the input characters.
     *         Returns an empty list if the input list is empty.
     */
    public static List<MinimalCharacterOutputDTO> convertMinimal(final List<Character> characters) {
        // Uses Java Streams to map each Character in the list to its minimal DTO representation.
        return characters.stream()
                .map(CharacterOutputDTOConverter::convertMinimal) // Calls the convertMinimal(Character) method for each element
                .toList(); // Collects the results into a new List
    }
}