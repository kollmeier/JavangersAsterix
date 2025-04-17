package ckollmeier.de.asterixapi.controller;

import ckollmeier.de.asterixapi.exception.NotFoundException;
import ckollmeier.de.asterixapi.service.CharacterService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import ckollmeier.de.asterixapi.model.Character;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/asterix")
@RequiredArgsConstructor
public class AsterixController {
    private final CharacterService characterService;
    /**
     * @return List of characters
     */
    @GetMapping("characters")
    public List<Character> getCharacters() {
        return characterService.getCharacters();
    }

    @GetMapping("characters/{name}")
    public Character getCharacterByName(@PathVariable String name) {
        return characterService.getCharacterByName(name).orElseThrow(() -> new NotFoundException(String.format("Character with name '%s' not found", name)));
    }

    @GetMapping("characters/profession/{profession}")
    public List<Character> getCharactersByProfession(@PathVariable String profession) {
        return characterService.getCharactersByProfession(profession);
    }

    @GetMapping("characters/olderthan/{age}")
    public List<Character> getCharactersOlderThan(@PathVariable Integer age) {
        return characterService.getCharactersOlderThan(age);
    }

    @PostMapping("characters/add")
    public Character addCharacter(@RequestBody Character character) {
        return characterService.addCharacter(character);
    }


}
