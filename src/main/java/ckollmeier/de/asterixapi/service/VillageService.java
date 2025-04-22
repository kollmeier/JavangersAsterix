package ckollmeier.de.asterixapi.service;

import ckollmeier.de.asterixapi.converter.VillageConverter;
import ckollmeier.de.asterixapi.converter.VillageOutputDTOConverter;
import ckollmeier.de.asterixapi.dataprovider.VillagesPageDataProvider;
import ckollmeier.de.asterixapi.dto.VillageInputDTO;
import ckollmeier.de.asterixapi.dto.VillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillagesPageDTO;
import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.model.Character;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import ckollmeier.de.asterixapi.repository.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Asterix villages.
 * Handles business logic related to village retrieval, creation, deletion, and updates,
 * interacting with the {@link VillageRepository}, {@link CharacterRepository}, {@link IdService},
 * data providers, and converters.
 */
@Service
@RequiredArgsConstructor
public class VillageService {

    /**
     * Repository for accessing village data persistence.
     */
    private final VillageRepository villageRepository;

    /**
     * Repository for accessing character data, primarily used here to fetch
     * inhabitants when updating a village's character list.
     */
    private final CharacterRepository characterRepository;
    /**
     * Data provider for aggregating data needed for the villages page view.
     */
    private final VillagesPageDataProvider villagePageDataProvider;

    /**
     * Converter used to transform {@link VillageInputDTO} objects into {@link Village} entities.
     */
    private final VillageConverter villageConverter;
    /**
     * Service used for generating unique IDs for new villages.
     */
    private final IdService idService;

    /**
     * Retrieves a list of all villages currently stored.
     * Note: This retrieves the raw {@link Village} entities. For DTOs suitable for output,
     * consider using methods from {@link ckollmeier.de.asterixapi.dataprovider.VillageDataProvider}.
     *
     * @return A {@link List} containing all {@link Village} entities. Returns an empty list if none exist.
     */
    public List<Village> getVillages() {
        return villageRepository.findAll();
    }

    /**
     * Retrieves the aggregated data required for displaying the villages page.
     * Delegates the data fetching and aggregation to the {@link VillagesPageDataProvider}.
     *
     * @return A {@link VillagesPageDTO} containing lists of villages for selection
     *         and villages for output display.
     */
    public VillagesPageDTO getVillagesPageData() {
        return villagePageDataProvider.providePageData();
    }

    /**
     * Finds a single village by their exact name.
     *
     * @param name The name of the village to search for.
     * @return An {@link Optional} containing the found {@link Village} if a match exists, otherwise an empty Optional.
     */
    public Optional<Village> getVillageByName(final String name) {
        // Note: Repository method findOneByName might return null, which Optional.ofNullable handles correctly.
        // If the repository method itself returned Optional, this wrapping wouldn't be needed.
        return villageRepository.findOneByName(name);
    }

    /**
     * Finds a single village by their unique identifier.
     *
     * @param id The unique ID of the village to search for.
     * @return An {@link Optional} containing the found {@link Village} if a match exists, otherwise an empty Optional.
     */
    public Optional<Village> getVillageById(final String id) {
        return villageRepository.findById(id);
    }

    /**
     * Creates and saves a new village based on the provided DTO data.
     * A unique ID is generated for the new village using the {@link IdService}.
     * The village data is converted from the DTO to the entity model using the {@link VillageConverter}.
     *
     * @param villageInputDTO The {@link VillageInputDTO} containing the data for the new village.
     * @return The newly created and persisted {@link Village} entity, including its generated ID.
     */
    public VillageOutputDTO addVillage(final VillageInputDTO villageInputDTO) {
        // Converts DTO to entity using the converter, generates a new ID, and saves it.
        Village village = villageRepository.save(villageConverter.convert(villageInputDTO).withId(idService.generateId()));

        final List<Character> addedInhabitants =
                characterRepository.findByIdIn(villageInputDTO.characterIds())
                        .stream()
                        .map(character -> character.withVillage(village))
                        .toList();
        characterRepository.saveAll(addedInhabitants);
        return VillageOutputDTOConverter.convert(village, addedInhabitants);
    }

    /**
     * Saves the given village entity after assigning it a newly generated unique ID.
     * <p>
     * This method first checks if the provided {@code village} object already has an ID.
     * If an ID is present, it throws an {@link IllegalArgumentException} to prevent accidental
     * overwriting or misuse when a new ID generation is intended.
     * If no ID is present, a new unique ID is generated using the {@link IdService} before saving.
     * </p>
     *
     * @param village The {@link Village} entity to save. Must not have its ID already set.
     * @return The persisted {@link Village} entity with its newly generated ID.
     * @throws IllegalArgumentException if the input {@code village} already has a non-null ID.
     */
    public Village addVillage(final Village village) {
        if (village.id() != null) {
            throw new IllegalArgumentException("Village id is already set");
        }
        // Assigns a new ID and saves the village.
        return villageRepository.save(village.withId(idService.generateId()));
    }

