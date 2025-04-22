package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.converter.CharacterOutputDTOConverter; // Import the correct converter
import ckollmeier.de.asterixapi.dto.CharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.CharacterSelectDTO;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service component responsible for providing character data in specific DTO formats.
 * It acts as a data provider layer, fetching data using the {@link CharacterRepository}
 * and converting it into formats suitable for different use cases like dropdown selections
 * or detailed outputs using the {@link CharacterOutputDTOConverter}.
 */
@Service
@RequiredArgsConstructor
public class CharacterDataProvider {

    /**
     * Repository for accessing character data persistence.
     * Injected via constructor due to {@link RequiredArgsConstructor} and being final.
     */
    private final CharacterRepository characterRepository;

    /**
     * Provides a simplified list of characters suitable for selection interfaces (e.g., dropdowns).
     * Each character is represented by a {@link CharacterSelectDTO} containing only their ID and name.
     *
     * @return A {@link List} of {@link CharacterSelectDTO} objects representing all characters.
     *         Returns an empty list if no characters exist.
     */
    public List<CharacterSelectDTO> provideListForSelect() {
        return characterRepository.findAll().stream()
                .map(character -> new CharacterSelectDTO(character.id(),
                        character.name(),
                         character.village() != null ? character.village().name() : null,
                         character.village() != null ? character.village().id() : null))
                .toList();
    }

    /**
     * Provides a detailed list of characters suitable for output display.
     * Each character is represented by a {@link CharacterOutputDTO} containing their ID, name, age,
     * profession, and associated village information (converted to a minimal village DTO).
     * The conversion is handled by the injected {@link CharacterOutputDTOConverter}.
     *
     * @return A {@link List} of {@link CharacterOutputDTO} objects representing all characters with details.
     *         Returns an empty list if no characters exist.
     */
    public List<CharacterOutputDTO> provideListForOutput() {
        return CharacterOutputDTOConverter.convert(characterRepository.findAll());
    }
}