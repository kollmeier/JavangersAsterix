package ckollmeier.de.asterixapi.service;

import ckollmeier.de.asterixapi.converter.VillageConverter;
import ckollmeier.de.asterixapi.dataprovider.VillagesPageDataProvider;
import ckollmeier.de.asterixapi.dto.*; // Import all DTOs
import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.model.Character;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import ckollmeier.de.asterixapi.repository.VillageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link VillageService} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class)
class VillageServiceTest {

    // Mocks for dependencies
    @Mock
    private VillageRepository villageRepository;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private VillagesPageDataProvider villagePageDataProvider;
    @Mock
    private VillageConverter villageConverter;
    @Mock
    private IdService idService;

    // Inject mocks into the service
    @InjectMocks
    private VillageService villageService;

    // Argument Captors
    @Captor
    private ArgumentCaptor<Village> villageCaptor;
    @Captor
    private ArgumentCaptor<Character> characterCaptor;
    @Captor
    private ArgumentCaptor<List<Character>> characterListCaptor;


    // Test Data
    private Village testVillage1;
    private Village testVillage2;
    private Character testCharacter1;
    private Character testCharacter2;
    private Character testCharacter3; // Belongs to village 2 initially
    private VillageInputDTO testVillageInputDTO;
    private String testVillageId1;
    private String testVillageId2;
    private String testCharId1;
    private String testCharId2;
    private String testCharId3;


    @BeforeEach
    void setUp() {
        // Initialize test data
        testVillageId1 = UUID.randomUUID().toString();
        testVillageId2 = UUID.randomUUID().toString();
        testCharId1 = UUID.randomUUID().toString();
        testCharId2 = UUID.randomUUID().toString();
        testCharId3 = UUID.randomUUID().toString();

        // Villages (initially without inhabitants list populated for simplicity, repo mocks handle that)
        testVillage1 = new Village(testVillageId1, "Indomitable Village");
        testVillage2 = new Village(testVillageId2, "Aquarium");

        // Characters
        testCharacter1 = new Character(testCharId1, "Asterix", 35, "Warrior", testVillage1);
        testCharacter2 = new Character(testCharId2, "Obelix", 36, "Menhir Carver", testVillage1);
        testCharacter3 = new Character(testCharId3, "Geriatrix", 80, "Elder", testVillage2); // In village 2

        // Input DTO for adding/updating
        testVillageInputDTO = new VillageInputDTO("New Gaulish Village", List.of(testCharId1)); // Add with Asterix
    }

    @Test
    @DisplayName("getVillages should return list from repository")
    void getVillages_shouldReturnListFromRepository() {
        // Given
        List<Village> expectedVillages = List.of(testVillage1, testVillage2);
        when(villageRepository.findAll()).thenReturn(expectedVillages);

        // When
        List<Village> actualVillages = villageService.getVillages();

        // Then
        assertThat(actualVillages).isEqualTo(expectedVillages);
        verify(villageRepository, times(1)).findAll();
        verifyNoMoreInteractions(villageRepository);
    }

    @Test
    @DisplayName("getVillagesPageData should return DTO from data provider")
    void getVillagesPageData_shouldReturnDtoFromDataProvider() {
        // Given
        VillagesPageDTO expectedDto = getVillagesPageDTO();
        when(villagePageDataProvider.providePageData()).thenReturn(expectedDto);

        // When
        VillagesPageDTO actualDto = villageService.getVillagesPageData();

        // Then
        assertThat(actualDto).isEqualTo(expectedDto);
        verify(villagePageDataProvider, times(1)).providePageData();
        verifyNoMoreInteractions(villagePageDataProvider);
    }