    /**
     * Removes a village from the repository by their unique ID.
     * <p>
     * This method first attempts to find the village by the given ID. If the village is not found,
     * a {@link NotFoundException} is thrown. If found, the village is deleted from the repository.
     * </p>
     *
     * @param id The unique ID of the village to remove.
     * @return The {@link Village} entity that was removed.
     * @throws NotFoundException if no village with the given ID exists.
     */
    public Village removeVillage(final String id) {
        final Village village = getVillageById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Village with id '%s' not found", id)));
        final List<Character> existingInhabitants = characterRepository.findByVillageId(id);
        for (Character character : existingInhabitants) {
            characterRepository.save(character.withVillage(null));
        }
        villageRepository.delete(village);
        return village;
    }

    /**
     * Updates an existing village's information based on the provided data.
     * <p>
     * This method first retrieves the existing village by the given ID. If the village is not found,
     * a {@link NotFoundException} is thrown. If found, the village's properties (name, inhabitants)
     * are updated based on the values provided in the {@link VillageInputDTO} using the helper method
     * {@link #getVillage(VillageInputDTO, Village)}.
     * The village's ID remains unchanged.
     * </p>
     *
     * @param id        The unique ID of the village to update.
     * @param village The {@link VillageInputDTO} containing the updated data (name and character IDs).
     * @return The updated and persisted {@link Village} entity.
     * @throws NotFoundException if no village with the given ID exists.
     */
    public VillageOutputDTO updateVillage(final String id, final VillageInputDTO village) {
        // Retrieve the existing village or throw NotFoundException
        final Village existingVillage = getVillageById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Village with id '%s' not found", id)));
        if (village.characterIds() != null) {
            final List<Character> existingInhabitants = characterRepository.findByVillageId(id);
            final List<Character> changedInhabitants = characterRepository.findByIdIn(village.characterIds());
            final List<Character> removedInhabitants = existingInhabitants.stream()
                    .filter(i1 -> changedInhabitants.stream().noneMatch(i2 -> i1.id().equals(i2.id()))).toList();
            final List<Character> addedInhabitants = changedInhabitants.stream()
                    .filter(i1 -> existingInhabitants.stream().noneMatch(i2 -> i1.id().equals(i2.id()))).toList();

            for (Character removedCharacter : removedInhabitants) {
                characterRepository.save(removedCharacter.withVillage(null));
            }
            for (Character addedCharacter : addedInhabitants) {
                characterRepository.save(addedCharacter.withVillage(existingVillage));
            }
        }
        // Use helper method to apply updates from DTO to existing entity
        Village villageToUpdate = getVillage(village, existingVillage);

        // Save the potentially modified village
        return VillageOutputDTOConverter.convert(villageRepository.save(villageToUpdate), characterRepository.findByVillageId(id));
    }

    /**
     * Helper method to apply updates from a {@link VillageInputDTO} to an existing {@link Village} entity.
     * <p>
     * It takes the existing village and creates a new instance with updated fields based on the non-null
     * values from the input DTO. Specifically, it updates the name if provided and updates the list
     * of inhabitants if the list of character IDs in the DTO differs from the existing inhabitants' IDs.
     * This uses the `with...` methods generated by Lombok (assuming `@Wither` or similar),
     * promoting immutability.
     * </p>
     *
     * @param village         The {@link VillageInputDTO} containing the update data (name, character IDs).
     * @param existingVillage The current {@link Village} entity to be updated.
     * @return A new {@link Village} instance with the updates applied. The original ID is preserved.
     */
    private Village getVillage(final VillageInputDTO village, final Village existingVillage) {
        Village villageToUpdate = existingVillage;

        // Conditionally update name if provided in the DTO
        if (village.name() != null) {
            villageToUpdate = villageToUpdate.withName(village.name());
        }
        // Conditionally update inhabitants if the list of IDs has changed
//        if (village.characterIds() != null && !village.characterIds().equals(CharacterExtractor.extractCharacterIds(existingVillage.inhabitants()))) {
//            // Fetch the full Character objects based on the provided IDs and update the inhabitants list
//            villageToUpdate = villageToUpdate.withInhabitants(characterRepository.findByIdIn(village.characterIds()));
//        }
        return villageToUpdate;
    }
}