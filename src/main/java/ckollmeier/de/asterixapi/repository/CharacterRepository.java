package ckollmeier.de.asterixapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ckollmeier.de.asterixapi.model.Character;

import java.util.List;

public interface CharacterRepository extends MongoRepository<Character, String> {
    Character findOneByName(String name);
    List<Character> findByProfession(String profession);
    List<Character> findByAgeGreaterThanEqual(int age);
}
