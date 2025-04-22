package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.dto.CharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.CharactersPageDTO;
import ckollmeier.de.asterixapi.dto.MinimalVillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillageSelectDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CharactersPageDataProvider} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class) // Initialize Mockito
class CharactersPageDataProviderTest {

    // Mocks for the dependencies
    @Mock
    private VillageDataProvider villageDataProvider;
    @Mock
    private CharacterDataProvider characterDataProvider;

    // Inject mocks into the class under test
    @InjectMocks
    private CharactersPageDataProvider charactersPageDataProvider;

    @Test
    @DisplayName("providePageData should return DTO with data when both providers return data")
    void providePageData_shouldReturnDtoWithData_whenProvidersReturnData() {
        // Given
        // --- Data returned by VillageDataProvider mock ---
        String villageId1 = UUID.randomUUID().toString();
        List<VillageSelectDTO> expectedVillages = List.of(
                new VillageSelectDTO(villageId1, "Indomitable Village"), // Assuming constructor matches CharacterDataProvider test
                new VillageSelectDTO(UUID.randomUUID().toString(), "Aquarium")
        );
        when(villageDataProvider.provideListForSelect()).thenReturn(expectedVillages);

        // --- Data returned by CharacterDataProvider mock ---
        String charId1 = UUID.randomUUID().toString();
        List<CharacterOutputDTO> expectedCharacters = List.of(
                new CharacterOutputDTO(charId1, "Asterix", 35, "Warrior",
                        new MinimalVillageOutputDTO(villageId1, "Indomitable Village")),
                new CharacterOutputDTO(UUID.randomUUID().toString(), "Obelix", 36, "Menhir Carver",
                        new MinimalVillageOutputDTO(villageId1, "Indomitable Village"))
        );
        when(characterDataProvider.provideListForOutput()).thenReturn(expectedCharacters);

        // --- Expected final DTO ---
        CharactersPageDTO expectedDto = new CharactersPageDTO(expectedVillages, expectedCharacters);

        // When
        CharactersPageDTO actualDto = charactersPageDataProvider.providePageData();

        // Then
        // Verify that the correct methods were called on the mocks
        verify(villageDataProvider, times(1)).provideListForSelect();
        verify(characterDataProvider, times(1)).provideListForOutput();

        // Verify that no other methods were called on the mocks
        verifyNoMoreInteractions(villageDataProvider, characterDataProvider);

        // Assert that the returned DTO contains the expected data
        assertThat(actualDto).isNotNull();
        // Using recursive comparison is robust for DTOs
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        // Or assert individual lists if preferred
        // assertThat(actualDto.villagesForSelect()).isEqualTo(expectedVillages);
        // assertThat(actualDto.charactersForOutput()).isEqualTo(expectedCharacters);
    }

    @Test
    @DisplayName("providePageData should return DTO with empty lists when both providers return empty lists")
    void providePageData_shouldReturnDtoWithEmptyLists_whenProvidersReturnEmpty() {
        // Given
        // --- Data returned by mocks (empty lists) ---
        List<VillageSelectDTO> expectedVillages = Collections.emptyList();
        List<CharacterOutputDTO> expectedCharacters = Collections.emptyList();

        when(villageDataProvider.provideListForSelect()).thenReturn(expectedVillages);
        when(characterDataProvider.provideListForOutput()).thenReturn(expectedCharacters);

        // --- Expected final DTO ---
        CharactersPageDTO expectedDto = new CharactersPageDTO(expectedVillages, expectedCharacters);

        // When
        CharactersPageDTO actualDto = charactersPageDataProvider.providePageData();

        // Then
        // Verify that the correct methods were called on the mocks
        verify(villageDataProvider, times(1)).provideListForSelect();
        verify(characterDataProvider, times(1)).provideListForOutput();

        // Verify that no other methods were called on the mocks
        verifyNoMoreInteractions(villageDataProvider, characterDataProvider);

        // Assert that the returned DTO contains the expected empty lists
        assertThat(actualDto).isNotNull();
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        // Or assert individual lists
        // assertThat(actualDto.villagesForSelect()).isEmpty();
        // assertThat(actualDto.charactersForOutput()).isEmpty();
    }

    @Test
    @DisplayName("providePageData should return DTO with mixed empty/non-empty lists")
    void providePageData_shouldReturnDtoWithMixedLists_whenOneProviderReturnsEmpty() {
        // Given
        // --- Data returned by VillageDataProvider mock (empty) ---
        List<VillageSelectDTO> expectedVillages = Collections.emptyList();
        when(villageDataProvider.provideListForSelect()).thenReturn(expectedVillages);

        // --- Data returned by CharacterDataProvider mock (non-empty) ---
        String villageId1 = UUID.randomUUID().toString();
        String charId1 = UUID.randomUUID().toString();
        List<CharacterOutputDTO> expectedCharacters = List.of(
                new CharacterOutputDTO(charId1, "Asterix", 35, "Warrior",
                        new MinimalVillageOutputDTO(villageId1, "Indomitable Village"))
        );
        when(characterDataProvider.provideListForOutput()).thenReturn(expectedCharacters);

        // --- Expected final DTO ---
        CharactersPageDTO expectedDto = new CharactersPageDTO(expectedVillages, expectedCharacters);

        // When
        CharactersPageDTO actualDto = charactersPageDataProvider.providePageData();

        // Then
        // Verify that the correct methods were called on the mocks
        verify(villageDataProvider, times(1)).provideListForSelect();
        verify(characterDataProvider, times(1)).provideListForOutput();

        // Verify that no other methods were called on the mocks
        verifyNoMoreInteractions(villageDataProvider, characterDataProvider);

        // Assert that the returned DTO contains the expected lists
        assertThat(actualDto).isNotNull();
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        // Or assert individual lists
        // assertThat(actualDto.villagesForSelect()).isEmpty();
        // assertThat(actualDto.charactersForOutput()).isEqualTo(expectedCharacters);
    }
}