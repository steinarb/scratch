import React, { Component } from 'react';
import { useGetLogintilstandQuery } from './api';
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

export default function App(props) {
    const { history, basename } = props;
    useGetLogintilstandQuery();

    return (
        <Router history={history} basename={basename}>
            <Routes>
                <Route exact path="/" element={<Home/>} />
                <Route exact path="/hurtigregistrering" element={<Hurtigregistrering/>} />
                <Route exact path="/statistikk/sumbutikk" element={<StatistikkSumbutikk/>} />
                <Route exact path="/statistikk/handlingerbutikk" element={<StatistikkHandlingerbutikk/>} />
                <Route exact path="/statistikk/sistehandel" element={<StatistikkSistehandel/>} />
                <Route exact path="/statistikk/sumyearmonth" element={<StatistikkSumyearmonth/>} />
                <Route exact path="/statistikk/sumyear" element={<StatistikkSumyear/>} />
                <Route exact path="/statistikk" element={<Statistikk/>} />
                <Route exact path="/favoritter/leggtil" element={<FavoritterLeggTil/>} />
                <Route exact path="/favoritter/slett" element={<FavoritterSlett/>} />
                <Route exact path="/favoritter/sorter" element={<FavoritterSorter/>} />
                <Route exact path="/favoritter" element={<Favoritter/>} />
                <Route exact path="/nybutikk" element={<NyButikk/>} />
                <Route exact path="/endrebutikk" element={<EndreButikk/>} />
                <Route exact path="/login" element={<Login/>} />
                <Route exact path="/unauthorized" element={<Unauthorized/>} />
            </Routes>
        </Router>
    );
}
