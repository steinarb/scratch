import React from 'react';
import { useSelector } from 'react-redux';
import { Route } from 'react-router-dom';
import { ConnectedRouter as Router } from 'connected-react-router';
import './App.css';
import Album from './components/Album';
import Picture from './components/Picture';
import Login from './components/Login';
import Unauthorized from './components/Unauthorized';
import ModifyAlbum from './components/ModifyAlbum';
import AddAlbum from './components/AddAlbum';
import ModifyPicture from './components/ModifyPicture';
import AddPicture from './components/AddPicture';

export default function App(props) {
    const { history } = props;
    const allroutes = useSelector(state => state.allroutes);

    return (
        <Router history={history}>
            <div>
                <div>
                    { allroutes.map((item, index) => <Route exact key={index} path={item.path} component={() => albumOrPicture(item)} />) }
                    <Route exact key="login" path="/login" component={Login} />
                    <Route exact key="unauthorized" path="/unauthorized" component={Unauthorized} />
                    <Route key="modifyalbum" path='/modifyalbum' component={ModifyAlbum} />
                    <Route key="addalbum" path='/addalbum' component={AddAlbum} />
                    <Route key="modifypicture" path='/modifypicture' component={ModifyPicture} />
                    <Route key="addpicture" path='/addpicture' component={AddPicture} />
                </div>
            </div>
        </Router>
    );
}

function albumOrPicture(item) {
    if (item.album) {
        return <Album item={item} />;
    }

    return <Picture item={item} />;
}
