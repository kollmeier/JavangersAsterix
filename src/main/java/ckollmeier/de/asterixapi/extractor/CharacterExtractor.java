package ckollmeier.de.asterixapi.extractor;

import ckollmeier.de.asterixapi.model.Character;

import java.util.List;

/**
 * A utility class for extracting specific information, primarily IDs,
 * from {@link Character} objects or collections thereof.
 * <p>
 * This class contains only static methods and cannot be instantiated.
 * </p>
 */
public final class CharacterExtractor {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws {@link IllegalStateException} if an attempt is made.
     */
    private CharacterExtractor() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Extracts the unique identifier (ID) from a single {@link Character} object.
     *
     * @param character The {@link Character} object from which to extract the ID. Must not be null.
     * @return The ID ({@code String}) of the provided character.
     * @throws NullPointerException if the input {@code character} is null.
     */
    public static String extractCharacterId(final Character character) {
        return character.id();
    }

    /**
     * Extracts the unique identifiers (IDs) from a list of {@link Character} objects.
     *
     * @param characters The {@link List} of {@link Character} objects. Can be empty, but should not contain null elements
     *                  if null pointer exceptions are to be avoided during processing. Note: Parameter name is 'characters' for the list.
     * @return A {@link List} containing the IDs ({@code String}) of the characters in the input list.
     *         Returns an empty list if the input list is empty.
     * @throws NullPointerException if the input {@code characters} list itself is null, or if the list contains null elements
     *                              and the stream processing attempts to call a method on a null element.
     */
    public static List<String> extractCharacterIds(final List<Character> characters) {
        return characters.stream().map(CharacterExtractor::extractCharacterId).toList();
    }

}