import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Switch, Route, NavLink } from 'react-router-dom';
import { ConnectedRouter as Router } from 'connected-react-router';
import logo from './logo.svg';
import './App.css';
import Home from './components/Home';
import Album from './components/Album';
import Picture from './components/Picture';
import Login from './components/Login';
import ModifyAlbum from './components/ModifyAlbum';
import AddAlbum from './components/AddAlbum';
import ModifyPicture from './components/ModifyPicture';

class App extends Component {
    render() {
        const { history, allroutes } = this.props;
        const webcontext = '/oldalbum';

        return (
            <Router history={history}>
                <div>
                    <div>
                        { allroutes.map((item, index) => <Route exact key={index} path={item.path} component={() => albumOrPicture(item)} />) }
                        <Route exact key="login" path="/oldalbum/login" component={Login} />
                        <Route key="modifyalbum" path='/oldalbum/modifyalbum' component={ModifyAlbum} />
                        <Route key="addalbum" path='/oldalbum/addalbum' component={AddAlbum} />
                        <Route key="modifypicture" path='/oldalbum/modifypicture' component={ModifyPicture} />
                    </div>
                </div>
            </Router>
        );
    }
}


function mapStateToProps(state) {
    const allroutes = state.allroutes || [];
    return {
        allroutes,
    };
}

export default connect(mapStateToProps)(App);

function albumOrPicture(item) {
    if (item.album) {
        return <Album item={item} />;
    }

    return <Picture item={item} />;
}
