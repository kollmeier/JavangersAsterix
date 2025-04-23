import {ChangeEvent, useEffect, useState} from "react";
import CharacterInputDTO from "./interfaces/CharacterInputDTO.ts";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Character from "./interfaces/Character.ts";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faClose } from "@fortawesome/free-solid-svg-icons/faClose";
import {toast} from "react-toastify";
import { faSave } from "@fortawesome/free-solid-svg-icons/faSave";
import Select from "react-select";
import CreatableSelect from "react-select/creatable";

interface Props {
    setCharacters: React.Dispatch<React.SetStateAction<Character[]>>
    setEditingCharacter?: React.Dispatch<React.SetStateAction<Character | null>>
    character: Character;
    villageOptions: { value: string; label: string }[];
    professions: { value: string; label: string }[];
}

const CharacterEdit = ({ setCharacters, character, setEditingCharacter, villageOptions, professions }: Props) => {
    const [changedCharacter, setChangedCharacter] = useState<CharacterInputDTO>({
        name: '',
        profession: '',
        age: '',
        villageId: ''
    });

    const navigate = useNavigate();

    useEffect(() => {
        setChangedCharacter({
            name: character.name,
            profession: character.profession,
            age: character.age + '',
            villageId: character.village?.id ?? ''
        });
    }, [character]);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        if (setEditingCharacter) {
            setEditingCharacter(null);
        }
        return toast.promise(
            axios.put('/api/asterix/characters/update/' + character.id, changedCharacter)
                .then((response) => {
                    setCharacters((prevCharacters) => prevCharacters.map((c) => character.id === c.id ? response.data : c));
                    navigate('/characters');
                    return true;
                }).catch(() => {
                    return false;
            }),
            {
                pending: 'Speichere Charakter...',
                error: 'Fehler beim Speichern des Charakters',
                success: 'Charakter erfolgreich gespeichert'
            }
        );
    };

    return (
      <div>
          <form onSubmit={handleSubmit}>
              <div className="input-widget input-name">
                  <label htmlFor="name">Name</label>
                  <input
                      type="text"
                      name="name"
                      value={changedCharacter.name}
                      onChange={(e: ChangeEvent<HTMLInputElement>) => setChangedCharacter({ ...changedCharacter, name: e.currentTarget.value })}
                  />
              </div>
              <div className="input-widget">
                  <label htmlFor="profession">Beruf/Aufgabe</label>
                  <CreatableSelect
                      name="profession"
                      value={{value: changedCharacter.profession, label: changedCharacter.profession}}
                      options={professions}
                      onChange={(newValue) => setChangedCharacter({ ...changedCharacter, profession: newValue?.value ?? '' })}
                      classNamePrefix="select"
                  />
              </div>
              <div className="input-widget">
                  <label htmlFor="age">Alter</label>
                  <input
                      type="text"
                      name="age"
                      value={changedCharacter.age}
                      onChange={(e: ChangeEvent<HTMLInputElement>) => setChangedCharacter({ ...changedCharacter, age: e.currentTarget.value })}
                  />
              </div>
              {(villageOptions && villageOptions.length > 0) ? (
                  <div  className="input-widget input-village">
                    <label htmlFor="village">Dorf</label>
                    <Select<{ value: string; label: string }>
                        name="village"
                        options={villageOptions}
                        value={villageOptions.find((option) => option.value === changedCharacter.villageId)}
                        onChange={(newValue) => setChangedCharacter({...changedCharacter, villageId: newValue?.value ?? ''})}
                        classNamePrefix="select"
                    />
                  </div>
              ) : ("")}

              <div className="character-actions">
                <button type="submit"><FontAwesomeIcon icon={faSave}/> Speichern</button>
                <button type="button" onClick={() => navigate('/characters')}><FontAwesomeIcon icon={faClose}/> Abbrechen</button>
              </div>
          </form>
      </div>
    );
}

export default CharacterEdit;