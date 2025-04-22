package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.CharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.MinimalCharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.MinimalVillageOutputDTO; // Needed for expected DTOs
import ckollmeier.de.asterixapi.model.Character;
import ckollmeier.de.asterixapi.model.Village;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link CharacterOutputDTOConverter} using the Given-When-Then pattern.
 * Since this is a utility class with static methods and no dependencies to mock,
 * we focus on verifying the output for given inputs.
 */
class CharacterOutputDTOConverterTest {

    // Test Data - Initialized in setUp
    private Village testVillage1;
    private Character testCharacter1; // With village
    private Character testCharacter2; // Without village
    private String testVillageId1;
    private String testCharId1;
    private String testCharId2;

    @BeforeEach
    void setUp() {
        // Common setup for test data
        testVillageId1 = UUID.randomUUID().toString();
        testCharId1 = UUID.randomUUID().toString();
        testCharId2 = UUID.randomUUID().toString();

        testVillage1 = new Village(testVillageId1, "Indomitable Village"); // Inhabitants not relevant for conversion itself

        testCharacter1 = new Character(testCharId1, "Asterix", 35, "Warrior", testVillage1);
        testCharacter2 = new Character(testCharId2, "Dogmatix", 5, "Dog", null); // No village
    }

    @Nested
    @DisplayName("convert(Character) Tests")
    class ConvertSingleCharacterTests {

        @Test
        @DisplayName("should convert Character with village to CharacterOutputDTO")
        void convert_shouldReturnDto_whenCharacterHasVillage() {
            // Given
            Character inputCharacter = testCharacter1;
            MinimalVillageOutputDTO expectedMinimalVillage = new MinimalVillageOutputDTO(testVillageId1, testVillage1.name());
            CharacterOutputDTO expectedDto = new CharacterOutputDTO(
                    testCharId1,
                    "Asterix",
                    35,
                    "Warrior",
                    expectedMinimalVillage
            );

            // When
            CharacterOutputDTO actualDto = CharacterOutputDTOConverter.convert(inputCharacter);

            // Then
            assertThat(actualDto).isNotNull();
            // Use recursive comparison for complex DTOs
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        }

        @Test
        @DisplayName("should convert Character without village to CharacterOutputDTO with null village")
        void convert_shouldReturnDtoWithNullVillage_whenCharacterHasNoVillage() {
            // Given
            Character inputCharacter = testCharacter2;
            CharacterOutputDTO expectedDto = new CharacterOutputDTO(
                    testCharId2,
                    "Dogmatix",
                    5,
                    "Dog",
                    null // Village DTO should be null
            );

            // When
            CharacterOutputDTO actualDto = CharacterOutputDTOConverter.convert(inputCharacter);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
            assertThat(actualDto.village()).isNull(); // Explicit check
        }
    }

    @Nested
    @DisplayName("convertMinimal(Character) Tests")
    class ConvertMinimalSingleCharacterTests {

        @Test
        @DisplayName("should convert Character with village to MinimalCharacterOutputDTO (ignoring village)")
        void convertMinimal_shouldReturnMinimalDto_whenCharacterHasVillage() {
            // Given
            Character inputCharacter = testCharacter1; // Has a village, but it should be ignored
            MinimalCharacterOutputDTO expectedDto = new MinimalCharacterOutputDTO(
                    testCharId1,
                    "Asterix",
                    35,
                    "Warrior"
            );

            // When
            MinimalCharacterOutputDTO actualDto = CharacterOutputDTOConverter.convertMinimal(inputCharacter);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        }

        @Test
        @DisplayName("should convert Character without village to MinimalCharacterOutputDTO")
        void convertMinimal_shouldReturnMinimalDto_whenCharacterHasNoVillage() {
            // Given
            Character inputCharacter = testCharacter2; // Has no village
            MinimalCharacterOutputDTO expectedDto = new MinimalCharacterOutputDTO(
                    testCharId2,
                    "Dogmatix",
                    5,
                    "Dog"
            );

            // When
            MinimalCharacterOutputDTO actualDto = CharacterOutputDTOConverter.convertMinimal(inputCharacter);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        }
    }

