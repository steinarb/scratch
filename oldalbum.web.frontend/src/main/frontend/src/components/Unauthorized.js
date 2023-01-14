import React from 'react';
import { NavLink } from 'react-router-dom';
import { Navigate } from 'react-router';
import { useSelector, useDispatch } from 'react-redux';
import { LOGOUT_REQUEST } from '../reduxactions';
import EditModeButton from './EditModeButton';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';


export default function Unauthorized() {
    const text = useSelector(state => state.displayTexts);
    const haveReceivedInitialLoginStatus = useSelector(state => state.haveReceivedInitialLoginStatus);
    const username = useSelector(state => state.username);
    const loggedIn = useSelector(state => state.loggedIn);
    const dispatch = useDispatch();
    if (haveReceivedInitialLoginStatus && !loggedIn) {
        return <Navigate to="/login" />;
    }

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <NavLink className="btn btn-primary left-align-cell" to="/"><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Go home!</NavLink>
                <h1>{text.unauthorized}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
                        <CopyLinkButton className="nav-item" />
                        <EditModeButton className="nav-item" />
                        <LoginLogoutButton className="nav-item" item={{}}/>
                    </div>
                </div>
            </nav>
            <div className="container">
                <p>{text.hi} {username}! {text.unauthorizedtomodifyalbum}</p>
                <p>{text.navigatetotoporlogout}</p>
                <form onSubmit={ e => e.preventDefault() }>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={() => dispatch(LOGOUT_REQUEST())}>
                                {text.logout}</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}
