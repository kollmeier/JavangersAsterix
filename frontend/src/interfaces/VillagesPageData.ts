import Village, {implementsVillage} from "./Village.ts";
import CharacterForSelect, {implementsCharacterForSelect} from "./CharacterForSelect.ts";

export default interface VillagesPageData {
    villages: Village[];
    characters: CharacterForSelect[];
}

export function implementsVillagesPageData(item: unknown): item is VillagesPageData {
    return item !== null
        && typeof item === 'object'
        && 'characters' in item
        && 'villages' in item
        && Array.isArray(item.characters)
        && item.characters.every(implementsCharacterForSelect)
        && Array.isArray(item.villages)
        && item.villages.every(implementsVillage);
}