package ckollmeier.de.asterixapi.service;

import ckollmeier.de.asterixapi.dto.CharacterInputDTO;
import ckollmeier.de.asterixapi.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ckollmeier.de.asterixapi.model.Character;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;

    public List<Character> getCharacters() {
        return characterRepository.findAll();
    }

    public Optional<Character> getCharacterByName(final String name) {
        return Optional.ofNullable(characterRepository.findOneByName(name));
    }

    public List<Character> getCharactersByProfession(final String profession) {
        return characterRepository.findByProfession(profession);
    }

    public List<Character> getCharactersOlderThan(final int age) {
        return characterRepository.findByAgeGreaterThanEqual(age);
    }

    public Character addCharacter(final CharacterInputDTO characterInputDTO) {
        return characterRepository.save(characterInputDTO.toCharacter().withId(UUID.randomUUID().toString()));
    }

    public Character addCharacter(final Character character) {
        return characterRepository.save(character.withId(UUID.randomUUID().toString()));
    }
}
