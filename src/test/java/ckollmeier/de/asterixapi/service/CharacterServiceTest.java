package ckollmeier.de.asterixapi.service;

import ckollmeier.de.asterixapi.converter.CharacterConverter;
import ckollmeier.de.asterixapi.dataprovider.CharactersPageDataProvider;
import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
import ckollmeier.de.asterixapi.dto.CharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.CharactersPageDTO;
import ckollmeier.de.asterixapi.dto.MinimalVillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillageSelectDTO;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CharacterService} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class) // Initialize Mockito
class CharacterServiceTest {

    // Mocks for all dependencies
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private VillageRepository villageRepository; // Added based on CharacterService code
    @Mock
    private CharactersPageDataProvider characterPageDataProvider;
    @Mock
    private IdService idService;
    @Mock
    private CharacterConverter characterConverter;

    // Inject mocks into the service instance
    @InjectMocks
    private CharacterService characterService;

    private Village testVillage;
    private Character testCharacter1;
    private Character testCharacter2;
    private CharacterInputDTO testCharacterInputDTO;
    private String testId1;
    private String testId2;
    private String testVillageId;

    @BeforeEach
    void setUp() {
        // Common setup for test data
        testId1 = UUID.randomUUID().toString();
        testId2 = UUID.randomUUID().toString();
        testVillageId = UUID.randomUUID().toString();

        testVillage = new Village(testVillageId, "Indomitable Village");
        testCharacter1 = new Character(testId1, "Asterix", 35, "Warrior", testVillage);
        testCharacter2 = new Character(testId2, "Obelix", 36, "Menhir Carver", testVillage);

        // Input DTO for adding/updating
        testCharacterInputDTO = new CharacterInputDTO("Getafix", 65, "Druid", testVillageId);
    }

    @Test
    @DisplayName("getCharacters should return list from repository")
    void shouldReturnAllCharacters() {
        // Given
        List<Character> expectedCharacters = List.of(testCharacter1, testCharacter2);
        when(characterRepository.findAll()).thenReturn(expectedCharacters);

        // When
        List<Character> actualCharacters = characterService.getCharacters();

        // Then
        assertThat(actualCharacters).isEqualTo(expectedCharacters);
        verify(characterRepository, times(1)).findAll();
        verifyNoMoreInteractions(characterRepository);
    }

    @Test
    @DisplayName("getCharactersPageData should return DTO from data provider")
    void shouldReturnCharactersPageData() {
        // Given
        List<VillageSelectDTO> villages = List.of(new VillageSelectDTO(testVillageId, testVillage.name()));
        List<CharacterOutputDTO> characters = List.of(
                new CharacterOutputDTO(testId1, "Asterix", 35, "Warrior", new MinimalVillageOutputDTO(testVillageId, testVillage.name()))
        );
        CharactersPageDTO expectedDto = new CharactersPageDTO(villages, characters);
        when(characterPageDataProvider.providePageData()).thenReturn(expectedDto);

        // When
        CharactersPageDTO actualDto = characterService.getCharactersPageData();

        // Then
        assertThat(actualDto).isEqualTo(expectedDto);
        verify(characterPageDataProvider, times(1)).providePageData();
        verifyNoMoreInteractions(characterPageDataProvider);
    }

    @Nested
    @DisplayName("getCharacterByName Tests")
    class GetCharacterByNameTests {
        @Test
        @DisplayName("should return character when found by name")
        void shouldReturnCharacterWhenFoundByName() {
            // Given
            String name = "Asterix";
            when(characterRepository.findOneByName(name)).thenReturn(Optional.of(testCharacter1));

            // When
            Optional<Character> actualCharacterOpt = characterService.getCharacterByName(name);

            // Then
            assertThat(actualCharacterOpt).isPresent().contains(testCharacter1);
            verify(characterRepository, times(1)).findOneByName(name);
        }

        @Test
        @DisplayName("should return empty Optional when not found by name")
        void shouldReturnEmptyOptionalWhenNotFoundByName() {
            // Given
            String name = "NonExistent";
            when(characterRepository.findOneByName(name)).thenReturn(Optional.empty());

            // When
            Optional<Character> actualCharacterOpt = characterService.getCharacterByName(name);

            // Then
            assertThat(actualCharacterOpt).isEmpty();
            verify(characterRepository, times(1)).findOneByName(name);
        }
    }

    @Nested
    @DisplayName("getCharacterById Tests")
    class GetCharacterByIdTests {
        @Test
        @DisplayName("should return character when found by ID")
        void shouldReturnCharacterWhenFoundById() {
            // Given
            when(characterRepository.findById(testId1)).thenReturn(Optional.of(testCharacter1));

            // When
            Optional<Character> actualCharacterOpt = characterService.getCharacterById(testId1);

            // Then
            assertThat(actualCharacterOpt).isPresent().contains(testCharacter1);
            verify(characterRepository, times(1)).findById(testId1);
        }

