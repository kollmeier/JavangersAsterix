package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.CharacterIdDTO;
import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CharacterConverter} using the Given-When-Then pattern.
 */
@ExtendWith(MockitoExtension.class) // Initialize Mockito
class CharacterConverterTest {

    // Mocks for the dependencies
    @Mock
    private VillageRepository villageRepository;
    @Mock
    private CharacterRepository characterRepository;

    // Inject mocks into the class under test
    @InjectMocks
    private CharacterConverter characterConverter;

    // Test Data - can be initialized here or within tests
    private String testVillageId;
    private Village testVillage;
    private String testCharacterId;
    private Character testCharacter;

    @BeforeEach
    void setUp() {
        testVillageId = UUID.randomUUID().toString();
        testVillage = new Village(testVillageId, "Indomitable Village");

        testCharacterId = UUID.randomUUID().toString();
        // Character model often used as expected result or mock return value
        testCharacter = new Character(testCharacterId, "Asterix", 35, "Warrior", testVillage);
    }

    @Nested
    @DisplayName("convert(CharacterInputDTO) Tests")
    class ConvertFromInputDTOTests {

        @Test
        @DisplayName("should convert CharacterInputDTO to Character when village exists")
        void convertInputDTO_shouldReturnCharacter_whenVillageExists() {
            // Given
            CharacterInputDTO inputDto = new CharacterInputDTO("Asterix", 35, "Warrior", testVillageId);
            // Mock the village repository to find the village
            when(villageRepository.findById(testVillageId)).thenReturn(Optional.of(testVillage));

            // Expected character (ID is null, village is the found one)
            Character expectedCharacter = new Character(null, "Asterix", 35, "Warrior", testVillage);

            // When
            Character actualCharacter = characterConverter.convert(inputDto);

            // Then
            // Verify villageRepository was called
            verify(villageRepository, times(1)).findById(testVillageId);
            verifyNoInteractions(characterRepository); // Character repo not used in this conversion

            // Assert the resulting character matches expectations
            assertThat(actualCharacter).isNotNull();
            // Use recursive comparison, but ignore the ID field as it's expected to be null
            assertThat(actualCharacter)
                    .usingRecursiveComparison()
                    .ignoringFields("id") // ID is null by design in this conversion
                    .isEqualTo(expectedCharacter);
            assertThat(actualCharacter.id()).isNull(); // Explicitly check ID is null
            assertThat(actualCharacter.village()).isEqualTo(testVillage); // Check village object
        }

        @Test
        @DisplayName("should convert CharacterInputDTO to Character with null village when village does not exist")
        void convertInputDTO_shouldReturnCharacterWithNullVillage_whenVillageNotFound() {
            // Given
            String nonExistentVillageId = UUID.randomUUID().toString();
            CharacterInputDTO inputDto = new CharacterInputDTO("Obelix", 36, "Menhir Carver", nonExistentVillageId);
            // Mock the village repository to return empty (village not found)
            when(villageRepository.findById(nonExistentVillageId)).thenReturn(Optional.empty());

            // Expected character (ID is null, village is null)
            Character expectedCharacter = new Character(null, "Obelix", 36, "Menhir Carver", null);

            // When
            Character actualCharacter = characterConverter.convert(inputDto);

            // Then
            // Verify villageRepository was called
            verify(villageRepository, times(1)).findById(nonExistentVillageId);
            verifyNoInteractions(characterRepository);

            // Assert the resulting character matches expectations
            assertThat(actualCharacter).isNotNull();
            assertThat(actualCharacter)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedCharacter);
            assertThat(actualCharacter.id()).isNull();
            assertThat(actualCharacter.village()).isNull(); // Explicitly check village is null
        }

        @Test
        @DisplayName("should convert CharacterInputDTO to Character with null village when villageId is null")
        void convertInputDTO_shouldReturnCharacterWithNullVillage_whenVillageIdIsNull() {
            // Given
            CharacterInputDTO inputDto = new CharacterInputDTO("Dogmatix", 5, "Dog", null); // Village ID is null
            // Mock the village repository to return empty when called with null
            when(villageRepository.findById(null)).thenReturn(Optional.empty());

            // Expected character (ID is null, village is null)
            Character expectedCharacter = new Character(null, "Dogmatix", 5, "Dog", null);

            // When
            Character actualCharacter = characterConverter.convert(inputDto);

            // Then
            // Verify villageRepository was called with null
            verify(villageRepository, times(1)).findById(null);
            verifyNoInteractions(characterRepository);

            // Assert the resulting character matches expectations
            assertThat(actualCharacter).isNotNull();
            assertThat(actualCharacter)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedCharacter);
            assertThat(actualCharacter.id()).isNull();
            assertThat(actualCharacter.village()).isNull();
        }
    }

    @Nested
    @DisplayName("convert(CharacterIdDTO) Tests")
    class ConvertFromIdDTOTests {

        @Test
        @DisplayName("should convert CharacterIdDTO to Character when character exists")
        void convertIdDTO_shouldReturnCharacter_whenCharacterExists() {
            // Given
            CharacterIdDTO idDto = new CharacterIdDTO(testCharacterId);
            // Mock the character repository to find the character
            when(characterRepository.findById(testCharacterId)).thenReturn(Optional.of(testCharacter));

            // When
            Character actualCharacter = characterConverter.convert(idDto);

            // Then
            // Verify characterRepository was called
            verify(characterRepository, times(1)).findById(testCharacterId);
            verifyNoInteractions(villageRepository); // Village repo not used in this conversion

            // Assert the returned character is the expected one
            assertThat(actualCharacter).isNotNull();
            assertThat(actualCharacter).isEqualTo(testCharacter);
        }

        @Test
        @DisplayName("should return null when converting CharacterIdDTO and character does not exist")
        void convertIdDTO_shouldReturnNull_whenCharacterNotFound() {
            // Given
            String nonExistentCharacterId = UUID.randomUUID().toString();
            CharacterIdDTO idDto = new CharacterIdDTO(nonExistentCharacterId);
            // Mock the character repository to return empty (character not found)
            when(characterRepository.findById(nonExistentCharacterId)).thenReturn(Optional.empty());

            // When
            Character actualCharacter = characterConverter.convert(idDto);

            // Then
            // Verify characterRepository was called
            verify(characterRepository, times(1)).findById(nonExistentCharacterId);
            verifyNoInteractions(villageRepository);

            // Assert the result is null
            assertThat(actualCharacter).isNull();
        }
    }
}
