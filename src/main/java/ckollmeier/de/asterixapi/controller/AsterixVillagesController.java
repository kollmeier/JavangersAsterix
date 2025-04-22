package ckollmeier.de.asterixapi.controller;

import ckollmeier.de.asterixapi.dataprovider.VillageDataProvider;
import ckollmeier.de.asterixapi.dto.VillageIdDTO;
import ckollmeier.de.asterixapi.dto.VillageInputDTO;
import ckollmeier.de.asterixapi.dto.VillageOutputDTO;
import ckollmeier.de.asterixapi.dto.VillagesPageDTO;
import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.model.Village;
import ckollmeier.de.asterixapi.service.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing Asterix villages.
 * Provides endpoints to retrieve, add, update, and delete villages,
 * often interacting with services, data providers, and converters.
 */
@RestController
@RequestMapping("/asterix/villages")
@RequiredArgsConstructor
public class AsterixVillagesController {

    /**
     * Service layer dependency for core village-related business logic
     * like creation, update, deletion, and specific lookups.
     */
    private final VillageService villageService;
    /**
     * Data provider dependency for fetching village data formatted specifically
     * for output (e.g., lists with associated village details).
     */
    private final VillageDataProvider villageDataProvider;
    /**
     * Retrieves a list of all Asterix villages formatted for output.
     * Corresponds to the GET request at "/asterix/villages".
     * Uses the {@link VillageDataProvider} to get detailed DTOs.
     *
     * @return A {@link List} containing {@link VillageOutputDTO} objects.
     */
    @GetMapping()
    public List<VillageOutputDTO> getVillages() {
        return villageDataProvider.provideListForOutput();
    }

    /**
     * Retrieves the aggregated data needed for displaying the villages page.
     * Corresponds to the GET request at "/asterix/villages/page-data".
     * This typically includes data for dropdowns (like villages) and the main village list.
     *
     * @return A {@link VillagesPageDTO} containing the necessary data for the page view.
     */
    @GetMapping("/page-data")
    public VillagesPageDTO getVillagesPageData() {
        return villageService.getVillagesPageData();
    }

    /**
     * Retrieves a specific village entity by their name.
     * Corresponds to the GET request at "/asterix/villages/{name}".
     * Note: Returns the raw {@link Village} model entity.
     *
     * @param name The name of the village to retrieve (passed as a path variable).
     * @return The {@link Village} entity matching the given name.
     * @throws NotFoundException if no village with the specified name is found.
     */
    @GetMapping("/{name}")
    public Village getVillageByName(final @PathVariable String name) {
        return villageService.getVillageByName(name)
                .orElseThrow(() -> new NotFoundException(String.format("Village with name '%s' not found", name)));
    }

    /**
     * Retrieves a specific village entity by their unique identifier.
     * Corresponds to the GET request at "/asterix/villages/id/{id}".
     * Note: Returns the raw {@link Village} model entity.
     *
     * @param id The unique ID of the village to retrieve (passed as a path variable).
     * @return The {@link Village} entity matching the given ID.
     * @throws NotFoundException if no village with the specified ID is found.
     */
    @GetMapping("/id/{id}")
    public Village getVillageById(final @PathVariable String id) {
        return villageService.getVillageById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Village with id '%s' not found", id)));
    }

    /**
     * Adds a new village to the collection using data from the input DTO.
     * Corresponds to the POST request at "/asterix/villages/add".
     * Delegates the creation logic to the {@link VillageService}.
     *
     * @param village The village data provided in the request body as a {@link VillageInputDTO}.
     * @return The newly created {@link Village} entity, including its generated ID.
     */
    @PostMapping("/add")
    public VillageOutputDTO addVillage(final @RequestBody VillageInputDTO village) {
        return villageService.addVillage(village);
    }

    /**
     * Removes a village from the collection by its ID.
     * Corresponds to the DELETE request at "/asterix/villages/remove".
     * Expects a DTO containing the ID in the request body.
     * Delegates the removal logic to the {@link VillageService}.
     *
     * @param village The {@link VillageIdDTO} with the ID of the village to remove, provided in the request body.
     * @return The removed {@link Village} entity.
     * @throws NotFoundException if no village with the specified ID is found.
     */
    @DeleteMapping("/remove")
    public Village removeVillage(final @RequestBody VillageIdDTO village) {
        return villageService.removeVillage(village.id());
    }

    /**
     * Updates an existing village in the collection by its ID.
     * Corresponds to the PUT request at "/asterix/villages/update/{id}".
     * Delegates the update logic to the {@link VillageService}.
     *
     * @param id The ID of the village to update, provided as a path variable.
     * @param village The updated village data provided in the request body as a {@link VillageInputDTO}.
     * @return The updated {@link Village} entity.
     * @throws NotFoundException if no village with the specified ID is found.
     */
    @PutMapping("/update/{id}")
    public VillageOutputDTO updateVillage(final @PathVariable String id, final @RequestBody VillageInputDTO village) {
        return villageService.updateVillage(id, village);
    }

}