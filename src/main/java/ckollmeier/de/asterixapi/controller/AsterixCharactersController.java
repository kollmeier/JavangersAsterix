package ckollmeier.de.asterixapi.controller;

import ckollmeier.de.asterixapi.dataprovider.CharacterDataProvider;
import ckollmeier.de.asterixapi.dto.CharacterIdDTO;
import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
import ckollmeier.de.asterixapi.dto.CharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.CharactersPageDTO;
import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.service.CharacterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import ckollmeier.de.asterixapi.model.Character;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing Asterix characters.
 * Provides endpoints to retrieve, add, update, and delete characters,
 * often interacting with services, data providers, and converters.
 */
@RestController
@RequestMapping("/asterix/characters")
@RequiredArgsConstructor
public class AsterixCharactersController {

    /**
     * Service layer dependency for core character-related business logic
     * like creation, update, deletion, and specific lookups.
     */
    private final CharacterService characterService;
    /**
     * Data provider dependency for fetching character data formatted specifically
     * for output (e.g., lists with associated village details).
     */
    private final CharacterDataProvider characterDataProvider;
    /**
     * Retrieves a list of all Asterix characters formatted for output.
     * Corresponds to the GET request at "/asterix/characters".
     * Uses the {@link CharacterDataProvider} to get detailed DTOs.
     *
     * @return A {@link List} containing {@link CharacterOutputDTO} objects.
     */
    @GetMapping()
    public List<CharacterOutputDTO> getCharacters() {
        return characterDataProvider.provideListForOutput();
    }

    /**
     * Retrieves the aggregated data needed for displaying the characters page.
     * Corresponds to the GET request at "/asterix/characters/page-data".
     * This typically includes data for dropdowns (like villages) and the main character list.
     *
     * @return A {@link CharactersPageDTO} containing the necessary data for the page view.
     */
    @GetMapping("/page-data")
    public CharactersPageDTO getCharactersPageData() {
        return characterService.getCharactersPageData();
    }

    /**
     * Retrieves a specific character entity by their name.
     * Corresponds to the GET request at "/asterix/characters/{name}".
     * Note: Returns the raw {@link Character} model entity.
     *
     * @param name The name of the character to retrieve (passed as a path variable).
     * @return The {@link Character} entity matching the given name.
     * @throws NotFoundException if no character with the specified name is found.
     */
    @GetMapping("/{name}")
    public Character getCharacterByName(final @PathVariable String name) {
        return characterService.getCharacterByName(name)
                .orElseThrow(() -> new NotFoundException(String.format("Character with name '%s' not found", name)));
    }

    /**
     * Retrieves a specific character entity by their unique identifier.
     * Corresponds to the GET request at "/asterix/characters/id/{id}".
     * Note: Returns the raw {@link Character} model entity.
     *
     * @param id The unique ID of the character to retrieve (passed as a path variable).
     * @return The {@link Character} entity matching the given ID.
     * @throws NotFoundException if no character with the specified ID is found.
     */
    @GetMapping("/id/{id}")
    public Character getCharacterById(final @PathVariable String id) {
        return characterService.getCharacterById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Character with id '%s' not found", id)));
    }

    /**
     * Retrieves a list of character entities based on their profession.
     * Corresponds to the GET request at "/asterix/characters/profession/{profession}".
     * Note: Returns a list of raw {@link Character} model entities.
     *
     * @param profession The profession to filter characters by (passed as a path variable).
     * @return A {@link List} of {@link Character} entities matching the given profession. Returns an empty list if none found.
     */
    @GetMapping("/profession/{profession}")
    public List<Character> getCharactersByProfession(final @PathVariable String profession) {
        return characterService.getCharactersByProfession(profession);
    }

    /**
     * Retrieves a list of character entities older than or equal to a specified age.
     * Corresponds to the GET request at "/asterix/characters/minage/{age}".
     * Note: Returns a list of raw {@link Character} model entities.
     *
     * @param age The minimum age (inclusive) to filter characters by (passed as a path variable).
     * @return A {@link List} of {@link Character} entities older than or equal to the given age. Returns an empty list if none found.
     */
    @GetMapping("/minage/{age}")
    public List<Character> getCharactersOlderThanOrEqual(final @PathVariable Integer age) {
        return characterService.getCharactersOlderThanOrEqual(age);
    }

    /**
     * Adds a new character to the collection using data from the input DTO.
     * Corresponds to the POST request at "/asterix/characters/add".
     * Delegates the creation logic to the {@link CharacterService}.
     *
     * @param character The character data provided in the request body as a {@link CharacterInputDTO}.
     * @return The newly created {@link Character} entity, including its generated ID.
     */
    @PostMapping("/add")
    public Character addCharacter(final @RequestBody CharacterInputDTO character) {
        return characterService.addCharacter(character);
    }

    /**
     * Removes a character from the collection by its ID.
     * Corresponds to the DELETE request at "/asterix/characters/remove".
     * Expects a DTO containing the ID in the request body.
     * Delegates the removal logic to the {@link CharacterService}.
     *
     * @param character The {@link CharacterIdDTO} with the ID of the character to remove, provided in the request body.
     * @return The removed {@link Character} entity.
     * @throws NotFoundException if no character with the specified ID is found.
     */
    @DeleteMapping("/remove")
    public Character removeCharacter(final @RequestBody CharacterIdDTO character) {
        return characterService.removeCharacter(character.id());
    }

    /**
     * Updates an existing character in the collection by its ID.
     * Corresponds to the PUT request at "/asterix/characters/update/{id}".
     * Delegates the update logic to the {@link CharacterService}.
     *
     * @param id The ID of the character to update, provided as a path variable.
     * @param character The updated character data provided in the request body as a {@link CharacterInputDTO}.
     * @return The updated {@link Character} entity.
     * @throws NotFoundException if no character with the specified ID is found.
     */
    @PutMapping("/update/{id}")
    public Character updateCharacter(final @PathVariable String id, final @RequestBody CharacterInputDTO character) {
        return characterService.updateCharacter(id, character);
    }

}