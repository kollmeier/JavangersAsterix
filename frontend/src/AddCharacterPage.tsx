import React, { useState } from 'react';
import axios from 'axios';
import CharacterInputDTO from "./interfaces/CharacterInputDTO.ts";

const AddCharacterPage: React.FC = () => {
    const [newCharacter, setNewCharacter] = useState<CharacterInputDTO>({
        name: '',
        profession: '',
        age: '',
    });

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        try {
            await axios.post('/api/asterix/characters/add', newCharacter);
            alert('Character added!');
            setNewCharacter({ name: '', profession: '', age: '' });
        } catch (error) {
            alert('Error adding character');
        }
    };

    return (
        <div>
            <h2>Add Character</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Name"
                    value={newCharacter.name}
                    onChange={(e) => setNewCharacter({ ...newCharacter, name: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Profession"
                    value={newCharacter.profession}
                    onChange={(e) => setNewCharacter({ ...newCharacter, profession: e.target.value })}
                />
                <input
                    type="number"
                    placeholder="Age"
                    value={newCharacter.age}
                    onChange={(e) => setNewCharacter({ ...newCharacter, age: e.target.value })}
                />
                <button type="submit">Add</button>
            </form>
        </div>
    );
};

export default AddCharacterPage;