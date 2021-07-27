import React from 'react';
import { Redirect } from 'react-router';
import { connect } from 'react-redux';
import { Container } from './bootstrap/Container';


function Home(props) {
    const {
        loginresultat,
    } = props;
    if (!loginresultat.authorized) {
        return <Redirect to="/handlelapp/unauthorized" />;
    }

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary left-align-cell" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Gå hjem!</a>
                <h1>Handlelapp</h1>
            </nav>
            <Container>
                <p>Hei !</p>
            </Container>
        </div>
    );
}

const mapStateToProps = state => {
    const { loginresultat } = state;
    return {
        loginresultat,
    };
};

const mapDispatchToProps = () => {
    return {
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Home);
