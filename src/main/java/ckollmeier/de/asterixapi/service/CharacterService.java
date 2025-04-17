package ckollmeier.de.asterixapi.service;

import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
// Unused import: import java.util.UUID; // Removed as it's not used

import ckollmeier.de.asterixapi.model.Character;

/**
 * Service layer for managing Asterix characters.
 * Handles business logic related to character retrieval and creation,
 * interacting with the {@link CharacterRepository} and {@link IdService}.
 */
@Service
@RequiredArgsConstructor
public class CharacterService {

    /**
     * Repository for accessing character data persistence.
     */
    private final CharacterRepository characterRepository;
    /**
     * Service used for generating unique IDs for new characters.
     */
    private final IdService idService;

    /**
     * Retrieves a list of all characters currently stored.
     *
     * @return A {@link List} containing all {@link Character} entities. Returns an empty list if none exist.
     */
    public List<Character> getCharacters() {
        return characterRepository.findAll();
    }

    /**
     * Finds a single character by their exact name.
     *
     * @param name The name of the character to search for.
     * @return An {@link Optional} containing the found {@link Character} if a match exists, otherwise an empty Optional.
     */
    public Optional<Character> getCharacterByName(final String name) {
        return characterRepository.findOneByName(name);
    }

    /**
     * Finds a single character by their unique identifier.
     *
     * @param id The unique ID of the character to search for.
     * @return An {@link Optional} containing the found {@link Character} if a match exists, otherwise an empty Optional.
     */
    public Optional<Character> getCharacterById(final String id) {
        return characterRepository.findById(id);
    }

    /**
     * Retrieves a list of all characters matching a specific profession.
     *
     * @param profession The profession to filter characters by.
     * @return A {@link List} of {@link Character} entities matching the profession. Returns an empty list if none found.
     */
    public List<Character> getCharactersByProfession(final String profession) {
        return characterRepository.findByProfession(profession);
    }

    /**
     * Retrieves a list of all characters whose age is greater than or equal to the specified age.
     *
     * @param age The minimum age (inclusive) to filter characters by.
     * @return A {@link List} of {@link Character} entities meeting the age criteria. Returns an empty list if none found.
     */
    public List<Character> getCharactersOlderThanOrEqual(final int age) {
        // Note: Repository method name implies >=, aligning with the parameter name 'age' as a minimum threshold.
        return characterRepository.findByAgeGreaterThanEqual(age);
    }

    /**
     * Creates and saves a new character based on the provided DTO data.
     * A unique ID is generated for the new character using the {@link IdService}.
     *
     * @param characterInputDTO The {@link CharacterInputDTO} containing the data for the new character.
     * @return The newly created and persisted {@link Character} entity, including its generated ID.
     */
    public Character addCharacter(final CharacterInputDTO characterInputDTO) {
        // Converts DTO to entity, generates a new ID, and saves it.
        return characterRepository.save(characterInputDTO.toCharacter().withId(idService.generateId()));
    }

    /**
     * Saves the given character entity after assigning it a newly generated unique ID.
     * <p>
     * This method first checks if the provided {@code character} object already has an ID.
     * If an ID is present, it throws an {@link IllegalArgumentException} to prevent accidental
     * overwriting or misuse when a new ID generation is intended.
     * If no ID is present, a new unique ID is generated using the {@link IdService} before saving.
     * </p>
     *
     * @param character The {@link Character} entity to save. Must not have its ID already set.
     * @return The persisted {@link Character} entity with its newly generated ID.
     * @throws IllegalArgumentException if the input {@code character} already has a non-null ID.
     */
    public Character addCharacter(final Character character) {
        if (character.id() != null) {
            throw new IllegalArgumentException("Character id is already set");
        }
        // Assigns a new ID and saves the character.
        return characterRepository.save(character.withId(idService.generateId()));
    }

    /**
     * Removes a character from the repository by their unique ID.
     * <p>
     * This method first attempts to find the character by the given ID. If the character is not found,
     * a {@link NotFoundException} is thrown. If found, the character is deleted from the repository.
     * </p>
     *
     * @param id The unique ID of the character to remove.
     * @return The {@link Character} entity that was removed.
     * @throws NotFoundException if no character with the given ID exists.
     */
    public Character removeCharacter(final String id) {
        final Character character = getCharacterById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Character with id '%s' not found", id)));
        characterRepository.delete(character);
        return character;
    }
}