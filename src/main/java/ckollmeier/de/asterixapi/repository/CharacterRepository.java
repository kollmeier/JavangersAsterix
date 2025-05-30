package ckollmeier.de.asterixapi.repository;

import ckollmeier.de.asterixapi.model.Village;
import org.springframework.data.mongodb.repository.MongoRepository;
import ckollmeier.de.asterixapi.model.Character;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CharacterRepository extends MongoRepository<Character, String> {
    Optional<Character> findOneByName(String name);
    List<Character> findByProfession(String profession);
    List<Character> findByAgeGreaterThanEqual(int age);
    List<Character> findByVillageId(String villageId);
    List<Character> findByVillageIn(Collection<Village> villages);
    List<Character> findByIdIn(Collection<String> ids);
}