        @Test
        @DisplayName("should return empty Optional when not found by ID")
        void shouldReturnEmptyOptionalWhenNotFoundById() {
            // Given
            String nonExistentId = "non-existent-id";
            when(characterRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When
            Optional<Character> actualCharacterOpt = characterService.getCharacterById(nonExistentId);

            // Then
            assertThat(actualCharacterOpt).isEmpty();
            verify(characterRepository, times(1)).findById(nonExistentId);
        }
    }

    @Test
    @DisplayName("getCharactersByProfession should return list from repository")
    void shouldReturnCharactersByProfession() {
        // Given
        String profession = "Warrior";
        List<Character> expectedCharacters = List.of(testCharacter1);
        when(characterRepository.findByProfession(profession)).thenReturn(expectedCharacters);

        // When
        List<Character> actualCharacters = characterService.getCharactersByProfession(profession);

        // Then
        assertThat(actualCharacters).isEqualTo(expectedCharacters);
        verify(characterRepository, times(1)).findByProfession(profession);
    }

    @Test
    @DisplayName("getCharactersOlderThanOrEqual should return list from repository")
    void shouldReturnCharactersOlderThanOrEqual() {
        // Given
        int age = 36;
        List<Character> expectedCharacters = List.of(testCharacter2);
        when(characterRepository.findByAgeGreaterThanEqual(age)).thenReturn(expectedCharacters);

        // When
        List<Character> actualCharacters = characterService.getCharactersOlderThanOrEqual(age);

        // Then
        assertThat(actualCharacters).isEqualTo(expectedCharacters);
        verify(characterRepository, times(1)).findByAgeGreaterThanEqual(age);
    }

    @Nested
    @DisplayName("addCharacter Tests")
    class AddCharacterTests {

        @Test
        @DisplayName("should add character from DTO")
        void shouldAddCharacterFromDTO() {
            // Given
            String generatedId = UUID.randomUUID().toString();
            // Simulate converter result (ID is null before saving and ID generation)
            Character characterFromConverter = new Character(null, "Getafix", 65, "Druid", testVillage);
            Character expectedSavedCharacter = characterFromConverter.withId(generatedId); // Character after ID is assigned

            when(characterConverter.convert(testCharacterInputDTO)).thenReturn(characterFromConverter);
            when(idService.generateId()).thenReturn(generatedId);
            when(characterRepository.save(any(Character.class))).thenReturn(expectedSavedCharacter); // Mock save returns the final object

            // When
            Character actualSavedCharacter = characterService.addCharacter(testCharacterInputDTO);

            // Then
            assertThat(actualSavedCharacter).isEqualTo(expectedSavedCharacter);
            assertThat(actualSavedCharacter.id()).isEqualTo(generatedId); // Verify ID was set correctly

            // Verify interactions
            verify(characterConverter, times(1)).convert(testCharacterInputDTO);
            verify(idService, times(1)).generateId();
            ArgumentCaptor<Character> characterCaptor = ArgumentCaptor.forClass(Character.class);
            verify(characterRepository, times(1)).save(characterCaptor.capture());
            // Assert the state of the object *passed* to save()
            assertThat(characterCaptor.getValue().id()).isEqualTo(generatedId);
            assertThat(characterCaptor.getValue().name()).isEqualTo(testCharacterInputDTO.name());
            // Note: village comparison might need adjustment if converter logic changes
            assertThat(characterCaptor.getValue().village()).isEqualTo(characterFromConverter.village());
        }

        @Test
        @DisplayName("should add character from Model when ID is null")
        void shouldAddCharacterFromModel() {
            // Given
            String generatedId = UUID.randomUUID().toString();
            Character characterToAdd = new Character(null, "New Guy", 30, "Fisherman", testVillage); // No ID
            Character expectedSavedCharacter = characterToAdd.withId(generatedId);

            when(idService.generateId()).thenReturn(generatedId);
            when(characterRepository.save(any(Character.class))).thenReturn(expectedSavedCharacter);

            // When
            Character actualSavedCharacter = characterService.addCharacter(characterToAdd);

            // Then
            assertThat(actualSavedCharacter).isEqualTo(expectedSavedCharacter);
            assertThat(actualSavedCharacter.id()).isEqualTo(generatedId);

            verify(idService, times(1)).generateId();
            ArgumentCaptor<Character> characterCaptor = ArgumentCaptor.forClass(Character.class);
            verify(characterRepository, times(1)).save(characterCaptor.capture());
            assertThat(characterCaptor.getValue().id()).isEqualTo(generatedId);
            assertThat(characterCaptor.getValue().name()).isEqualTo(characterToAdd.name());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when adding Character model with existing ID")
        void shouldThrowExceptionWhenAddingModelWithId() {
            // Given
            Character characterWithId = testCharacter1; // Already has an ID

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> characterService.addCharacter(characterWithId), "IllegalArgumentException should be thrown");

            // Then (verify no side effects)
            verifyNoInteractions(idService);
            verify(characterRepository, never()).save(any(Character.class));
        }
    }

