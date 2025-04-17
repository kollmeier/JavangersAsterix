package ckollmeier.de.asterixapi.controller;

import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.service.CharacterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import ckollmeier.de.asterixapi.model.Character;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing Asterix characters.
 * Provides endpoints to retrieve and add characters.
 */
@RestController
@RequestMapping("/asterix")
@RequiredArgsConstructor
public class AsterixController {

    /**
     * Service layer dependency for character-related business logic.
     */
    private final CharacterService characterService;

    /**
     * Retrieves a list of all Asterix characters.
     * Corresponds to the GET request at "/asterix/characters".
     *
     * @return A {@link List} containing all {@link Character} objects.
     */
    @GetMapping("characters")
    public List<Character> getCharacters() {
        return characterService.getCharacters();
    }

    /**
     * Retrieves a specific character by their name.
     * Corresponds to the GET request at "/asterix/characters/{name}".
     *
     * @param name The name of the character to retrieve (passed as a path variable).
     * @return The {@link Character} object matching the given name.
     * @throws NotFoundException if no character with the specified name is found.
     */
    @GetMapping("characters/{name}")
    public Character getCharacterByName(final @PathVariable String name) {
        return characterService.getCharacterByName(name)
                .orElseThrow(() -> new NotFoundException(String.format("Character with name '%s' not found", name)));
    }

    /**
     * Retrieves a list of characters based on their profession.
     * Corresponds to the GET request at "/asterix/characters/profession/{profession}".
     *
     * @param profession The profession to filter characters by (passed as a path variable).
     * @return A {@link List} of {@link Character} objects matching the given profession. Returns an empty list if none found.
     */
    @GetMapping("characters/profession/{profession}")
    public List<Character> getCharactersByProfession(final @PathVariable String profession) {
        return characterService.getCharactersByProfession(profession);
    }

    /**
     * Retrieves a list of characters older than a specified age.
     * Corresponds to the GET request at "/asterix/characters/olderthan/{age}".
     *
     * @param age The minimum age (exclusive) to filter characters by (passed as a path variable).
     * @return A {@link List} of {@link Character} objects older than the given age. Returns an empty list if none found.
     */
    @GetMapping("characters/olderthan/{age}")
    public List<Character> getCharactersOlderThan(final @PathVariable Integer age) {
        return characterService.getCharactersOlderThan(age);
    }

    /**
     * Adds a new character to the collection.
     * Corresponds to the POST request at "/asterix/characters/add".
     *
     * @param character The character data provided in the request body as a {@link CharacterInputDTO}.
     * @return The newly created {@link Character} object, potentially including a generated ID or other persisted details.
     */
    @PostMapping("characters/add")
    public Character addCharacter(final @RequestBody CharacterInputDTO character) {
        return characterService.addCharacter(character);
    }
}