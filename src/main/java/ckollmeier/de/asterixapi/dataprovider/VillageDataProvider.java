package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.converter.VillageOutputDTOConverter; // Added this import as it seems intended for the output conversion
import ckollmeier.de.asterixapi.dto.VillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillageSelectDTO;
import ckollmeier.de.asterixapi.model.Character;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import ckollmeier.de.asterixapi.repository.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service component responsible for providing village data in specific DTO formats.
 * It acts as a data provider layer, fetching data using the {@link VillageRepository}
 * and converting it into formats suitable for different use cases like dropdown selections
 * or detailed outputs including inhabitants.
 */
@Service
@RequiredArgsConstructor
public final class VillageDataProvider { // Made class final as per original code

    /**
     * Repository for accessing village data persistence.
     * Injected via constructor due to {@link RequiredArgsConstructor} and being final.
     */
    private final VillageRepository villageRepository; // Made final to work correctly with @RequiredArgsConstructor

    /**
     * Repository for accessing character data persistence.
     * Injected via constructor due to {@link RequiredArgsConstructor} and being final.
     */
    private final CharacterRepository characterRepository; // Made final to work correctly with @RequiredArgsConstructor

    /**
     * Provides a simplified list of villages suitable for selection interfaces (e.g., dropdowns).
     * Each village is represented by a {@link VillageSelectDTO} containing only its ID and name.
     *
     * @return A {@link List} of {@link VillageSelectDTO} objects representing all villages.
     *         Returns an empty list if no villages exist.
     */
    public List<VillageSelectDTO> provideListForSelect() {
        return villageRepository.findAll().stream()
                .map(village -> new VillageSelectDTO(village.id(), village.name()))
                .toList(); // Switched to .toList()
    }

    /**
     * Provides a detailed list of villages suitable for output display.
     * Each village is represented by a {@link VillageOutputDTO} containing its ID, name,
     * and a list of its inhabitants converted into minimal character DTOs using the
     * injected {@link VillageOutputDTOConverter}.
     *
     * @return A {@link List} of {@link VillageOutputDTO} objects representing all villages with details.
     *         Returns an empty list if no villages exist.
     */
    public List<VillageOutputDTO> provideListForOutput() {
        List<Village> villages = villageRepository.findAll();
        List<Character> charactersInVillages = characterRepository.findByVillageIn(villages);
        return VillageOutputDTOConverter.convert(villages, charactersInVillages);
    }
}