    @Nested
    @DisplayName("removeCharacter Tests")
    class RemoveCharacterTests {

        @Test
        @DisplayName("should remove character when found")
        void shouldRemoveCharacterWhenFound() {
            // Given
            when(characterRepository.findById(testId1)).thenReturn(Optional.of(testCharacter1));
            doNothing().when(characterRepository).delete(testCharacter1); // delete returns void

            // When
            Character removedCharacter = characterService.removeCharacter(testId1);

            // Then
            assertThat(removedCharacter).isEqualTo(testCharacter1);
            verify(characterRepository, times(1)).findById(testId1);
            verify(characterRepository, times(1)).delete(testCharacter1);
        }

        @Test
        @DisplayName("should throw NotFoundException when removing non-existent character")
        void shouldThrowNotFoundExceptionWhenRemovingNonExistentCharacter() {
            // Given
            String nonExistentId = "non-existent-id";
            when(characterRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When / Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> characterService.removeCharacter(nonExistentId), "NotFoundException should be thrown");

            // Then (verify exception details and no side effects)
            assertThat(exception.getMessage()).contains(nonExistentId);
            verify(characterRepository, times(1)).findById(nonExistentId);
            verify(characterRepository, never()).delete(any(Character.class));
        }
    }

    @Nested
    @DisplayName("updateCharacter Tests")
    class UpdateCharacterTests {

        @Test
        @DisplayName("should update character with all fields changed")
        void shouldUpdateCharacterAllFields() {
            // Given
            String newVillageId = UUID.randomUUID().toString();
            Village newVillage = new Village(newVillageId, "New Village");
            CharacterInputDTO updateDto = new CharacterInputDTO("Asterix Updated", 36, "Chief Warrior", newVillageId);
            // Expected character state *after* update logic and save
            Character expectedUpdatedCharacter = new Character(testId1, "Asterix Updated", 36, "Chief Warrior", newVillage);

            when(characterRepository.findById(testId1)).thenReturn(Optional.of(testCharacter1));
            when(villageRepository.findById(newVillageId)).thenReturn(Optional.of(newVillage)); // Mock village lookup
            when(characterRepository.save(any(Character.class))).thenReturn(expectedUpdatedCharacter); // Mock save returns final state

            // When
            Character actualUpdatedCharacter = characterService.updateCharacter(testId1, updateDto);

            // Then
            assertThat(actualUpdatedCharacter).isEqualTo(expectedUpdatedCharacter);

            // Verify interactions and capture saved entity
            verify(characterRepository, times(1)).findById(testId1);
            verify(villageRepository, times(1)).findById(newVillageId); // Verify village lookup
            ArgumentCaptor<Character> characterCaptor = ArgumentCaptor.forClass(Character.class);
            verify(characterRepository, times(1)).save(characterCaptor.capture());

            // Assert the state of the object *passed* to save()
            Character savedCharacter = characterCaptor.getValue();
            assertThat(savedCharacter.id()).isEqualTo(testId1); // ID should not change
            assertThat(savedCharacter.name()).isEqualTo(updateDto.name());
            assertThat(savedCharacter.age()).isEqualTo(updateDto.age());
            assertThat(savedCharacter.profession()).isEqualTo(updateDto.profession());
            assertThat(savedCharacter.village()).isEqualTo(newVillage); // Village should be updated
        }

        @Test
        @DisplayName("should update character with only name changed")
        void shouldUpdateCharacterOnlyName() {
            // Given
            CharacterInputDTO updateDto = new CharacterInputDTO("Asterix Renamed", 0, null, null); // Only name set, age=0, others null
            // Expected state after update logic and save
            Character expectedSavedCharacter = testCharacter1.withName("Asterix Renamed");

            when(characterRepository.findById(testId1)).thenReturn(Optional.of(testCharacter1));
            // villageRepository should not be called if villageId is null in DTO
            when(characterRepository.save(any(Character.class))).thenReturn(expectedSavedCharacter);

            // When
            Character actualUpdatedCharacter = characterService.updateCharacter(testId1, updateDto);

            // Then
            assertThat(actualUpdatedCharacter).isEqualTo(expectedSavedCharacter);

            verify(characterRepository, times(1)).findById(testId1);
            verifyNoInteractions(villageRepository); // Village ID was null, so no lookup
            ArgumentCaptor<Character> characterCaptor = ArgumentCaptor.forClass(Character.class);
            verify(characterRepository, times(1)).save(characterCaptor.capture());

            // Assert the state of the object *passed* to save()
            Character savedCharacter = characterCaptor.getValue();
            assertThat(savedCharacter.id()).isEqualTo(testId1);
            assertThat(savedCharacter.name()).isEqualTo(updateDto.name()); // Updated
            assertThat(savedCharacter.age()).isEqualTo(testCharacter1.age()); // Should be original (age 0 ignored)
            assertThat(savedCharacter.profession()).isEqualTo(testCharacter1.profession()); // Should be original (null ignored)
            assertThat(savedCharacter.village()).isEqualTo(testCharacter1.village()); // Should be original (null villageId ignored)
        }

