import React, { Component } from 'react';
import { Routes, Route } from 'react-router-dom';
import { HistoryRouter as Router } from "redux-first-history/rr6";
import './App.css';
import Home from './components/Home';
import Hurtigregistrering from './components/Hurtigregistrering';
import Statistikk from './components/Statistikk';
import StatistikkSumbutikk from './components/StatistikkSumbutikk';
import StatistikkHandlingerbutikk from './components/StatistikkHandlingerbutikk';
import StatistikkSistehandel from './components/StatistikkSistehandel';
import StatistikkSumyear from './components/StatistikkSumyear';
import StatistikkSumyearmonth from './components/StatistikkSumyearmonth';
import Favoritter from './components/Favoritter';
import FavoritterLeggTil from './components/FavoritterLeggTil';
import FavoritterSlett from './components/FavoritterSlett';
import FavoritterSorter from './components/FavoritterSorter';
import NyButikk from './components/NyButikk';
import EndreButikk from './components/EndreButikk';
import Login from './components/Login';
import Unauthorized from './components/Unauthorized';

class App extends Component {
    render() {
        const { history } = this.props;

        return (
            <Router history={history}>
                <Routes>
                    <Route exact path="/handlereg/" element={<Home/>} />
                    <Route exact path="/handlereg/hurtigregistrering" element={<Hurtigregistrering/>} />
                    <Route exact path="/handlereg/statistikk/sumbutikk" element={<StatistikkSumbutikk/>} />
                    <Route exact path="/handlereg/statistikk/handlingerbutikk" element={<StatistikkHandlingerbutikk/>} />
                    <Route exact path="/handlereg/statistikk/sistehandel" element={<StatistikkSistehandel/>} />
                    <Route exact path="/handlereg/statistikk/sumyearmonth" element={<StatistikkSumyearmonth/>} />
                    <Route exact path="/handlereg/statistikk/sumyear" element={<StatistikkSumyear/>} />
                    <Route exact path="/handlereg/statistikk" element={<Statistikk/>} />
                    <Route exact path="/handlereg/favoritter/leggtil" element={<FavoritterLeggTil/>} />
                    <Route exact path="/handlereg/favoritter/slett" element={<FavoritterSlett/>} />
                    <Route exact path="/handlereg/favoritter/sorter" element={<FavoritterSorter/>} />
                    <Route exact path="/handlereg/favoritter" element={<Favoritter/>} />
                    <Route exact path="/handlereg/nybutikk" element={<NyButikk/>} />
                    <Route exact path="/handlereg/endrebutikk" element={<EndreButikk/>} />
                    <Route exact path="/handlereg/login" element={<Login/>} />
                    <Route exact path="/handlereg/unauthorized" element={<Unauthorized/>} />
                </Routes>
            </Router>
        );
    }
}

export default App;