    @Nested
    @DisplayName("convert(List<Character>) Tests")
    class ConvertListTests {

        @Test
        @DisplayName("should convert list of Characters to list of CharacterOutputDTOs")
        void convertList_shouldReturnDtoList() {
            // Given
            List<Character> inputList = List.of(testCharacter1, testCharacter2);

            // Manually create expected DTOs based on single conversion logic
            MinimalVillageOutputDTO expectedMinimalVillage = new MinimalVillageOutputDTO(testVillageId1, testVillage1.name());
            CharacterOutputDTO expectedDto1 = new CharacterOutputDTO(testCharId1, "Asterix", 35, "Warrior", expectedMinimalVillage);
            CharacterOutputDTO expectedDto2 = new CharacterOutputDTO(testCharId2, "Dogmatix", 5, "Dog", null);
            List<CharacterOutputDTO> expectedList = List.of(expectedDto1, expectedDto2);

            // When
            List<CharacterOutputDTO> actualList = CharacterOutputDTOConverter.convert(inputList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator() // Compare elements deeply
                    .containsExactlyInAnyOrderElementsOf(expectedList);
        }

        @Test
        @DisplayName("should return empty list when converting an empty list of Characters")
        void convertList_shouldReturnEmptyList_whenInputIsEmpty() {
            // Given
            List<Character> inputList = Collections.emptyList();
            List<CharacterOutputDTO> expectedList = Collections.emptyList();

            // When
            List<CharacterOutputDTO> actualList = CharacterOutputDTOConverter.convert(inputList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .isEqualTo(expectedList)
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("convertMinimal(List<Character>) Tests")
    class ConvertMinimalListTests {

        @Test
        @DisplayName("should convert list of Characters to list of MinimalCharacterOutputDTOs")
        void convertMinimalList_shouldReturnMinimalDtoList() {
            // Given
            List<Character> inputList = List.of(testCharacter1, testCharacter2);

            // Manually create expected minimal DTOs
            MinimalCharacterOutputDTO expectedDto1 = new MinimalCharacterOutputDTO(testCharId1, "Asterix", 35, "Warrior");
            MinimalCharacterOutputDTO expectedDto2 = new MinimalCharacterOutputDTO(testCharId2, "Dogmatix", 5, "Dog");
            List<MinimalCharacterOutputDTO> expectedList = List.of(expectedDto1, expectedDto2);

            // When
            List<MinimalCharacterOutputDTO> actualList = CharacterOutputDTOConverter.convertMinimal(inputList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrderElementsOf(expectedList);
        }

        @Test
        @DisplayName("should return empty list when converting an empty list of Characters minimally")
        void convertMinimalList_shouldReturnEmptyList_whenInputIsEmpty() {
            // Given
            List<Character> inputList = Collections.emptyList();
            List<MinimalCharacterOutputDTO> expectedList = Collections.emptyList();

            // When
            List<MinimalCharacterOutputDTO> actualList = CharacterOutputDTOConverter.convertMinimal(inputList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .isEqualTo(expectedList)
                    .isEmpty();
        }
    }

    @Test
    @DisplayName("Constructor should throw IllegalStateException to prevent instantiation")
    void constructor_shouldThrowIllegalStateException() {
        // Given
        Constructor<CharacterOutputDTOConverter> constructor;
        try {
            constructor = CharacterOutputDTOConverter.class.getDeclaredConstructor();
            constructor.setAccessible(true); // Make private constructor accessible
        } catch (NoSuchMethodException e) {
            fail("Private constructor not found", e);
            return; // Keep compiler happy
        }

        // When / Then
        // Assert that calling the constructor throws an InvocationTargetException
        // whose cause is an IllegalStateException
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(IllegalStateException.class)
                .cause() // Get the cause (IllegalStateException)
                .hasMessage("Utility class"); // Check the message
    }
}