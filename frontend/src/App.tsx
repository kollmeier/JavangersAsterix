import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import CharactersPage from './CharactersPage';
import Layout from "./Layout.tsx";
import VillagesPage from "./VillagesPage.tsx";

const App: React.FC = () => {
    return (
        <Router>
            <div>
                <h1>Asterix</h1>
                <Routes>
                    <Route path="/" element={<Layout />}>
                        <Route path="characters//*" element={<CharactersPage />} />
                        <Route path="characters/:characterId/edit" element={<CharactersPage />} />
                        <Route path="characters/:characterId/*" element={<CharactersPage />} />
                        <Route path="villages//*" element={<VillagesPage />} />
                        <Route path="villages/:villageId/edit" element={<VillagesPage />} />
                        <Route path="villages/:villageId/*" element={<VillagesPage />} />
                        <Route path="/" element={<CharactersPage />} />
                    </Route>
                </Routes>
            </div>
        </Router>
    );
};

export default App;