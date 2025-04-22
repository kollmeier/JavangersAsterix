package ckollmeier.de.asterixapi.service;

import ckollmeier.de.asterixapi.converter.CharacterConverter;
import ckollmeier.de.asterixapi.dataprovider.CharactersPageDataProvider;
import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
import ckollmeier.de.asterixapi.dto.CharactersPageDTO;
import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import ckollmeier.de.asterixapi.repository.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import ckollmeier.de.asterixapi.model.Character;

/**
 * Service layer for managing Asterix characters.
 * Handles business logic related to character retrieval, creation, deletion, and updates,
 * interacting with the {@link CharacterRepository}, {@link IdService}, data providers,
 * and converters.
 */
@Service
@RequiredArgsConstructor
public class CharacterService {

    /**
     * Repository for accessing character data persistence.
     */
    private final CharacterRepository characterRepository;
    private final VillageRepository villageRepository;
    /**
     * Data provider for aggregating data needed for the characters page view.
     */
    private final CharactersPageDataProvider characterPageDataProvider;
    /**
     * Service used for generating unique IDs for new characters.
     */
    private final IdService idService;
    /**
     * Converter used to transform Character DTOs (e.g., {@link CharacterInputDTO})
     * into {@link Character} model entities.
     */
    private final CharacterConverter characterConverter;

    /**
     * Retrieves a list of all characters currently stored.
     * Note: This retrieves the raw {@link Character} entities. For DTOs suitable for output,
     * consider using methods from {@link ckollmeier.de.asterixapi.dataprovider.CharacterDataProvider}.
     *
     * @return A {@link List} containing all {@link Character} entities. Returns an empty list if none exist.
     */
    public List<Character> getCharacters() {
        return characterRepository.findAll();
    }

    /**
     * Retrieves the aggregated data required for displaying the characters page.
     * Delegates the data fetching and aggregation to the {@link CharactersPageDataProvider}.
     *
     * @return A {@link CharactersPageDTO} containing lists of villages for selection
     *         and characters for output display.
     */
    public CharactersPageDTO getCharactersPageData() {
        return characterPageDataProvider.providePageData();
    }

    /**
     * Finds a single character by their exact name.
     *
     * @param name The name of the character to search for.
     * @return An {@link Optional} containing the found {@link Character} if a match exists, otherwise an empty Optional.
     */
    public Optional<Character> getCharacterByName(final String name) {
        // Note: Repository method findOneByName might return null, which Optional.ofNullable handles correctly.
        // If the repository method itself returned Optional, this wrapping wouldn't be needed.
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
     * The character data is converted from the DTO to the entity model using the {@link CharacterConverter}.
     *
     * @param characterInputDTO The {@link CharacterInputDTO} containing the data for the new character.
     * @return The newly created and persisted {@link Character} entity, including its generated ID.
     */
    public Character addCharacter(final CharacterInputDTO characterInputDTO) {
        // Converts DTO to entity using the converter, generates a new ID, and saves it.
        return characterRepository.save(characterConverter.convert(characterInputDTO).withId(idService.generateId()));
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

    /**
     * Updates an existing character's information based on the provided data.
     * <p>
     * This method first retrieves the existing character by the given ID. If the character is not found,
     * a {@link NotFoundException} is thrown. If found, the character's properties (name, age, profession)
     * are updated based on the values provided in the {@link CharacterInputDTO} using the helper method
     * {@link #getCharacter(CharacterInputDTO, Character)}.
     * The character's ID remains unchanged. The village associated with the character is not updated by this method.
     * </p>
     *
     * @param id        The unique ID of the character to update.
     * @param character The {@link CharacterInputDTO} containing the updated data.
     * @return The updated and persisted {@link Character} entity.
     * @throws NotFoundException if no character with the given ID exists.
     */
    public Character updateCharacter(final String id, final CharacterInputDTO character) {
        // Retrieve the existing character or throw NotFoundException
        final Character existingCharacter = getCharacterById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Character with id '%s' not found", id)));

        // Use helper method to apply updates from DTO to existing entity
        Character characterToUpdate = getCharacter(character, existingCharacter);

        // Save the potentially modified character
        return characterRepository.save(characterToUpdate);
    }

    /**
     * Helper method to apply updates from a {@link CharacterInputDTO} to an existing {@link Character} entity.
     * <p>
     * It takes the existing character and creates a new instance with updated fields based on the non-null/non-zero
     * values from the input DTO. This uses the `with...` methods generated by Lombok (assuming `@Wither` or similar),
     * promoting immutability.
     * </p>
     *
     * @param character         The {@link CharacterInputDTO} containing the update data.
     * @param existingCharacter The current {@link Character} entity to be updated.
     * @return A new {@link Character} instance with the updates applied. The original ID and village are preserved.
     */
    private Character getCharacter(final CharacterInputDTO character, final Character existingCharacter) {
        Character characterToUpdate = existingCharacter;

        // Conditionally update fields based on the input DTO using 'with' methods for immutability
        if (character.name() != null) {
            characterToUpdate = characterToUpdate.withName(character.name());
        }
        // Use a check against 0 for age, assuming 0 is not a valid age and indicates no update
        if (character.age() != 0) {
            characterToUpdate = characterToUpdate.withAge(character.age());
        }
        if (character.profession() != null) {
            characterToUpdate = characterToUpdate.withProfession(character.profession());
        }
        if (character.villageId() != null) {
            characterToUpdate = characterToUpdate.withVillage(villageRepository.findById(character.villageId()).orElse(null));
        }
        // Note: Village ID from the DTO is not used here to update the village association.
        // The existing village association is preserved.
        return characterToUpdate;
    }
}