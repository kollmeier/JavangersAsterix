export default class VillageOptionsConverter {
    static convert(villages: { id: string; name: string }[]) {
        return villages.map(village => ({
            value: village.id,
            label: village.name
        }));
    }
}