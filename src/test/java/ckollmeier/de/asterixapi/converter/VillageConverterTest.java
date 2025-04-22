package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.VillageIdDTO;
import ckollmeier.de.asterixapi.dto.VillageInputDTO;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import ckollmeier.de.asterixapi.repository.VillageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link VillageConverter} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class) // Initialize Mockito
class VillageConverterTest {

    // Mocks for the dependencies
    @Mock
    private VillageRepository villageRepository;
    @Mock
    private CharacterRepository characterRepository; // Mocked even if not used by all methods

    // Inject mocks into the class under test
    @InjectMocks
    private VillageConverter villageConverter;

    // Test Data
    private String testVillageId;
    private Village testVillage;

    @BeforeEach
    void setUp() {
        testVillageId = UUID.randomUUID().toString();
        // Village model often used as expected result or mock return value
        testVillage = new Village(testVillageId, "Indomitable Village");
    }

    @Nested
    @DisplayName("convert(VillageInputDTO) Tests")
    class ConvertFromInputDTOTests {

        @Test
        @DisplayName("should convert VillageInputDTO to Village with null ID and null inhabitants")
        void convertInputDTO_shouldReturnVillageWithNullIdAndInhabitants() {
            // Given
            String charId1 = UUID.randomUUID().toString();
            VillageInputDTO inputDto = new VillageInputDTO("New Village", List.of(charId1));

            // Expected character (ID is null, name from DTO, inhabitants null due to commented code)
            Village expectedVillage = new Village(null, "New Village");

            // When
            Village actualVillage = villageConverter.convert(inputDto);

            // Then
            // Verify no repository interactions occurred for this conversion
            verifyNoInteractions(villageRepository);
            verifyNoInteractions(characterRepository); // Important: Verify character repo wasn't called

            // Assert the resulting village matches expectations
            assertThat(actualVillage).isNotNull();
            // Use recursive comparison, ignoring ID and inhabitants (which are expected null)
            assertThat(actualVillage)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "inhabitants") // ID and inhabitants are null by design
                    .isEqualTo(expectedVillage);
            assertThat(actualVillage.id()).isNull(); // Explicitly check ID is null
        }
    }

    @Nested
    @DisplayName("convert(VillageIdDTO) Tests")
    class ConvertFromIdDTOTests {

        @Test
        @DisplayName("should convert VillageIdDTO to Village when village exists")
        void convertIdDTO_shouldReturnVillage_whenVillageExists() {
            // Given
            VillageIdDTO idDto = new VillageIdDTO(testVillageId);
            // Mock the village repository to find the village
            when(villageRepository.findById(testVillageId)).thenReturn(Optional.of(testVillage));

            // When
            Village actualVillage = villageConverter.convert(idDto);

            // Then
            // Verify villageRepository was called
            verify(villageRepository, times(1)).findById(testVillageId);
            verifyNoInteractions(characterRepository); // Character repo not used in this conversion

            // Assert the returned village is the expected one
            assertThat(actualVillage).isNotNull();
            assertThat(actualVillage).isEqualTo(testVillage);
        }

        @Test
        @DisplayName("should return null when converting VillageIdDTO and village does not exist")
        void convertIdDTO_shouldReturnNull_whenVillageNotFound() {
            // Given
            String nonExistentVillageId = UUID.randomUUID().toString();
            VillageIdDTO idDto = new VillageIdDTO(nonExistentVillageId);
            // Mock the village repository to return empty (village not found)
            when(villageRepository.findById(nonExistentVillageId)).thenReturn(Optional.empty());

            // When
            Village actualVillage = villageConverter.convert(idDto);

            // Then
            // Verify villageRepository was called
            verify(villageRepository, times(1)).findById(nonExistentVillageId);
            verifyNoInteractions(characterRepository);

            // Assert the result is null
            assertThat(actualVillage).isNull();
        }
    }
}