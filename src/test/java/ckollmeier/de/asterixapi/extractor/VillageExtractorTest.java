package ckollmeier.de.asterixapi.extractor;

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
 * Unit tests for {@link VillageExtractor} using the Given-When-Then pattern.
 */
class VillageExtractorTest {

    @Test
    @DisplayName("extractVillageId should return the ID of a single village")
    void extractVillageId_shouldReturnVillageId() {
        // Given
        String expectedId = UUID.randomUUID().toString();
        Village village = new Village(expectedId, "Test Village"); // Inhabitants list not relevant here

        // When
        String actualId = VillageExtractor.extractVillageId(village);

        // Then
        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("extractVillageIds should return a list of IDs from a list of villages")
    void extractVillageIds_shouldReturnListOfIds() {
        // Given
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        Village village1 = new Village(id1, "Village One");
        Village village2 = new Village(id2, "Village Two");
        List<Village> villages = List.of(village1, village2);

        List<String> expectedIds = List.of(id1, id2);

        // When
        List<String> actualIds = VillageExtractor.extractVillageIds(villages);

        // Then
        assertThat(actualIds)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    @DisplayName("extractVillageIds should return an empty list when given an empty list")
    void extractVillageIds_shouldReturnEmptyList_whenInputIsEmpty() {
        // Given
        List<Village> villages = Collections.emptyList();
        List<String> expectedIds = Collections.emptyList();

        // When
        List<String> actualIds = VillageExtractor.extractVillageIds(villages);

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
        Constructor<VillageExtractor> constructor;
        try {
            constructor = VillageExtractor.class.getDeclaredConstructor();
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