    private VillagesPageDTO getVillagesPageDTO() {
        List<CharacterSelectDTO> characterSelects = List.of(new CharacterSelectDTO(testCharId1, "Asterix", testVillageId1, testVillage1.name()));
        // Output DTO needs inhabitants, let's mock a simple one
        List<MinimalCharacterOutputDTO> inhabitants = List.of(new MinimalCharacterOutputDTO(testCharId1, "Asterix", 35, "Warrior"));
        List<VillageOutputDTO> villageOutputs = List.of(new VillageOutputDTO(testVillageId1, testVillage1.name(), inhabitants));
        return new VillagesPageDTO(villageOutputs, characterSelects);
    }

    @Nested
    @DisplayName("getVillageByName Tests")
    class GetVillageByNameTests {
        @Test
        @DisplayName("should return village when found by name")
        void getVillageByName_shouldReturnVillage_whenFound() {
            // Given
            String name = testVillage1.name();
            when(villageRepository.findOneByName(name)).thenReturn(Optional.of(testVillage1));

            // When
            Optional<Village> actualVillageOpt = villageService.getVillageByName(name);

            // Then
            assertThat(actualVillageOpt).isPresent().contains(testVillage1);
            verify(villageRepository, times(1)).findOneByName(name);
        }

        @Test
        @DisplayName("should return empty Optional when not found by name")
        void getVillageByName_shouldReturnEmpty_whenNotFound() {
            // Given
            String name = "NonExistent";
            when(villageRepository.findOneByName(name)).thenReturn(Optional.empty());

            // When
            Optional<Village> actualVillageOpt = villageService.getVillageByName(name);

            // Then
            assertThat(actualVillageOpt).isEmpty();
            verify(villageRepository, times(1)).findOneByName(name);
        }
    }

    @Nested
    @DisplayName("getVillageById Tests")
    class GetVillageByIdTests {
        @Test
        @DisplayName("should return village when found by ID")
        void getVillageById_shouldReturnVillage_whenFound() {
            // Given
            when(villageRepository.findById(testVillageId1)).thenReturn(Optional.of(testVillage1));

            // When
            Optional<Village> actualVillageOpt = villageService.getVillageById(testVillageId1);

            // Then
            assertThat(actualVillageOpt).isPresent().contains(testVillage1);
            verify(villageRepository, times(1)).findById(testVillageId1);
        }

