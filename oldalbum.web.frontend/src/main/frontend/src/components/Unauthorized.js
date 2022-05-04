import React from 'react';
import { Redirect } from 'react-router';
import { connect } from 'react-redux';
import {
    LOGOUT_REQUEST,
} from '../reduxactions';


function Unauthorized(props) {
    const { haveReceivedInitialLoginStatus, username, loginresult, onLogout } = props;
    if (haveReceivedInitialLoginStatus && !loginresult.success) {
        return <Redirect to="/login" />;
    }

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary left-align-cell" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Go home!</a>
                <h1>Unauthorized</h1>
                <div className="col-sm-2"></div>
            </nav>
            <div className="container">
                <p>Hi {username}! You do not have access to modify the album</p>
                <p>Click &quot;Go home&quot; to navigate to the top of the application, or log out to log back in with a user that is allowed to modify the album</p>
                <form onSubmit={ e => e.preventDefault() }>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button
                                className="btn btn-primary"
                                onClick={onLogout}>
                                Log out</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

const mapStateToProps = state => {
    const { haveReceivedInitialLoginStatus, username, loginresult } = state;
    return {
        haveReceivedInitialLoginStatus,
        username,
        loginresult,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Unauthorized);
