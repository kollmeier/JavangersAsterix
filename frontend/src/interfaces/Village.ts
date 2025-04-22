import Character, {implementsCharacter} from "./Character.ts";

export default interface Village {
    id: string;
    name: string;
    characters?: Character[];
}

export function implementsVillage(item: unknown): item is Village {
    return item !== null
        && typeof item === 'object'
        && 'id' in item
        && 'name' in item
        && typeof item.id === 'string'
        && typeof item.name === 'string'
        && (!('characters' in item)
            || (Array.isArray(item.characters)
                && item.characters.every(implementsCharacter))
        );
}