        @Test
        @DisplayName("should return empty Optional when not found by ID")
        void getVillageById_shouldReturnEmpty_whenNotFound() {
            // Given
            String nonExistentId = "non-existent-id";
            when(villageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When
            Optional<Village> actualVillageOpt = villageService.getVillageById(nonExistentId);

            // Then
            assertThat(actualVillageOpt).isEmpty();
            verify(villageRepository, times(1)).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("addVillage(VillageInputDTO) Tests")
    class AddVillageFromDTOTests {

        @Test
        @DisplayName("should add village and update characters")
        void addVillage_fromDto_shouldAddVillageAndUpdateCharacters() {
            // Given
            String generatedId = UUID.randomUUID().toString();
            Village villageFromConverter = new Village(null, testVillageInputDTO.name()); // ID null before save
            Village savedVillage = villageFromConverter.withId(generatedId); // Village after ID generation and save

            Character charToUpdate = new Character(testCharId1, "Asterix", 35, "Warrior", null); // Character before update
            Character updatedChar = charToUpdate.withVillage(savedVillage); // Character after update

            when(villageConverter.convert(testVillageInputDTO)).thenReturn(villageFromConverter);
            when(idService.generateId()).thenReturn(generatedId);
            when(villageRepository.save(any(Village.class))).thenReturn(savedVillage);
            when(characterRepository.findByIdIn(testVillageInputDTO.characterIds())).thenReturn(List.of(charToUpdate));
            // saveAll returns the saved entities, which should have the village set
            when(characterRepository.saveAll(anyList())).thenReturn(List.of(updatedChar));

            // When
            VillageOutputDTO actualDto = villageService.addVillage(testVillageInputDTO);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto.id()).isEqualTo(generatedId);
            assertThat(actualDto.name()).isEqualTo(testVillageInputDTO.name());
            assertThat(actualDto.characters()).hasSize(1);
            assertThat(actualDto.characters().getFirst().id()).isEqualTo(testCharId1);

            // Verify village saving
            verify(villageConverter, times(1)).convert(testVillageInputDTO);
            verify(idService, times(1)).generateId();
            verify(villageRepository, times(1)).save(villageCaptor.capture());
            assertThat(villageCaptor.getValue().id()).isEqualTo(generatedId); // Check ID was set before save
            assertThat(villageCaptor.getValue().name()).isEqualTo(testVillageInputDTO.name());

            // Verify character fetching and saving
            verify(characterRepository, times(1)).findByIdIn(testVillageInputDTO.characterIds());
            verify(characterRepository, times(1)).saveAll(characterListCaptor.capture());
            assertThat(characterListCaptor.getValue()).hasSize(1);
            assertThat(characterListCaptor.getValue().getFirst().id()).isEqualTo(testCharId1);
            // Crucially, check the village was assigned *before* saveAll
            assertThat(characterListCaptor.getValue().getFirst().village()).isEqualTo(savedVillage);
        }
    }


    @Nested
    @DisplayName("addVillage(Village) Tests")
    class AddVillageFromModelTests {
        @Test
        @DisplayName("should add village from Model when ID is null")
        void addVillage_fromModel_shouldAddVillage_whenIdIsNull() {
            // Given
            String generatedId = UUID.randomUUID().toString();
            Village villageToAdd = new Village(null, "Another Village"); // No ID
            Village expectedSavedVillage = villageToAdd.withId(generatedId);

            when(idService.generateId()).thenReturn(generatedId);
            when(villageRepository.save(any(Village.class))).thenReturn(expectedSavedVillage);

            // When
            Village actualSavedVillage = villageService.addVillage(villageToAdd);

            // Then
            assertThat(actualSavedVillage).isEqualTo(expectedSavedVillage);
            assertThat(actualSavedVillage.id()).isEqualTo(generatedId);

            verify(idService, times(1)).generateId();
            verify(villageRepository, times(1)).save(villageCaptor.capture());
            assertThat(villageCaptor.getValue().id()).isEqualTo(generatedId);
            assertThat(villageCaptor.getValue().name()).isEqualTo(villageToAdd.name());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when adding Village model with existing ID")
        void addVillage_fromModel_shouldThrowException_whenIdExists() {
            // Given
            Village villageWithId = testVillage1; // Already has an ID

            // When / Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> villageService.addVillage(villageWithId), "IllegalArgumentException should be thrown");

            assertThat(exception.getMessage()).isEqualTo("Village id is already set");

            // Then (verify no side effects)
            verifyNoInteractions(idService);
            verify(villageRepository, never()).save(any(Village.class));
        }
    }

    @Nested
    @DisplayName("removeVillage Tests")
    class RemoveVillageTests {
        @Test
        @DisplayName("should remove village and nullify character references")
        void removeVillage_shouldRemoveAndNullifyCharacters() {
            // Given
            List<Character> inhabitants = List.of(testCharacter1, testCharacter2);
            when(villageRepository.findById(testVillageId1)).thenReturn(Optional.of(testVillage1));
            when(characterRepository.findByVillageId(testVillageId1)).thenReturn(inhabitants);
            // Mock the save operation for each character being updated
            when(characterRepository.save(any(Character.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0)); // Return the character passed to save
            doNothing().when(villageRepository).delete(testVillage1);

            // When
            Village removedVillage = villageService.removeVillage(testVillageId1);

            // Then
            assertThat(removedVillage).isEqualTo(testVillage1);

            // Verify finding village and inhabitants
            verify(villageRepository, times(1)).findById(testVillageId1);
            verify(characterRepository, times(1)).findByVillageId(testVillageId1);

            // Verify each inhabitant's village was set to null and saved
            verify(characterRepository, times(inhabitants.size())).save(characterCaptor.capture());
            List<Character> savedCharacters = characterCaptor.getAllValues();
            assertThat(savedCharacters).hasSize(inhabitants.size());
            assertThat(savedCharacters).extracting(Character::village).containsOnlyNulls();
            assertThat(savedCharacters).extracting(Character::id).containsExactlyInAnyOrder(testCharId1, testCharId2);


            // Verify village deletion
            verify(villageRepository, times(1)).delete(testVillage1);
        }

        @Test
        @DisplayName("should throw NotFoundException when removing non-existent village")
        void removeVillage_shouldThrowNotFound_whenVillageDoesNotExist() {
            // Given
            String nonExistentId = "non-existent-id";
            when(villageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When / Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> villageService.removeVillage(nonExistentId), "NotFoundException should be thrown");

            // Then (verify exception and no side effects)
            assertThat(exception.getMessage()).contains(nonExistentId);
            verify(villageRepository, times(1)).findById(nonExistentId);
            verify(characterRepository, never()).findByVillageId(anyString());
            verify(characterRepository, never()).save(any(Character.class));
            verify(villageRepository, never()).delete(any(Village.class));
        }
    }

    @Nested
    @DisplayName("updateVillage Tests")
    class UpdateVillageTests {

        @Test
        @DisplayName("should update village name and change inhabitants")
        void updateVillage_shouldUpdateNameAndChangeInhabitants() {
            // Given
            String updatedName = "Renamed Village";
            // Input DTO: Rename village1, keep Asterix (char1), add Geriatrix (char3)
            VillageInputDTO updateDto = new VillageInputDTO(updatedName, List.of(testCharId1, testCharId3));

            List<Character> initialInhabitants = List.of(testCharacter1.withVillage(testVillage1), testCharacter2.withVillage(testVillage1)); // Asterix, Obelix
            List<Character> targetInhabitantsFromDto = List.of(testCharacter1.withVillage(testVillage1), testCharacter3.withVillage(testVillage1)); // Asterix, Geriatrix

            // Mock finding the village
            when(villageRepository.findById(testVillageId1)).thenReturn(Optional.of(testVillage1));
            // Mock finding *initial* inhabitants, then target
            //noinspection unchecked
            when(characterRepository.findByVillageId(testVillageId1)).thenReturn(
                    initialInhabitants,
                    targetInhabitantsFromDto);
            // Mock finding characters specified in the DTO
            when(characterRepository.findByIdIn(updateDto.characterIds())).thenReturn(targetInhabitantsFromDto);

            // Mock saving characters (both setting null and setting new village)
            when(characterRepository.save(any(Character.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Mock saving the updated village (only name should change based on getVillage helper)
            Village villageWithUpdatedName = testVillage1.withName(updatedName);
            when(villageRepository.save(any(Village.class))).thenReturn(villageWithUpdatedName);

            // When
            VillageOutputDTO actualDto = villageService.updateVillage(testVillageId1, updateDto);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto.id()).isEqualTo(testVillageId1);
            assertThat(actualDto.name()).isEqualTo(updatedName);
            assertThat(actualDto.characters()).hasSize(2);
            assertThat(actualDto.characters()).extracting(MinimalCharacterOutputDTO::id)
                    .containsExactlyInAnyOrder(testCharId1, testCharId3);

            // Verify finding village and initial inhabitants
            verify(villageRepository, times(1)).findById(testVillageId1);
            // findByVillageId is called twice: once initially, once for the final DTO conversion
            verify(characterRepository, times(2)).findByVillageId(testVillageId1);
            // Verify finding target inhabitants based on DTO IDs
            verify(characterRepository, times(1)).findByIdIn(updateDto.characterIds());

            // Verify character updates (save called for removed and added)
            // Obelix (char2) removed (village set to null), Geriatrix (char3) added (village set to existingVillage)
            verify(characterRepository, times(2)).save(characterCaptor.capture());
            List<Character> savedChars = characterCaptor.getAllValues();

            // Check Obelix village is null
            assertThat(savedChars).filteredOn(c -> c.id().equals(testCharId2))
                    .hasSize(1)
                    .extracting(Character::village)
                    .containsOnlyNulls();
            // Check Geriatrix village is testVillage1 (the existing village instance *before* name change)
            assertThat(savedChars).filteredOn(c -> c.id().equals(testCharId3))
                    .hasSize(1)
                    .extracting(Character::village)
                    .containsOnly(testVillage1); // Check against the original existingVillage instance


            // Verify village update (only name should be updated by getVillage helper)
            verify(villageRepository, times(1)).save(villageCaptor.capture());
            assertThat(villageCaptor.getValue().id()).isEqualTo(testVillageId1);
            assertThat(villageCaptor.getValue().name()).isEqualTo(updatedName); // Name updated
        }


        @Test
        @DisplayName("should update only village name when inhabitants list is null in DTO")
        void updateVillage_shouldUpdateOnlyName_whenInhabitantsNull() {
            // Given
            String updatedName = "Just Renamed Village";
            VillageInputDTO updateDto = new VillageInputDTO(updatedName, null); // Inhabitants null

            List<Character> initialInhabitants = List.of(testCharacter1, testCharacter2);

            when(villageRepository.findById(testVillageId1)).thenReturn(Optional.of(testVillage1));
            // findByVillageId called once for DTO conversion at the end
            when(characterRepository.findByVillageId(testVillageId1)).thenReturn(initialInhabitants);

            // Mock saving the updated village
            Village villageWithUpdatedName = testVillage1.withName(updatedName);
            when(villageRepository.save(any(Village.class))).thenReturn(villageWithUpdatedName);

            // When
            VillageOutputDTO actualDto = villageService.updateVillage(testVillageId1, updateDto);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto.id()).isEqualTo(testVillageId1);
            assertThat(actualDto.name()).isEqualTo(updatedName);
            // Inhabitants should reflect the original state as no changes were requested
            assertThat(actualDto.characters()).hasSize(2);
            assertThat(actualDto.characters()).extracting(MinimalCharacterOutputDTO::id)
                    .containsExactlyInAnyOrder(testCharId1, testCharId2);

            // Verify finding village
            verify(villageRepository, times(1)).findById(testVillageId1);
            // Verify finding inhabitants (only for final DTO conversion)
            verify(characterRepository, times(1)).findByVillageId(testVillageId1);
            // Verify NO character lookups or updates based on DTO IDs because DTO list was null
            verify(characterRepository, never()).findByIdIn(anyList());
            verify(characterRepository, never()).save(any(Character.class)); // No character saves

            // Verify village update
            verify(villageRepository, times(1)).save(villageCaptor.capture());
            assertThat(villageCaptor.getValue().id()).isEqualTo(testVillageId1);
            assertThat(villageCaptor.getValue().name()).isEqualTo(updatedName); // Name updated
        }


        @Test
        @DisplayName("should throw NotFoundException when updating non-existent village")
        void updateVillage_shouldThrowNotFound_whenVillageDoesNotExist() {
            // Given
            String nonExistentId = "non-existent-id";
            when(villageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When / Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> villageService.updateVillage(nonExistentId, testVillageInputDTO), "NotFoundException should be thrown");

            // Then (verify exception and no side effects)
            assertThat(exception.getMessage()).contains(nonExistentId);
            verify(villageRepository, times(1)).findById(nonExistentId);
            verify(characterRepository, never()).findByVillageId(anyString());
            verify(characterRepository, never()).findByIdIn(anyList());
            verify(characterRepository, never()).save(any(Character.class));
            verify(villageRepository, never()).save(any(Village.class));
        }
    }
}