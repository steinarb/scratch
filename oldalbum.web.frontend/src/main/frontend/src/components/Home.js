import React from 'react';
import { Link, NavLink } from 'react-router-dom';

function Home(props) {
    return (
        <div>
            <h1>Hello old album world</h1>
            <Link to="/oldalbum/pics">pics</Link>
        </div>
    );
}

export default Home;
