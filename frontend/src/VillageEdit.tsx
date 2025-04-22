import {ChangeEvent, useEffect, useState } from "react";
import VillageInputDTO from "./interfaces/VillageInputDTO.ts";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Village, {implementsVillage} from "./interfaces/Village.ts";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faClose } from "@fortawesome/free-solid-svg-icons/faClose";
import {toast} from "react-toastify";
import { faSave } from "@fortawesome/free-solid-svg-icons/faSave";
import Select from "react-select";

interface Props {
    setVillages: React.Dispatch<React.SetStateAction<Village[]>>
    setEditingVillage?: React.Dispatch<React.SetStateAction<Village | null>>
    village: Village;
    characterOptions: { label: string; options: { value: string; label: string }[]}[];
}

const VillageEdit = ({ setVillages, village, characterOptions }: Props) => {
    const [changedVillage, setChangedVillage] = useState<VillageInputDTO>({
        name: '',
        characterIds: []
    });

    const navigate = useNavigate();

    useEffect(() => {
        setChangedVillage({
            name: village.name,
            characterIds: village.characters?.map((character) => character.id) ?? []
        });
    }, [village]);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        return toast.promise(
            axios.put('/api/asterix/villages/update/' + village.id, changedVillage)
                .then((response) => {
                    if (implementsVillage(response.data)) {
                        const changedVillage: Village = response.data;
                        setVillages((prevVillages) =>
                            prevVillages
                                .map((v) =>
                                    village.id === v.id
                                    ? changedVillage
                                    : {
                                        ...v,
                                        characters: v.characters
                                            ?.filter(c1 =>
                                                changedVillage.characters
                                                    ?.every(c2 => c2.id !== c1.id)
                                            )
                                    }
                        ));
                        navigate('/villages');
                        return true;
                    }
                    return false;
                }).catch(() => {
                    return false;
            }),
            {
                pending: 'Speichere Dorf...',
                error: 'Fehler beim Speichern des Dorfs',
                success: 'Dorf erfolgreich gespeichert'
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
                      value={changedVillage.name}
                      onChange={(e: ChangeEvent<HTMLInputElement>) => setChangedVillage({ ...changedVillage, name: e.currentTarget.value })}
                  />
              </div>
              {(characterOptions && characterOptions.length > 0) ? (
                  <div  className="input-widget input-inhabitants">
                      <label htmlFor="inhabitants">Einwohner</label>
                      <Select
                          name="inhabitants"
                          options={characterOptions}
                          isMulti={true}
                          classNamePrefix="select"
                          value={
                              characterOptions
                                  .flatMap((group) => group.options)
                                  .filter((option) => changedVillage.characterIds.includes(option.value))
                          }
                          onChange={(newValue) => setChangedVillage({...changedVillage, characterIds: newValue.map((option) => option.value)})}
                      />
                  </div>
              ) : ( "" )}
              <div className="village-actions">
                <button type="submit"><FontAwesomeIcon icon={faSave}/> Speichern</button>
                <button type="button" onClick={() => navigate('/villages')}><FontAwesomeIcon icon={faClose}/> Abbrechen</button>
              </div>
          </form>
      </div>
    );
}

export default VillageEdit;