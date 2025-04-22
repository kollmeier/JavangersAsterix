package ckollmeier.de.asterixapi.extractor;

import ckollmeier.de.asterixapi.model.Character;
import ckollmeier.de.asterixapi.model.Village;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link CharacterExtractor} using the Given-When-Then pattern.
 */
class CharacterExtractorTest {

    @Test
    @DisplayName("extractCharacterId should return the ID of a single character")
    void extractCharacterId_shouldReturnCharacterId() {
        // Given
        String expectedId = UUID.randomUUID().toString();
        Village village = new Village(UUID.randomUUID().toString(), "Test Village");
        Character character = new Character(expectedId, "Asterix", 35, "Warrior", village);

        // When
        String actualId = CharacterExtractor.extractCharacterId(character);

        // Then
        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("extractCharacterIds should return a list of IDs from a list of characters")
    void extractCharacterIds_shouldReturnListOfIds() {
        // Given
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        Village village = new Village(UUID.randomUUID().toString(), "Test Village");
        Character character1 = new Character(id1, "Asterix", 35, "Warrior", village);
        Character character2 = new Character(id2, "Obelix", 36, "Menhir Carver", village);
        List<Character> characters = List.of(character1, character2);

        List<String> expectedIds = List.of(id1, id2);

        // When
        List<String> actualIds = CharacterExtractor.extractCharacterIds(characters);

        // Then
        assertThat(actualIds)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    @DisplayName("extractCharacterIds should return an empty list when given an empty list")
    void extractCharacterIds_shouldReturnEmptyList_whenInputIsEmpty() {
        // Given
        List<Character> characters = Collections.emptyList();
        List<String> expectedIds = Collections.emptyList();

        // When
        List<String> actualIds = CharacterExtractor.extractCharacterIds(characters);

        // Then
        assertThat(actualIds)
                .isNotNull()
                .isEqualTo(expectedIds)
                .isEmpty();
    }

    @Test
    @DisplayName("Constructor should throw IllegalStateException to prevent instantiation")
    void constructor_shouldThrowIllegalStateException() {
        // Given
        Constructor<CharacterExtractor> constructor;
        try {
            constructor = CharacterExtractor.class.getDeclaredConstructor();
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