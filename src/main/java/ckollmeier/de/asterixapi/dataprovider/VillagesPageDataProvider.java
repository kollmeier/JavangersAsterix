package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.dto.VillagesPageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service component responsible for aggregating data needed for the villages page.
 * It combines data fetched from other data providers (like {@link VillageDataProvider}
 * and {@link CharacterDataProvider}) into a single {@link VillagesPageDTO}.
 */
@Service
@RequiredArgsConstructor
public class VillagesPageDataProvider {

    /**
     * Data provider for village-related data, specifically for selection lists.
     */
    private final VillageDataProvider villageDataProvider; // Made final as it's injected via @RequiredArgsConstructor
    /**
     * Data provider for character-related data, specifically for output lists.
     */
    private final CharacterDataProvider characterDataProvider; // Made final as it's injected via @RequiredArgsConstructor

    /**
     * Provides the aggregated data required for displaying the villages page.
     * This includes a list of villages suitable for selection and a detailed list
     * of characters for output.
     *
     * @return A {@link VillagesPageDTO} containing the necessary data lists
     *         (villages for selection, characters for output).
     */
    public VillagesPageDTO providePageData() {
        return new VillagesPageDTO(
                villageDataProvider.provideListForOutput(),
                characterDataProvider.provideListForSelect()
        );
    }
}