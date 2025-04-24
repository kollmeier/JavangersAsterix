import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {Link, useParams} from 'react-router-dom';
import Character from "./interfaces/Character.ts";
import CharacterEdit from "./CharacterEdit.tsx";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faEdit} from '@fortawesome/free-solid-svg-icons/faEdit';
import {faTrash} from '@fortawesome/free-solid-svg-icons/faTrash';
import {toast} from 'react-toastify';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import CharacterAdd from "./CharacterAdd.tsx";
import {implementsCharactersPageData} from "./interfaces/CharactersPageData.ts";
import VillageOptionsConverter from "./services/VillageOptionsConverter.ts";

const CharactersPage: React.FC = () => {
    const [characters, setCharacters] = useState<Character[]>([]);
    const [villageOptions, setVillageOptions] = useState<{ value: string; label: string }[]>([]);
    const [editingCharacter, setEditingCharacter] = useState<Character | null>(null);
    const [professions, setProfessions] = useState<{ value: string; label: string }[]>([]);

    const characterId = useParams().characterId;

    function onlyUnique(value: { value: string; label: string }, index: number, array: { value: string; label: string }[]) {
        return array.indexOf(value) === index;
    }

    useEffect(() => {
        setProfessions(characters
            .map((character) => {return {value: character.profession, label: character.profession}})
            .filter(onlyUnique)
            .sort((a, b) => a.value.localeCompare(b.value)));
    }, [characters]);

    useEffect(() => {
        toast.promise(
            axios.get('/api/asterix/characters/page-data')
                .then((response) => {
                    const pageData = response.data;
                    if (implementsCharactersPageData(pageData)) {
                        setCharacters(pageData.characters);
                        setVillageOptions(VillageOptionsConverter.convert(pageData.villages));
                    }
                }), {
                pending: 'Lade Charakterliste...',
                error: 'Fehler beim Laden der Charakterliste',
                success: 'Charakterliste erfolgreich geladen'
            }
        );
    }, []);

    useEffect(() => {
        if (characterId) {
            setEditingCharacter(characters.find((character) => character.id === characterId) || null);
            return;
        }
        setEditingCharacter(null);
    }, [characterId]);

    const handleDelete = async (event: React.MouseEvent<HTMLAnchorElement>, id: string) => {
        event.preventDefault();
        if (!confirm("Sind Sie sicher, dass Sie diesen Charakter löschen möchten?")) {
            return;
        }
        return toast.promise(
            axios.delete('/api/asterix/characters/remove', {data: {id}})
                .then(() => {
                    setCharacters(characters.filter((character) => character.id !== id));
                }),
            {
                pending: 'Lösche Charakter...',
                error: 'Fehler beim Löschen des Charakters...',
                success: 'Charakter erfolgreich gelöscht'
            }
        );
    }

    const CharacterCard = ({character}: Props) => {
        return (
            <div>
                <span className="character-name">{character.name}</span>
                <span className="character-profession">{character.profession}</span>
                <span className="character-age">{character.age} Jahre alt</span>
                <span className="character-village">{character.village?.name ?? 'Unbekannt'}</span>
                <span className="character-actions">
                <Link className="button" to={`/characters/${character.id}/edit`}
                      onClick={() => setEditingCharacter(character)}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</Link>
                <Link className="button" to={`/characters/${character.id}/delete`}
                      onClick={(event: React.MouseEvent<HTMLAnchorElement>) => handleDelete(event, character.id)}><FontAwesomeIcon
                    icon={faTrash}/> Löschen</Link>
            </span>
            </div>
        );
    }

    return (
        <div>
            <h2>Charaktere</h2>

            <ul className="character-list">
                <li className="character-card">
                    {characterId !== 'add' ? (
                        <Link to="/characters/add" className="centered circle-button"><FontAwesomeIcon icon={faPlus}/> Charakter hinzufügen</Link>
                    ) : (
                        <CharacterAdd setCharacters={setCharacters} villageOptions={villageOptions} professions={professions} />
                    )}
                </li>
                {characters.map((character) => (
                    <li className="character-card" key={character.id}>
                        {((editingCharacter?.id ?? characterId) !== character.id) ? (
                            <CharacterCard character={character}/>
                        ) : (
                            <CharacterEdit character={character}
                                           setCharacters={setCharacters}
                                           setEditingCharacter={setEditingCharacter}
                                           villageOptions={villageOptions}
                                           professions={professions}/>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

interface Props {
    character: Character;
}

export default CharactersPage;