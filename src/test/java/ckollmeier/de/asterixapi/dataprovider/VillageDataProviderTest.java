package ckollmeier.de.asterixapi.dataprovider;

import ckollmeier.de.asterixapi.converter.CharacterOutputDTOConverter; // Needed for manual DTO creation in test
import ckollmeier.de.asterixapi.dto.MinimalCharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.VillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillageSelectDTO;
import ckollmeier.de.asterixapi.model.Character;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link VillageDataProvider} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class) // Initialize Mockito
class VillageDataProviderTest {

    // Mocks for the dependencies
    @Mock
    private VillageRepository villageRepository;
    @Mock
    private CharacterRepository characterRepository;

    // Inject mocks into the class under test
    @InjectMocks
    private VillageDataProvider villageDataProvider;

    // Test Data
    private Village testVillage1;
    private Village testVillage2;
    private Character testCharacter1; // Belongs to village1
    private Character testCharacter2; // Belongs to village1
    private Character testCharacter3; // Belongs to village2
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

        // Villages (inhabitants list is null, repo mocks handle relationships)
        testVillage1 = new Village(testVillageId1, "Indomitable Village");
        testVillage2 = new Village(testVillageId2, "Aquarium");

        // Characters linked to villages
        testCharacter1 = new Character(testCharId1, "Asterix", 35, "Warrior", testVillage1);
        testCharacter2 = new Character(testCharId2, "Obelix", 36, "Menhir Carver", testVillage1);
        testCharacter3 = new Character(testCharId3, "Geriatrix", 80, "Elder", testVillage2);
    }

    @Nested
    @DisplayName("provideListForSelect Tests")
    class ProvideListForSelectTests {

        @Test
        @DisplayName("should return list of VillageSelectDTO when repository returns villages")
        void provideListForSelect_shouldReturnDtoList_whenVillagesExist() {
            // Given
            List<Village> villagesFromRepo = List.of(testVillage1, testVillage2);
            when(villageRepository.findAll()).thenReturn(villagesFromRepo);

            List<VillageSelectDTO> expectedDtoList = List.of(
                    new VillageSelectDTO(testVillageId1, "Indomitable Village"),
                    new VillageSelectDTO(testVillageId2, "Aquarium")
            );

            // When
            List<VillageSelectDTO> actualDtoList = villageDataProvider.provideListForSelect();

            // Then
            assertThat(actualDtoList)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrderElementsOf(expectedDtoList); // Use containsExactlyInAnyOrderElementsOf for list comparison

            verify(villageRepository, times(1)).findAll();
            verifyNoMoreInteractions(villageRepository);
            verifyNoInteractions(characterRepository); // Should not be called for select
        }

        @Test
        @DisplayName("should return empty list when repository returns no villages")
        void provideListForSelect_shouldReturnEmptyList_whenNoVillagesExist() {
            // Given
            when(villageRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<VillageSelectDTO> actualDtoList = villageDataProvider.provideListForSelect();

            // Then
            assertThat(actualDtoList).isNotNull().isEmpty();
            verify(villageRepository, times(1)).findAll();
            verifyNoMoreInteractions(villageRepository);
            verifyNoInteractions(characterRepository); // Should not be called for select
        }
    }

    @Nested
    @DisplayName("provideListForOutput Tests")
    class ProvideListForOutputTests {

        @Test
        @DisplayName("should return list of VillageOutputDTO with inhabitants when repositories return data")
        void provideListForOutput_shouldReturnDtoListWithInhabitants_whenDataExists() {
            // Given
            List<Village> villagesFromRepo = List.of(testVillage1, testVillage2);
            List<Character> charactersFromRepo = List.of(testCharacter1, testCharacter2, testCharacter3); // All relevant characters

            when(villageRepository.findAll()).thenReturn(villagesFromRepo);
            // Mock the character repo to return characters when queried with the village list
            when(characterRepository.findByVillageIn(villagesFromRepo)).thenReturn(charactersFromRepo);

            // Manually construct the expected output based on the static converter's logic
            // Village 1 DTO
            List<MinimalCharacterOutputDTO> inhabitants1 = List.of(
                    CharacterOutputDTOConverter.convertMinimal(testCharacter1),
                    CharacterOutputDTOConverter.convertMinimal(testCharacter2)
            );
            VillageOutputDTO expectedDto1 = new VillageOutputDTO(testVillageId1, testVillage1.name(), inhabitants1);

            // Village 2 DTO
            List<MinimalCharacterOutputDTO> inhabitants2 = List.of(
                    CharacterOutputDTOConverter.convertMinimal(testCharacter3)
            );
            VillageOutputDTO expectedDto2 = new VillageOutputDTO(testVillageId2, testVillage2.name(), inhabitants2);

            List<VillageOutputDTO> expectedDtoList = List.of(expectedDto1, expectedDto2);

            // When
            List<VillageOutputDTO> actualDtoList = villageDataProvider.provideListForOutput();

            // Then
            // Verify repository interactions
            verify(villageRepository, times(1)).findAll();
            verify(characterRepository, times(1)).findByVillageIn(villagesFromRepo); // Verify correct argument
            verifyNoMoreInteractions(villageRepository, characterRepository);

            // Assert the result matches the expected DTO list
            assertThat(actualDtoList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator() // Good for comparing lists of complex objects
                    .containsExactlyInAnyOrderElementsOf(expectedDtoList);
        }

        @Test
        @DisplayName("should return list of VillageOutputDTO with empty inhabitants when character repo returns empty")
        void provideListForOutput_shouldReturnDtoListWithEmptyInhabitants_whenCharRepoIsEmpty() {
            // Given
            List<Village> villagesFromRepo = List.of(testVillage1, testVillage2);
            List<Character> charactersFromRepo = Collections.emptyList(); // No characters found

            when(villageRepository.findAll()).thenReturn(villagesFromRepo);
            when(characterRepository.findByVillageIn(villagesFromRepo)).thenReturn(charactersFromRepo);

            // Manually construct the expected output
            VillageOutputDTO expectedDto1 = new VillageOutputDTO(testVillageId1, testVillage1.name(), Collections.emptyList());
            VillageOutputDTO expectedDto2 = new VillageOutputDTO(testVillageId2, testVillage2.name(), Collections.emptyList());
            List<VillageOutputDTO> expectedDtoList = List.of(expectedDto1, expectedDto2);

            // When
            List<VillageOutputDTO> actualDtoList = villageDataProvider.provideListForOutput();

            // Then
            // Verify repository interactions
            verify(villageRepository, times(1)).findAll();
            verify(characterRepository, times(1)).findByVillageIn(villagesFromRepo);
            verifyNoMoreInteractions(villageRepository, characterRepository);

            // Assert the result matches the expected DTO list
            assertThat(actualDtoList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrderElementsOf(expectedDtoList);
        }

        @Test
        @DisplayName("should return empty list when village repository returns empty")
        void provideListForOutput_shouldReturnEmptyList_whenVillageRepoIsEmpty() {
            // Given
            List<Village> villagesFromRepo = Collections.emptyList();
            List<Character> charactersFromRepo = Collections.emptyList(); // Will also be empty

            when(villageRepository.findAll()).thenReturn(villagesFromRepo);
            // Mock findByVillageIn even with empty list input, should return empty
            when(characterRepository.findByVillageIn(villagesFromRepo)).thenReturn(charactersFromRepo);

            List<VillageOutputDTO> expectedDtoList = Collections.emptyList();

            // When
            List<VillageOutputDTO> actualDtoList = villageDataProvider.provideListForOutput();

            // Then
            // Verify repository interactions
            verify(villageRepository, times(1)).findAll();
            verify(characterRepository, times(1)).findByVillageIn(villagesFromRepo); // Called with empty list
            verifyNoMoreInteractions(villageRepository, characterRepository);

            // Assert the result is an empty list
            assertThat(actualDtoList).isNotNull().isEmpty();
        }
    }
}