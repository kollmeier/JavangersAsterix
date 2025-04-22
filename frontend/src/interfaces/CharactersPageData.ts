import Character, {implementsCharacter} from "./Character.ts";
import Village, {implementsVillage} from "./Village.ts";

export default interface CharactersPageData {
    characters: Character[];
    villages: Village[];
}

export function implementsCharactersPageData(item: unknown): item is CharactersPageData {
    return item !== null
        && typeof item === 'object'
        && 'characters' in item
        && 'villages' in item
        && Array.isArray(item.characters)
        && item.characters.every(implementsCharacter)
        && Array.isArray(item.villages)
        && item.villages.every(implementsVillage);
}