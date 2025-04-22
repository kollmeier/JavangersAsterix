package ckollmeier.de.asterixapi.extractor;

import ckollmeier.de.asterixapi.model.Village;

import java.util.List;

/**
 * A utility class for extracting specific information, primarily IDs,
 * from {@link Village} objects or collections thereof.
 * <p>
 * This class contains only static methods and cannot be instantiated.
 * </p>
 */
public final class VillageExtractor {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws {@link IllegalStateException} if an attempt is made.
     */
    private VillageExtractor() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Extracts the unique identifier (ID) from a single {@link Village} object.
     *
     * @param village The {@link Village} object from which to extract the ID. Must not be null.
     * @return The ID ({@code String}) of the provided village.
     * @throws NullPointerException if the input {@code village} is null.
     */
    public static String extractVillageId(final Village village) {
        return village.id();
    }

    /**
     * Extracts the unique identifiers (IDs) from a list of {@link Village} objects.
     *
     * @param villages The {@link List} of {@link Village} objects. Can be empty, but should not contain null elements
     *                 if null pointer exceptions are to be avoided during processing.
     * @return A {@link List} containing the IDs ({@code String}) of the villages in the input list.
     *         Returns an empty list if the input list is empty.
     * @throws NullPointerException if the input {@code villages} list itself is null, or if the list contains null elements
     *                              and the stream processing attempts to call a method on a null element.
     */
    public static List<String> extractVillageIds(final List<Village> villages) {
        return villages.stream().map(VillageExtractor::extractVillageId).toList();
    }

}