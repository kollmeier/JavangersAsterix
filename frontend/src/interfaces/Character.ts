import Village, {implementsVillage} from "./Village.ts";

export default interface Character {
    id: string;
    name: string;
    age: number;
    profession: string;
    village?: Village;
}

export function implementsCharacter(item: unknown): item is Character {
    return item !== null
        && typeof item === 'object'
        && 'id' in item
        && 'name' in item
        && 'age' in item
        && 'profession' in item
        && typeof item.id === 'string'
        && typeof item.name === 'string'
        && typeof item.age === 'number'
        && typeof item.profession === 'string'
        && (!('village' in item) || item.village === null || implementsVillage(item.village));
}

