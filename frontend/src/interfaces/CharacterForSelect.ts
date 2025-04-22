export default interface CharacterForSelect {
    id: string;
    name: string;
    villageName: string | null;
    villageId: string  | null;
}

export function implementsCharacterForSelect(item: unknown): item is CharacterForSelect {
    return item !== null
        && typeof item === 'object'
        && 'id' in item
        && 'name' in item
        && 'villageName' in item
        && 'villageId' in item
        && typeof item.id === 'string'
        && typeof item.name === 'string'
        && (item.villageName === null || typeof item.villageName === 'string')
        && (item.villageId === null || typeof item.villageId === 'string');
}

