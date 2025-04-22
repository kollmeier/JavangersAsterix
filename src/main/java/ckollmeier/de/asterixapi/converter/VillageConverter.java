package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.VillageIdDTO;
import ckollmeier.de.asterixapi.dto.VillageInputDTO;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.repository.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * A service component responsible for converting between different Village DTOs (Data Transfer Objects)
 * and the core {@link Village} model entity. It utilizes repositories to fetch related entities
 * like Villages or existing Villages during the conversion process.
 */
@Service
@RequiredArgsConstructor
public class VillageConverter {

    /**
     * Repository for accessing village data, used to link a village to its village during conversion.
     */
    private final VillageRepository villageRepository;

    /**
     * Converts a {@link VillageInputDTO} (typically used for creating new villages)
     * into a {@link Village} model entity.
     * <p>
     * The ID of the resulting Village is set to {@code null} as it's intended for creation.
     * It attempts to find the associated {@link ckollmeier.de.asterixapi.model.Village} using the
     * {@code villageId} from the DTO. If the village is not found, the village field in the
     * resulting Village entity will be {@code null}.
     * </p>
     *
     * @param villageInputDTO The input DTO containing village details and village ID. Must not be null.
     * @return A new {@link Village} entity populated with data from the DTO.
     */
    public Village convert(final VillageInputDTO villageInputDTO) {
        return new Village(null, // ID is null for new village creation
                villageInputDTO.name()
                // Find the inhabitants by ID
//                characterRepository.findByIdIn(villageInputDTO.characterIds())
                );
    }

    /**
     * Converts a {@link VillageIdDTO} (containing only an ID) into a full {@link Village} model entity
     * by fetching it from the repository.
     * <p>
     * If no village is found in the repository matching the ID from the DTO, this method returns {@code null}.
     * </p>
     *
     * @param villageIdDTO The input DTO containing the ID of the village to retrieve. Must not be null.
     * @return The found {@link Village} entity, or {@code null} if no village exists with the given ID.
     */
    public Village convert(final VillageIdDTO villageIdDTO) {
        // Find the village by ID, return null if not found
        return villageRepository.findById(villageIdDTO.id()).orElse(null);
    }

}