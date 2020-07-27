import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Switch, Route, NavLink } from 'react-router-dom';
import { ConnectedRouter as Router } from 'connected-react-router';
import logo from './logo.svg';
import './App.css';
import Home from './components/Home';
import Album from './components/Album';

class App extends Component {
    render() {
        const { history, allroutes } = this.props;

        return (
            <Router history={history}>
                <div>
                    <Route exact key="355" path="/oldalbum/" component={Home} />
                    { allroutes.map( (item, index) => <Route exact key={index} path={item.path} component={() => <Album id={item.id}/>}/> )}
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
