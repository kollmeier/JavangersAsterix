package ckollmeier.de.asterixapi.converter;

import ckollmeier.de.asterixapi.dto.MinimalVillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillageOutputDTO;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.model.Character;

import java.util.List;

/**
 * A utility class responsible for converting {@link Village} model entities
 * into different village-related DTOs like {@link VillageOutputDTO} and {@link MinimalVillageOutputDTO}.
 * This is typically used to prepare village data for presentation layers or API responses,
 * exposing different levels of detail depending on the target DTO.
 * <p>
 * This class uses static methods and cannot be instantiated.
 * </p>
 */
public final class VillageOutputDTOConverter {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private VillageOutputDTOConverter() {
        // Private constructor to prevent instantiation
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts a single {@link Village} entity into a {@link VillageOutputDTO}
     * **without** including its inhabitants. The inhabitants list in the resulting DTO will be {@code null}.
     * <p>
     * For a conversion including inhabitants, use {@link #convert(Village, List)}.
     * </p>
     * If the input village is {@code null}, this method will likely throw a {@link NullPointerException}.
     *
     * @param village The {@link Village} entity to convert. Should not be null.
     * @return A new {@link VillageOutputDTO} containing the ID and name, with inhabitants set to {@code null}.
     */
    public static VillageOutputDTO convert(final Village village) {
        // Creates a DTO using the ID and name from the Village record, inhabitants are null.
        return new VillageOutputDTO(
                village.id(),
                village.name(),
                null // Explicitly null as this version doesn't handle inhabitants
        );
    }

    /**
     * Converts a single {@link Village} entity into a detailed {@link VillageOutputDTO}.
     * This DTO includes the village's ID, name, and a list of its inhabitants converted
     * into minimal character DTOs (using {@link CharacterOutputDTOConverter#convertMinimal(Character)}).
     * It filters the provided list of all characters to find those belonging to the specified village.
     * <p>
     * If the input village or character list is {@code null}, this method may throw a {@link NullPointerException}.
     * </p>
     *
     * @param village              The {@link Village} entity to convert. Should not be null.
     * @param charactersInVillages A list containing all potentially relevant {@link Character} entities.
     *                             This list will be filtered to find inhabitants of the given {@code village}. Should not be null.
     * @return A new {@link VillageOutputDTO} containing the ID, name, and minimally represented inhabitants.
     */
    public static VillageOutputDTO convert(final Village village, final List<Character> charactersInVillages) {
        // Creates a DTO using the ID, name, and filters/converts inhabitants from the provided character list.
        return new VillageOutputDTO(
                village.id(),
                village.name(),
                charactersInVillages.stream()
                        // Ensure character and its village/ID are not null before comparison
                        .filter(character -> character != null && character.village() != null && character.village().id() != null)
                        .filter(character -> character.village().id().equals(village.id()))
                        .map(CharacterOutputDTOConverter::convertMinimal) // Convert matching characters
                        .toList()
        );
    }

    /**
     * Converts a single {@link Village} entity into a minimal {@link MinimalVillageOutputDTO}.
     * This DTO includes only the village's ID and name, omitting the list of inhabitants.
     * If the input village is {@code null}, this method will likely throw a {@link NullPointerException}.
     *
     * @param village The {@link Village} entity to convert. Should not be null.
     * @return A new {@link MinimalVillageOutputDTO} containing only the ID and name.
     */
    public static MinimalVillageOutputDTO convertMinimal(final Village village) {
        // Creates a minimal DTO using only the ID and name from the Village record.
        return new MinimalVillageOutputDTO(
                village.id(),
                village.name()
        );
    }

    /**
     * Converts a list of {@link Village} entities into a list of {@link VillageOutputDTO} objects
     * **without** including their inhabitants.
     * It iterates over the input list and applies the single-entity conversion method {@link #convert(Village)}
     * to each element. The inhabitants list in the resulting DTOs will be {@code null}.
     * <p>
     * For a conversion including inhabitants, use {@link #convert(List, List)}.
     * </p>
     *
     * @param villages The list of {@link Village} entities to convert. Can be empty, but should not be null
     *                 if the stream operation is to be avoided on nulls.
     * @return A new {@link List} containing {@link VillageOutputDTO} objects corresponding to the input villages,
     *         with inhabitants set to {@code null}. Returns an empty list if the input list is empty.
     */
    public static List<VillageOutputDTO> convert(final List<Village> villages) {
        // Uses Java Streams to map each Village in the list to its DTO representation without inhabitants.
        return villages.stream()
                .map(VillageOutputDTOConverter::convert) // Calls the convert(Village) method (no inhabitants)
                .toList(); // Collects the results into a new List
    }

    /**
     * Converts a list of {@link Village} entities into a list of detailed {@link VillageOutputDTO} objects,
     * including their respective inhabitants converted into minimal character DTOs.
     * It iterates over the input village list and, for each village, filters the provided character list
     * to find and convert its inhabitants using {@link #convert(Village, List)}.
     *
     * @param villages             The list of {@link Village} entities to convert. Can be empty, but should not be null.
     * @param charactersInVillages A list containing all potentially relevant {@link Character} entities
     *                             across all villages being converted. Should not be null.
     * @return A new {@link List} containing detailed {@link VillageOutputDTO} objects corresponding to the input villages,
     *         including their inhabitants. Returns an empty list if the input village list is empty.
     */
    public static List<VillageOutputDTO> convert(final List<Village> villages, final List<Character> charactersInVillages) {
        // Uses Java Streams to map each Village, applying the conversion that includes inhabitants.
        return villages.stream()
                .map(village -> VillageOutputDTOConverter.convert(village, charactersInVillages)) // Calls the convert(Village, List<Character>) method
                .toList(); // Collects the results into a new List
    }

    /**
     * Converts a list of {@link Village} entities into a list of minimal {@link MinimalVillageOutputDTO} objects.
     * It iterates over the input list and applies the single-entity minimal conversion method {@link #convertMinimal(Village)}
     * to each element.
     *
     * @param villages The list of {@link Village} entities to convert. Can be empty, but should not be null
     *                 if the stream operation is to be avoided on nulls.
     * @return A new {@link List} containing {@link MinimalVillageOutputDTO} objects corresponding to the input villages.
     *         Returns an empty list if the input list is empty.
     */
    public static List<MinimalVillageOutputDTO> convertMinimal(final List<Village> villages) {
        // Uses Java Streams to map each Village in the list to its minimal DTO representation.
        return villages.stream()
                .map(VillageOutputDTOConverter::convertMinimal) // Calls the convertMinimal(Village) method for each element
                .toList(); // Collects the results into a new List
    }
}