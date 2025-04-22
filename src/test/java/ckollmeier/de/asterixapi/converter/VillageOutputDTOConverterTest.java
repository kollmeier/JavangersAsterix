package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.MinimalCharacterOutputDTO;
import ckollmeier.de.asterixapi.dto.MinimalVillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillageOutputDTO;
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
 * Unit tests for {@link VillageOutputDTOConverter} using the Given-When-Then pattern.
 * Since this is a utility class with static methods and no dependencies to mock,
 * we focus on verifying the output for given inputs.
 */
class VillageOutputDTOConverterTest {

    // Test Data - Initialized in setUp
    private Village testVillage1;
    private Village testVillage2;
    private Character testCharacter1; // Belongs to village1
    private Character testCharacter2; // Belongs to village1
    private Character testCharacter3; // Belongs to village2
    private Character testCharacter4; // Belongs to null village
    private String testVillageId1;
    private String testVillageId2;
    private String testCharId1;
    private String testCharId2;
    private String testCharId3;
    private String testCharId4;

    @BeforeEach
    void setUp() {
        // Common setup for test data
        testVillageId1 = UUID.randomUUID().toString();
        testVillageId2 = UUID.randomUUID().toString();
        testCharId1 = UUID.randomUUID().toString();
        testCharId2 = UUID.randomUUID().toString();
        testCharId3 = UUID.randomUUID().toString();
        testCharId4 = UUID.randomUUID().toString();

        testVillage1 = new Village(testVillageId1, "Indomitable Village"); // Inhabitants list not directly used by converter logic
        testVillage2 = new Village(testVillageId2, "Aquarium");

        testCharacter1 = new Character(testCharId1, "Asterix", 35, "Warrior", testVillage1);
        testCharacter2 = new Character(testCharId2, "Obelix", 36, "Menhir Carver", testVillage1);
        testCharacter3 = new Character(testCharId3, "Geriatrix", 80, "Elder", testVillage2);
        testCharacter4 = new Character(testCharId4, "Justforkix", 20, "Visitor", null); // No village
    }

    @Nested
    @DisplayName("convert(Village) Tests - No Inhabitants")
    class ConvertSingleVillageNoInhabitantsTests {

        @Test
        @DisplayName("should convert Village to VillageOutputDTO with null inhabitants")
        void convert_shouldReturnDtoWithNullInhabitants() {
            // Given
            Village inputVillage = testVillage1;
            VillageOutputDTO expectedDto = new VillageOutputDTO(
                    testVillageId1,
                    "Indomitable Village",
                    null // Explicitly expect null inhabitants
            );

            // When
            VillageOutputDTO actualDto = VillageOutputDTOConverter.convert(inputVillage);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
            assertThat(actualDto.characters()).isNull(); // Double-check inhabitants are null
        }
    }

    @Nested
    @DisplayName("convert(Village, List<Character>) Tests - With Inhabitants")
    class ConvertSingleVillageWithInhabitantsTests {

        @Test
        @DisplayName("should convert Village and filter characters to VillageOutputDTO with correct inhabitants")
        void convert_withChars_shouldReturnDtoWithFilteredInhabitants() {
            // Given
            Village inputVillage = testVillage1;
            // Provide a list containing characters from multiple villages and null village
            List<Character> allCharacters = List.of(testCharacter1, testCharacter2, testCharacter3, testCharacter4);

            // Expected inhabitants are only those belonging to inputVillage, converted minimally
            List<MinimalCharacterOutputDTO> expectedInhabitants = List.of(
                    CharacterOutputDTOConverter.convertMinimal(testCharacter1),
                    CharacterOutputDTOConverter.convertMinimal(testCharacter2)
            );
            VillageOutputDTO expectedDto = new VillageOutputDTO(
                    testVillageId1,
                    "Indomitable Village",
                    expectedInhabitants
            );

            // When
            VillageOutputDTO actualDto = VillageOutputDTOConverter.convert(inputVillage, allCharacters);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
            assertThat(actualDto.characters())
                    .hasSize(2)
                    .extracting(MinimalCharacterOutputDTO::id)
                    .containsExactlyInAnyOrder(testCharId1, testCharId2);
        }

