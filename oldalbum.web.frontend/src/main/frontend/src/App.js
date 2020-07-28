import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Switch, Route, NavLink } from 'react-router-dom';
import { ConnectedRouter as Router } from 'connected-react-router';
import logo from './logo.svg';
import './App.css';
import Home from './components/Home';
import Album from './components/Album';
import DynamicComponentsData from './DynamicComponentsData';
import DynamicComponent from './DynamicComponent';

class App extends Component {
    render() {
        const { history, allroutes } = this.props;
        let dynamicComponents = DynamicComponentsData;

        return (
            <Router history={history}>
                <div>
                    <div>
                        { dynamicComponents.map((item, index) => <div key={index}><NavLink exact activeClassName="selected" to={item.route}>{item.title}</NavLink></div>) }
                    </div>
                    <div>
                        { dynamicComponents.map((item, index) => <Route exact key={index} path={item.route} component={() => <DynamicComponent title={item.title} content={item.content} />} />) }
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
