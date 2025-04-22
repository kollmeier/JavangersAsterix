package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.dto.CharactersPageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service component responsible for aggregating data needed for the characters page.
 * It combines data fetched from other data providers (like {@link VillageDataProvider}
 * and {@link CharacterDataProvider}) into a single {@link CharactersPageDTO}.
 */
@Service
@RequiredArgsConstructor
public class CharactersPageDataProvider {

    /**
     * Data provider for village-related data, specifically for selection lists.
     */
    private final VillageDataProvider villageDataProvider; // Made final as it's injected via @RequiredArgsConstructor
    /**
     * Data provider for character-related data, specifically for output lists.
     */
    private final CharacterDataProvider characterDataProvider; // Made final as it's injected via @RequiredArgsConstructor

    /**
     * Provides the aggregated data required for displaying the characters page.
     * This includes a list of villages suitable for selection and a detailed list
     * of characters for output.
     *
     * @return A {@link CharactersPageDTO} containing the necessary data lists
     *         (villages for selection, characters for output).
     */
    public CharactersPageDTO providePageData() {
        return new CharactersPageDTO(
                villageDataProvider.provideListForSelect(),
                characterDataProvider.provideListForOutput()
        );
    }
}