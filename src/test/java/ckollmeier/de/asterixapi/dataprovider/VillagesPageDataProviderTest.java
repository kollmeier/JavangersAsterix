package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.dto.*; // Import necessary DTOs
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
 * Unit tests for {@link VillagesPageDataProvider} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class) // Initialize Mockito
class VillagesPageDataProviderTest {

    // Mocks for the dependencies
    @Mock
    private VillageDataProvider villageDataProvider;
    @Mock
    private CharacterDataProvider characterDataProvider;

    // Inject mocks into the class under test
    @InjectMocks
    private VillagesPageDataProvider villagesPageDataProvider;

    @Test
    @DisplayName("providePageData should return DTO with data when both providers return data")
    void providePageData_shouldReturnDtoWithData_whenProvidersReturnData() {
        // Given
        // --- Data returned by VillageDataProvider mock ---
        String villageId1 = UUID.randomUUID().toString();
        String charId1 = UUID.randomUUID().toString();
        List<MinimalCharacterOutputDTO> inhabitants1 = List.of(
                new MinimalCharacterOutputDTO(charId1, "Asterix", 35, "Warrior")
        );
        List<VillageOutputDTO> expectedVillages = List.of(
                new VillageOutputDTO(villageId1, "Indomitable Village", inhabitants1),
                new VillageOutputDTO(UUID.randomUUID().toString(), "Aquarium", Collections.emptyList())
        );
        when(villageDataProvider.provideListForOutput()).thenReturn(expectedVillages);

        // --- Data returned by CharacterDataProvider mock ---
        List<CharacterSelectDTO> expectedCharacters = List.of(
                new CharacterSelectDTO(charId1, "Asterix", "Indomitable Village", villageId1),
                new CharacterSelectDTO(UUID.randomUUID().toString(), "Obelix", "Indomitable Village", villageId1)
        );
        when(characterDataProvider.provideListForSelect()).thenReturn(expectedCharacters);

        // --- Expected final DTO ---
        VillagesPageDTO expectedDto = new VillagesPageDTO(expectedVillages, expectedCharacters);

        // When
        VillagesPageDTO actualDto = villagesPageDataProvider.providePageData();

        // Then
        // Verify that the correct methods were called on the mocks
        verify(villageDataProvider, times(1)).provideListForOutput();
        verify(characterDataProvider, times(1)).provideListForSelect();

        // Verify that no other methods were called on the mocks
        verifyNoMoreInteractions(villageDataProvider, characterDataProvider);

        // Assert that the returned DTO contains the expected data
        assertThat(actualDto).isNotNull();
        // Using recursive comparison is robust for DTOs
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        // Or assert individual lists if preferred
        // assertThat(actualDto.villagesForOutput()).isEqualTo(expectedVillages);
        // assertThat(actualDto.charactersForSelect()).isEqualTo(expectedCharacters);
    }

    @Test
    @DisplayName("providePageData should return DTO with empty lists when both providers return empty lists")
    void providePageData_shouldReturnDtoWithEmptyLists_whenProvidersReturnEmpty() {
        // Given
        // --- Data returned by mocks (empty lists) ---
        List<VillageOutputDTO> expectedVillages = Collections.emptyList();
        List<CharacterSelectDTO> expectedCharacters = Collections.emptyList();

        when(villageDataProvider.provideListForOutput()).thenReturn(expectedVillages);
        when(characterDataProvider.provideListForSelect()).thenReturn(expectedCharacters);

        // --- Expected final DTO ---
        VillagesPageDTO expectedDto = new VillagesPageDTO(expectedVillages, expectedCharacters);

        // When
        VillagesPageDTO actualDto = villagesPageDataProvider.providePageData();

        // Then
        // Verify that the correct methods were called on the mocks
        verify(villageDataProvider, times(1)).provideListForOutput();
        verify(characterDataProvider, times(1)).provideListForSelect();

        // Verify that no other methods were called on the mocks
        verifyNoMoreInteractions(villageDataProvider, characterDataProvider);

        // Assert that the returned DTO contains the expected empty lists
        assertThat(actualDto).isNotNull();
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        // Or assert individual lists
        // assertThat(actualDto.villagesForOutput()).isEmpty();
        // assertThat(actualDto.charactersForSelect()).isEmpty();
    }

    @Test
    @DisplayName("providePageData should return DTO with mixed empty/non-empty lists")
    void providePageData_shouldReturnDtoWithMixedLists_whenOneProviderReturnsEmpty() {
        // Given
        // --- Data returned by VillageDataProvider mock (non-empty) ---
        String villageId1 = UUID.randomUUID().toString();
        List<VillageOutputDTO> expectedVillages = List.of(
                new VillageOutputDTO(villageId1, "Indomitable Village", Collections.emptyList())
        );
        when(villageDataProvider.provideListForOutput()).thenReturn(expectedVillages);

        // --- Data returned by CharacterDataProvider mock (empty) ---
        List<CharacterSelectDTO> expectedCharacters = Collections.emptyList();
        when(characterDataProvider.provideListForSelect()).thenReturn(expectedCharacters);

        // --- Expected final DTO ---
        VillagesPageDTO expectedDto = new VillagesPageDTO(expectedVillages, expectedCharacters);

        // When
        VillagesPageDTO actualDto = villagesPageDataProvider.providePageData();

        // Then
        // Verify that the correct methods were called on the mocks
        verify(villageDataProvider, times(1)).provideListForOutput();
        verify(characterDataProvider, times(1)).provideListForSelect();

        // Verify that no other methods were called on the mocks
        verifyNoMoreInteractions(villageDataProvider, characterDataProvider);

        // Assert that the returned DTO contains the expected lists
        assertThat(actualDto).isNotNull();
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        // Or assert individual lists
        // assertThat(actualDto.villagesForOutput()).isEqualTo(expectedVillages);
        // assertThat(actualDto.charactersForSelect()).isEmpty();
    }
}