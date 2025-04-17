package ckollmeier.de.asterixapi.service;

import ckollmeier.de.asterixapi.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import ckollmeier.de.asterixapi.model.Character;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;

    public List<Character> getCharacters() {
        return characterRepository.findAll();
    }

    public Optional<Character> getCharacterByName(String name) {
        return Optional.ofNullable(characterRepository.findOneByName(name));
    }

    public List<Character> getCharactersByProfession(String profession) {
        return characterRepository.findByProfession(profession);
    }

    public List<Character> getCharactersOlderThan(int age) {
        return characterRepository.findByAgeGreaterThanEqual(age);
    }

    public void addCharacters(List<Character> characters) {
        characterRepository.saveAll(characters);
    }

    public Character addCharacter(Character character) {
        return characterRepository.save(character);
    }

    public Character addCharacter(String name, int age, String profession) {
        Character character = new Character(null, name, age, profession);
        return addCharacter(character);
    }

}
