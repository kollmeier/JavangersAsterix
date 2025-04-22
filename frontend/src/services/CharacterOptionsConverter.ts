import CharacterForSelect from "../interfaces/CharacterForSelect.ts";
import Character from "../interfaces/Character.ts";

export default class VillageOptionsConverter {
    static convert(characters: CharacterForSelect[]) {
        const villagesById: Record<string, string> = {};
        characters.forEach(character => {
            if (character.villageId !== null && character.villageName !== null) {
                villagesById[character.villageId] = character.villageName;
            }
        });
        const options = Object.keys(villagesById).map((villageId: string) => ({
            label: villagesById[villageId],
            options: characters.filter(character => character.villageId === villageId)
                .map(character => ({
                    value: character.id,
                    label: character.name
                }))
        }))
        options.unshift({
            'label': "Kein Dorf ausgewählt",
            'options': characters
                .filter(character => character.villageId == null)
                .map(character => { return {
                    'value': character.id,
                    'label': character.name
                }})
        });
        return options;
    }

    static convertFromCharactersToCharacterForSelect(characters: Character[]) {
        return characters.map(
            character => {
                return {
                    id: character.id,
                    name: character.name,
                    villageId: character.village?.id ?? null,
                    villageName: character.village?.name ?? null
                }
            });
    }
}