        @Test
        @DisplayName("should update character with only village changed")
        void shouldUpdateCharacterOnlyVillage() {
            // Given
            String newVillageId = UUID.randomUUID().toString();
            Village newVillage = new Village(newVillageId, "New Village");
            CharacterInputDTO updateDto = new CharacterInputDTO(null, 0, null, newVillageId); // Only villageId set
            // Expected state after update logic and save
            Character expectedSavedCharacter = testCharacter1.withVillage(newVillage);

            when(characterRepository.findById(testId1)).thenReturn(Optional.of(testCharacter1));
            when(villageRepository.findById(newVillageId)).thenReturn(Optional.of(newVillage)); // Mock village lookup
            when(characterRepository.save(any(Character.class))).thenReturn(expectedSavedCharacter);

            // When
            Character actualUpdatedCharacter = characterService.updateCharacter(testId1, updateDto);

            // Then
            assertThat(actualUpdatedCharacter).isEqualTo(expectedSavedCharacter);

            verify(characterRepository, times(1)).findById(testId1);
            verify(villageRepository, times(1)).findById(newVillageId); // Verify village lookup
            ArgumentCaptor<Character> characterCaptor = ArgumentCaptor.forClass(Character.class);
            verify(characterRepository, times(1)).save(characterCaptor.capture());

            // Assert the state of the object *passed* to save()
            Character savedCharacter = characterCaptor.getValue();
            assertThat(savedCharacter.id()).isEqualTo(testId1);
            assertThat(savedCharacter.name()).isEqualTo(testCharacter1.name()); // Original
            assertThat(savedCharacter.age()).isEqualTo(testCharacter1.age()); // Original
            assertThat(savedCharacter.profession()).isEqualTo(testCharacter1.profession()); // Original
            assertThat(savedCharacter.village()).isEqualTo(newVillage); // New
        }

        @Test
        @DisplayName("should update character with null village when villageId not found")
        void shouldUpdateCharacterWithNullVillageWhenNotFound() {
            // Given
            String nonExistentVillageId = "non-existent-village-id";
            CharacterInputDTO updateDto = new CharacterInputDTO(null, 0, null, nonExistentVillageId);
            // Expected state after update logic (village becomes null) and save
            Character expectedSavedCharacter = testCharacter1.withVillage(null);

            when(characterRepository.findById(testId1)).thenReturn(Optional.of(testCharacter1));
            when(villageRepository.findById(nonExistentVillageId)).thenReturn(Optional.empty()); // Village not found
            when(characterRepository.save(any(Character.class))).thenReturn(expectedSavedCharacter);

            // When
            Character actualUpdatedCharacter = characterService.updateCharacter(testId1, updateDto);

            // Then
            assertThat(actualUpdatedCharacter).isEqualTo(expectedSavedCharacter);

            verify(characterRepository, times(1)).findById(testId1);
            verify(villageRepository, times(1)).findById(nonExistentVillageId); // Verify village lookup attempt
            ArgumentCaptor<Character> characterCaptor = ArgumentCaptor.forClass(Character.class);
            verify(characterRepository, times(1)).save(characterCaptor.capture());

            // Assert the state of the object *passed* to save()
            assertThat(characterCaptor.getValue().village()).isNull(); // Verify village is null
            assertThat(characterCaptor.getValue().name()).isEqualTo(testCharacter1.name()); // Verify others unchanged
        }


        @Test
        @DisplayName("should throw NotFoundException when updating non-existent character")
        void shouldThrowNotFoundExceptionWhenUpdatingNonExistentCharacter() {
            // Given
            String nonExistentId = "non-existent-id";
            when(characterRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When / Then
            NotFoundException exception = assertThrows(NotFoundException.class, () -> characterService.updateCharacter(nonExistentId, testCharacterInputDTO), "NotFoundException should be thrown");

            // Then (verify exception details and no side effects)
            assertThat(exception.getMessage()).contains(nonExistentId);
            verify(characterRepository, times(1)).findById(nonExistentId);
            verifyNoInteractions(villageRepository); // No village lookup if character not found first
            verify(characterRepository, never()).save(any(Character.class));
        }
    }
}