        @Test
        @DisplayName("should return VillageOutputDTO with empty inhabitants when no characters match")
        void convert_withChars_shouldReturnDtoWithEmptyInhabitants_whenNoMatch() {
            // Given
            Village inputVillage = testVillage1;
            // Provide a list containing characters only from other villages or null village
            List<Character> otherCharacters = List.of(testCharacter3, testCharacter4);

            VillageOutputDTO expectedDto = new VillageOutputDTO(
                    testVillageId1,
                    "Indomitable Village",
                    Collections.emptyList() // Expect empty inhabitants
            );

            // When
            VillageOutputDTO actualDto = VillageOutputDTOConverter.convert(inputVillage, otherCharacters);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
            assertThat(actualDto.characters()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return VillageOutputDTO with empty inhabitants when character list is empty")
        void convert_withChars_shouldReturnDtoWithEmptyInhabitants_whenCharListIsEmpty() {
            // Given
            Village inputVillage = testVillage1;
            List<Character> emptyCharacterList = Collections.emptyList();

            VillageOutputDTO expectedDto = new VillageOutputDTO(
                    testVillageId1,
                    "Indomitable Village",
                    Collections.emptyList() // Expect empty inhabitants
            );

            // When
            VillageOutputDTO actualDto = VillageOutputDTOConverter.convert(inputVillage, emptyCharacterList);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
            assertThat(actualDto.characters()).isNotNull().isEmpty();
        }
    }

    @Nested
    @DisplayName("convertMinimal(Village) Tests")
    class ConvertMinimalSingleVillageTests {

        @Test
        @DisplayName("should convert Village to MinimalVillageOutputDTO")
        void convertMinimal_shouldReturnMinimalDto() {
            // Given
            Village inputVillage = testVillage1;
            MinimalVillageOutputDTO expectedDto = new MinimalVillageOutputDTO(
                    testVillageId1,
                    "Indomitable Village"
            );

            // When
            MinimalVillageOutputDTO actualDto = VillageOutputDTOConverter.convertMinimal(inputVillage);

            // Then
            assertThat(actualDto).isNotNull();
            assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
        }
    }

    @Nested
    @DisplayName("convert(List<Village>) Tests - No Inhabitants")
    class ConvertListNoInhabitantsTests {

        @Test
        @DisplayName("should convert list of Villages to list of VillageOutputDTOs with null inhabitants")
        void convertList_shouldReturnDtoListWithNullInhabitants() {
            // Given
            List<Village> inputList = List.of(testVillage1, testVillage2);

            // Manually create expected DTOs
            VillageOutputDTO expectedDto1 = new VillageOutputDTO(testVillageId1, "Indomitable Village", null);
            VillageOutputDTO expectedDto2 = new VillageOutputDTO(testVillageId2, "Aquarium", null);
            List<VillageOutputDTO> expectedList = List.of(expectedDto1, expectedDto2);

            // When
            List<VillageOutputDTO> actualList = VillageOutputDTOConverter.convert(inputList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrderElementsOf(expectedList);
            // Check inhabitants are null in all elements
            assertThat(actualList).allSatisfy(dto -> assertThat(dto.characters()).isNull());
        }

        @Test
        @DisplayName("should return empty list when converting empty list of Villages")
        void convertList_shouldReturnEmptyList_whenInputIsEmpty() {
            // Given
            List<Village> inputList = Collections.emptyList();
            List<VillageOutputDTO> expectedList = Collections.emptyList();

            // When
            List<VillageOutputDTO> actualList = VillageOutputDTOConverter.convert(inputList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .isEqualTo(expectedList)
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("convert(List<Village>, List<Character>) Tests - With Inhabitants")
    class ConvertListWithInhabitantsTests {

        @Test
        @DisplayName("should convert list of Villages and filter characters to list of VillageOutputDTOs")
        void convertList_withChars_shouldReturnDtoListWithFilteredInhabitants() {
            // Given
            List<Village> inputVillages = List.of(testVillage1, testVillage2);
            List<Character> allCharacters = List.of(testCharacter1, testCharacter2, testCharacter3, testCharacter4);

            // Manually create expected DTOs with correct inhabitants
            List<MinimalCharacterOutputDTO> inhabitants1 = List.of(
                    CharacterOutputDTOConverter.convertMinimal(testCharacter1),
                    CharacterOutputDTOConverter.convertMinimal(testCharacter2)
            );
            VillageOutputDTO expectedDto1 = new VillageOutputDTO(testVillageId1, "Indomitable Village", inhabitants1);

            List<MinimalCharacterOutputDTO> inhabitants2 = List.of(
                    CharacterOutputDTOConverter.convertMinimal(testCharacter3)
            );
            VillageOutputDTO expectedDto2 = new VillageOutputDTO(testVillageId2, "Aquarium", inhabitants2);

            List<VillageOutputDTO> expectedList = List.of(expectedDto1, expectedDto2);

            // When
            List<VillageOutputDTO> actualList = VillageOutputDTOConverter.convert(inputVillages, allCharacters);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrderElementsOf(expectedList);
        }

        @Test
        @DisplayName("should return list of VillageOutputDTOs with empty inhabitants when character list is empty")
        void convertList_withChars_shouldReturnDtoListWithEmptyInhabitants_whenCharListIsEmpty() {
            // Given
            List<Village> inputVillages = List.of(testVillage1, testVillage2);
            List<Character> emptyCharacterList = Collections.emptyList();

            // Manually create expected DTOs with empty inhabitants
            VillageOutputDTO expectedDto1 = new VillageOutputDTO(testVillageId1, "Indomitable Village", Collections.emptyList());
            VillageOutputDTO expectedDto2 = new VillageOutputDTO(testVillageId2, "Aquarium", Collections.emptyList());
            List<VillageOutputDTO> expectedList = List.of(expectedDto1, expectedDto2);

            // When
            List<VillageOutputDTO> actualList = VillageOutputDTOConverter.convert(inputVillages, emptyCharacterList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrderElementsOf(expectedList);
            assertThat(actualList).allSatisfy(dto -> assertThat(dto.characters()).isNotNull().isEmpty());
        }

        @Test
        @DisplayName("should return empty list when converting empty list of Villages with characters")
        void convertList_withChars_shouldReturnEmptyList_whenVillageListIsEmpty() {
            // Given
            List<Village> emptyVillageList = Collections.emptyList();
            List<Character> allCharacters = List.of(testCharacter1, testCharacter2, testCharacter3, testCharacter4);
            List<VillageOutputDTO> expectedList = Collections.emptyList();

            // When
            List<VillageOutputDTO> actualList = VillageOutputDTOConverter.convert(emptyVillageList, allCharacters);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .isEqualTo(expectedList)
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("convertMinimal(List<Village>) Tests")
    class ConvertMinimalListTests {

        @Test
        @DisplayName("should convert list of Villages to list of MinimalVillageOutputDTOs")
        void convertMinimalList_shouldReturnMinimalDtoList() {
            // Given
            List<Village> inputList = List.of(testVillage1, testVillage2);

            // Manually create expected minimal DTOs
            MinimalVillageOutputDTO expectedDto1 = new MinimalVillageOutputDTO(testVillageId1, "Indomitable Village");
            MinimalVillageOutputDTO expectedDto2 = new MinimalVillageOutputDTO(testVillageId2, "Aquarium");
            List<MinimalVillageOutputDTO> expectedList = List.of(expectedDto1, expectedDto2);

            // When
            List<MinimalVillageOutputDTO> actualList = VillageOutputDTOConverter.convertMinimal(inputList);

            // Then
            assertThat(actualList)
                    .isNotNull()
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrderElementsOf(expectedList);
        }

        @Test
        @DisplayName("should return empty list when converting empty list of Villages minimally")
        void convertMinimalList_shouldReturnEmptyList_whenInputIsEmpty() {
            // Given
            List<Village> inputList = Collections.emptyList();
            List<MinimalVillageOutputDTO> expectedList = Collections.emptyList();

            // When
            List<MinimalVillageOutputDTO> actualList = VillageOutputDTOConverter.convertMinimal(inputList);

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
        Constructor<VillageOutputDTOConverter> constructor;
        try {
            constructor = VillageOutputDTOConverter.class.getDeclaredConstructor();
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