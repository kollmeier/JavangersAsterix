package ckollmeier.de.asterixapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ckollmeier.de.asterixapi.model.Character;

import java.util.List;

public interface CharacterRepository extends MongoRepository<Character, String> {
    public Character findOneByName(String name);
    public List<Character> findByProfession(String profession);
    public List<Character> findByAgeGreaterThanEqual(int age);
}
