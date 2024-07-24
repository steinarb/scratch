import React from 'react';
import { useSelector } from 'react-redux';
import { Routes, Route } from 'react-router-dom';
import { HistoryRouter as Router } from "redux-first-history/rr6";
import './App.css';
import Album from './components/Album';
import Picture from './components/Picture';
import Unauthorized from './components/Unauthorized';
import ModifyAlbum from './components/ModifyAlbum';
import AddAlbum from './components/AddAlbum';
import ModifyPicture from './components/ModifyPicture';
import AddPicture from './components/AddPicture';
import LoadingOrNotFound from './components/LoadingOrNotFound';
import PasswordProtectedWarningDialog from './components/PasswordProtectedWarningDialog';

export default function App(props) {
    const { history, basename } = props;
    const allroutes = useSelector(state => state.allroutes);

    return (
        <div>
            <PasswordProtectedWarningDialog/>
            <Router history={history} basename={basename}>
                <Routes >
                    { allroutes.map((item, index) => <Route exact key={index} path={item.path} element={albumOrPicture(item)} />) }
                    <Route exact key="unauthorized" path="/unauthorized" element={<Unauthorized/>} />
                    <Route key="modifyalbum" path="/modifyalbum" element={<ModifyAlbum/>} />
                    <Route key="addalbum" path="/addalbum" element={<AddAlbum/>} />
                    <Route key="modifypicture" path="/modifypicture" element={<ModifyPicture/>} />
                    <Route key="addpicture" path="/addpicture" element={<AddPicture/>} />
                    <Route key="loadingornotfound" path="*" element={<LoadingOrNotFound/>} />
                </Routes>
            </Router>
        </div>
    );
}

function albumOrPicture(item) {
    if (item.album) {
        return <Album item={item} />;
    }

    return <Picture item={item} />;
}
