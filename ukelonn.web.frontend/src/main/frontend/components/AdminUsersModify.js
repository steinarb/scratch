import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import Users from './Users';
import Amount from './Amount';

class AdminUsersModify extends Component {
    constructor(props) {
        super(props);
        this.state = {...props};
    }

    componentDidMount() {
        this.props.onUserList();
    }

    componentWillReceiveProps(props) {
        this.setState({...props});
    }

    render() {
        let {
            haveReceivedResponseFromLogin,
            loginResponse,
            users,
            usersMap,
            user,
            onUsersFieldChange,
            onFieldChange,
            onSaveUpdatedUser,
            onLogout,
        } = this.state;

        if (haveReceivedResponseFromLogin && loginResponse.roles.length === 0) {
            return <Redirect to="/ukelonn/login" />;
        }

        return (
            <div>
                <h1>Endre brukere</h1>
                <br/>
                <Link to="/ukelonn/admin/users">Administer brukere</Link>
                <br/>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <label htmlFor="users">Velg bruker</label>
                    <Users id="users" users={users} usersMap={usersMap} value={user.fullname} onUsersFieldChange={onUsersFieldChange} />
                    <br/>
                    <label htmlFor="username">Brukernavn</label>
                    <input id="username" type="text" value={user.username} onChange={(event) => onFieldChange({username: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="email">Epostadresse</label>
                    <input id="email" type="text" value={user.email} onChange={(event) => onFieldChange({email: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="firstname">Fornavn</label>
                    <input id="firstname" type="text" value={user.firstname} onChange={(event) => onFieldChange({firstname: event.target.value}, user)} />
                    <br/>
                    <label htmlFor="lastname">Etternavn</label>
                    <input id="lastname" type="text" value={user.lastname} onChange={(event) => onFieldChange({lastname: event.target.value}, user)} />
                    <br/>
                    <button onClick={() => onSaveUpdatedUser(user)}>Lagre endringer av bruker</button>
                </form>
                <br/>
                <button onClick={() => onLogout()}>Logout</button>
            </div>
        );
    };
};

const mapStateToProps = state => {
    return {
        haveReceivedResponseFromLogin: state.ukelonn.haveReceivedResponseFromLogin,
        loginResponse: state.ukelonn.loginResponse,
        users: state.ukelonn.users,
        usersMap: new Map(state.ukelonn.users.map(i => [i.fullname, i])),
        user: state.ukelonn.user,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onUserList: () => dispatch({ type: 'USERS_REQUEST' }),
        onUsersFieldChange: (selectedValue, usersMap) => {
            let user = usersMap.get(selectedValue);
            let changedField = {
                user: {...user},
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onFieldChange: (formValue, user) => {
            let changedField = {
                user: { ...user, ...formValue }
            };
            dispatch({ type: 'UPDATE', data: changedField });
        },
        onSaveUpdatedUser: (user) => dispatch({ type: 'MODIFY_USER_REQUEST', user }),
        onLogout: () => dispatch({ type: 'LOGOUT_REQUEST' }),
    };
};

AdminUsersModify = connect(mapStateToProps, mapDispatchToProps)(AdminUsersModify);

export default AdminUsersModify;
