package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.dto.CharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.CharacterSelectDTO;
import ckollmeier.de.asterixapi.dto.MinimalVillageOutputDTO;
import ckollmeier.de.asterixapi.model.Character;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
 * Unit tests for {@link CharacterDataProvider} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class) // Initialize Mockito
class CharacterDataProviderTest {

    // Mock the dependency
    @Mock
    private CharacterRepository characterRepository;

    // Inject the mock into the class under test
    @InjectMocks
    private CharacterDataProvider characterDataProvider;

    // Test Data
    private Village testVillage1;
    private Village testVillage2;
    private Character testCharacter1; // With village1
    private Character testCharacter2; // With village2
    private Character testCharacter3; // Without village
    private String testVillageId1;
    private String testVillageId2;
    private String testCharId1;
    private String testCharId2;
    private String testCharId3;

    @BeforeEach
    void setUp() {
        // Common setup for test data
        testVillageId1 = UUID.randomUUID().toString();
        testVillageId2 = UUID.randomUUID().toString();
        testCharId1 = UUID.randomUUID().toString();
        testCharId2 = UUID.randomUUID().toString();
        testCharId3 = UUID.randomUUID().toString();

        testVillage1 = new Village(testVillageId1, "Indomitable Village");
        testVillage2 = new Village(testVillageId2, "Aquarium");

        testCharacter1 = new Character(testCharId1, "Asterix", 35, "Warrior", testVillage1);
        testCharacter2 = new Character(testCharId2, "Obelix", 36, "Menhir Carver", testVillage2);
        testCharacter3 = new Character(testCharId3, "Dogmatix", 5, "Dog", null); // No village
    }

    @Nested
    @DisplayName("provideListForSelect Tests")
    class ProvideListForSelectTests {

        @Test
        @DisplayName("should return list of CharacterSelectDTO when repository returns characters")
        void provideListForSelect_shouldReturnDtoList_whenCharactersExist() {
            // Given
            List<Character> charactersFromRepo = List.of(testCharacter1, testCharacter2, testCharacter3);
            when(characterRepository.findAll()).thenReturn(charactersFromRepo);

            List<CharacterSelectDTO> expectedDtoList = List.of(
                    new CharacterSelectDTO(testCharId1, "Asterix", "Indomitable Village", testVillageId1),
                    new CharacterSelectDTO(testCharId2, "Obelix", "Aquarium", testVillageId2),
                    new CharacterSelectDTO(testCharId3, "Dogmatix", null, null) // Expect nulls for village info
            );

            // When
            List<CharacterSelectDTO> actualDtoList = characterDataProvider.provideListForSelect();

            // Then
            assertThat(actualDtoList)
                    .isNotNull()
                    .hasSize(3)
                    .containsExactlyInAnyOrderElementsOf(expectedDtoList); // Use containsExactlyInAnyOrderElementsOf for list comparison

            verify(characterRepository, times(1)).findAll();
            verifyNoMoreInteractions(characterRepository);
        }

        @Test
        @DisplayName("should return empty list when repository returns no characters")
        void provideListForSelect_shouldReturnEmptyList_whenNoCharactersExist() {
            // Given
            when(characterRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<CharacterSelectDTO> actualDtoList = characterDataProvider.provideListForSelect();

            // Then
            assertThat(actualDtoList).isNotNull().isEmpty();
            verify(characterRepository, times(1)).findAll();
            verifyNoMoreInteractions(characterRepository);
        }
    }

    @Nested
    @DisplayName("provideListForOutput Tests")
    class ProvideListForOutputTests {

        @Test
        @DisplayName("should return list of CharacterOutputDTO when repository returns characters")
        void provideListForOutput_shouldReturnDtoList_whenCharactersExist() {
            // Given
            List<Character> charactersFromRepo = List.of(testCharacter1, testCharacter2, testCharacter3);
            when(characterRepository.findAll()).thenReturn(charactersFromRepo);

            // Define the expected output based on the static converter's logic
            // We assume the converter works correctly for this test.
            List<CharacterOutputDTO> expectedDtoList = List.of(
                    new CharacterOutputDTO(testCharId1, "Asterix", 35, "Warrior",
                            new MinimalVillageOutputDTO(testVillageId1, "Indomitable Village")),
                    new CharacterOutputDTO(testCharId2, "Obelix", 36, "Menhir Carver",
                            new MinimalVillageOutputDTO(testVillageId2, "Aquarium")),
                    new CharacterOutputDTO(testCharId3, "Dogmatix", 5, "Dog",
                            null) // Expect null for village DTO when character has no village
            );

            // When
            List<CharacterOutputDTO> actualDtoList = characterDataProvider.provideListForOutput();

            // Then
            // Verify the repository was called
            verify(characterRepository, times(1)).findAll();
            verifyNoMoreInteractions(characterRepository);

            // Assert the result matches the expected DTO list (implicitly tests data passed to converter)
            assertThat(actualDtoList)
                    .isNotNull()
                    .hasSize(3)
                    .usingRecursiveFieldByFieldElementComparator() // Good for comparing lists of complex objects
                    .containsExactlyInAnyOrderElementsOf(expectedDtoList);
        }

        @Test
        @DisplayName("should return empty list when repository returns no characters")
        void provideListForOutput_shouldReturnEmptyList_whenNoCharactersExist() {
            // Given
            when(characterRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<CharacterOutputDTO> actualDtoList = characterDataProvider.provideListForOutput();

            // Then
            assertThat(actualDtoList).isNotNull().isEmpty();
            verify(characterRepository, times(1)).findAll();
            verifyNoMoreInteractions(characterRepository);
        }
    }
}