package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.CharacterIdDTO;
import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
import ckollmeier.de.asterixapi.model.Character;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import ckollmeier.de.asterixapi.repository.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * A service component responsible for converting between different Character DTOs (Data Transfer Objects)
 * and the core {@link Character} model entity. It utilizes repositories to fetch related entities
 * like Villages or existing Characters during the conversion process.
 */
@Service
@RequiredArgsConstructor
public class CharacterConverter {

    /**
     * Repository for accessing village data, used to link a character to its village during conversion.
     */
    private final VillageRepository villageRepository;
    /**
     * Repository for accessing character data, used to find existing characters by ID during conversion.
     */
    private final CharacterRepository characterRepository;

    /**
     * Converts a {@link CharacterInputDTO} (typically used for creating new characters)
     * into a {@link Character} model entity.
     * <p>
     * The ID of the resulting Character is set to {@code null} as it's intended for creation.
     * It attempts to find the associated {@link ckollmeier.de.asterixapi.model.Village} using the
     * {@code villageId} from the DTO. If the village is not found, the village field in the
     * resulting Character entity will be {@code null}.
     * </p>
     *
     * @param characterInputDTO The input DTO containing character details and village ID. Must not be null.
     * @return A new {@link Character} entity populated with data from the DTO.
     */
    public Character convert(final CharacterInputDTO characterInputDTO) {
        return new Character(null, // ID is null for new character creation
                characterInputDTO.name(),
                characterInputDTO.age(),
                characterInputDTO.profession(),
                // Find the village by ID, return null if not found
                villageRepository.findById(characterInputDTO.villageId()).orElse(null));
    }

    /**
     * Converts a {@link CharacterIdDTO} (containing only an ID) into a full {@link Character} model entity
     * by fetching it from the repository.
     * <p>
     * If no character is found in the repository matching the ID from the DTO, this method returns {@code null}.
     * </p>
     *
     * @param characterIdDTO The input DTO containing the ID of the character to retrieve. Must not be null.
     * @return The found {@link Character} entity, or {@code null} if no character exists with the given ID.
     */
    public Character convert(final CharacterIdDTO characterIdDTO) {
        // Find the character by ID, return null if not found
        return characterRepository.findById(characterIdDTO.id()).orElse(null);
    }

}