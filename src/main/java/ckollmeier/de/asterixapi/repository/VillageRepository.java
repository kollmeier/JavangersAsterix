package ckollmeier.de.asterixapi.repository;


import ckollmeier.de.asterixapi.model.Village;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VillageRepository extends MongoRepository<Village, String> {
    Optional<Village> findOneByName(String name);
}
