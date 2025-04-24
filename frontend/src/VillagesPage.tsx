import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {Link, useParams} from 'react-router-dom';
import Village from "./interfaces/Village.ts";
import VillageEdit from "./VillageEdit.tsx";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faEdit} from '@fortawesome/free-solid-svg-icons/faEdit';
import {faTrash} from '@fortawesome/free-solid-svg-icons/faTrash';
import {toast} from 'react-toastify';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import VillageAdd from "./VillageAdd.tsx";
import CharacterForSelect from "./interfaces/CharacterForSelect.ts";
import {implementsVillagesPageData} from "./interfaces/VillagesPageData.ts";
import CharacterOptionsConverter from './services/CharacterOptionsConverter.ts';

const VillagesPage: React.FC = () => {
    const [villages, setVillages] = useState<Village[]>([]);
    const [characterOptions, setCharacterOptions] = useState<CharacterForSelect[]>([]);
    const [editingVillage, setEditingVillage] = useState<Village | null>(null);

    const villageId = useParams().villageId;

    useEffect(() => {
        toast.promise(
            axios.get('/api/asterix/villages/page-data')
                .then((response) => {
                    if (implementsVillagesPageData(response.data)) {
                        setVillages(response.data.villages);
                        setCharacterOptions(response.data.characters);
                    }
                }), {
                pending: 'Lade Dorfliste...',
                error: 'Fehler beim Laden der Dorfliste',
                success: 'Dorfliste erfolgreich geladen'
            }
        );
    }, []);

    useEffect(() => {
        if (villageId) {
            setEditingVillage(villages.find((village) => village.id === villageId) || null);
            return;
        }
        setEditingVillage(null);
    }, [villageId]);

    useEffect(() => {
        const newCharacterOptions: CharacterForSelect[] = [];
        characterOptions.forEach((option) => {
            const village = villages
                .find((village) => village.characters?.some(character => character.id === option.id)) ?? null;
            option.villageId = village?.id ?? null;
            option.villageName = village?.name ?? null;
            newCharacterOptions.push(option);
        });
        setCharacterOptions(newCharacterOptions);
    }, [villages]);

    const handleDelete = async (event: React.MouseEvent<HTMLAnchorElement>, id: string) => {
        event.preventDefault();
        if (!confirm("Sind Sie sicher, dass Sie dieses Dorf löschen möchten?")) {
            return;
        }
        return toast.promise(
            axios.delete('/api/asterix/villages/remove', {data: {id}})
                .then(() => {
                    setVillages(villages.filter((village) => village.id !== id));
                }),
            {
                pending: 'Lösche Dorf...',
                error: 'Fehler beim Löschen des Dorfes...',
                success: 'Dorf erfolgreich gelöscht'
            }
        );
    }

    const VillageCard = ({village}: Props) => {
        return (
            <div>
                <span className="village-name">{village.name}</span>
                <span className="village-inhabitants">
                    {village.characters?.map(character => (
                        <span key={character.id}>{character.name}</span>
                    ))}
                </span>
                <span className="village-actions">
                    <Link className="button" to={`/villages/${village.id}/edit`}
                          onClick={() => setEditingVillage(village)}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</Link>
                    <Link className="button" to={`/villages/${village.id}/delete`}
                          onClick={(event: React.MouseEvent<HTMLAnchorElement>) => handleDelete(event, village.id)}><FontAwesomeIcon
                        icon={faTrash}/> Löschen</Link>
                </span>
            </div>
        );
    }

    return (
        <div>
            <h2>Dörfer</h2>

            <ul className="village-list">
                <li className="village-card">
                    {villageId !== 'add' ? (
                        <Link to="/villages/add" className="centered circle-button"><FontAwesomeIcon icon={faPlus}/> Dorf hinzufügen</Link>
                    ) : (
                        <VillageAdd setVillages={setVillages} characterOptions={CharacterOptionsConverter.convert(characterOptions)} />
                    )}
                </li>
                {villages.map((village) => (
                    <li className="village-card" key={village.id}>
                        {((editingVillage?.id ?? villageId) !== village.id) ? (
                            <VillageCard village={village}/>
                        ) : (
                            <VillageEdit village={village}
                                         characterOptions={CharacterOptionsConverter.convert(characterOptions)}
                                         setVillages={setVillages}
                            />
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

interface Props {
    village: Village;
}

export default VillagesPage;