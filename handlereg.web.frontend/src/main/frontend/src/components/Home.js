import React from 'react';
import { Redirect } from 'react-router';
import { connect } from 'react-redux';
import moment from 'moment';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {
    BELOP_ENDRE,
    BUTIKK_ENDRE,
    DATO_ENDRE,
    NYHANDLING_REGISTRER,
} from '../actiontypes';
import { Container } from './bootstrap/Container';
import { StyledLinkRight } from './bootstrap/StyledLinkRight';


function Home(props) {
    const {
        loginresultat,
        oversikt,
        handlinger,
        butikker,
        nyhandling,
        endreBelop,
        endreButikk,
        endreDato,
        onRegistrerHandling
    } = props;
    const favordisfavor = oversikt.balanse >= 0 ? 'favør' : 'disfavør';
    if (!loginresultat.authorized) {
        return <Redirect to="/handlereg/unauthorized" />;
    }

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <a className="btn btn-primary left-align-cell" href="../.."><span className="oi oi-chevron-left" title="chevron left" aria-hidden="true"></span>&nbsp;Gå hjem!</a>
                <h1>Matregnskap</h1>
                <StyledLinkRight className="col-sm-2" to="/handlereg/hurtigregistrering">Hurtig</StyledLinkRight>
            </nav>
            <Container>
                <p>Hei {oversikt.fornavn}! Totalsummen er for øyeblikket {Math.abs(oversikt.balanse)} i din {favordisfavor}.</p>
                <p>Dine 5 siste innkjøp, er:</p>
                <div className="table-responsive table-sm table-striped">
                    <table className="table">
                        <thead>
                            <tr>
                                <th className="transaction-table-col transaction-table-col1">Dato</th>
                                <th className="transaction-table-col transaction-table-col2">Beløp</th>
                                <th className="transaction-table-col transaction-table-col-hide-overflow transaction-table-col3">Butikk</th>
                            </tr>
                        </thead>
                        <tbody>
                            {handlinger.map((handling) =>
                                            <tr key={handling.transactionId}>
                                                <td className="transaction-table-col">{moment(handling.handletidspunkt).format("YYYY-MM-DD")}</td>
                                                <td className="transaction-table-col">{handling.belop}</td>
                                                <td className="transaction-table-col transaction-table-col-hide-overflow">{handling.butikk}</td>
                                            </tr>
                                           )}
                        </tbody>
                    </table>
                </div>
                <form onSubmit={ e => { e.preventDefault(); }}>
                    <div className="form-group row">
                        <label htmlFor="amount" className="col-form-label col-5">Nytt beløp</label>
                        <div className="col-7">
                            <input id="amount" className="form-control" type="number" pattern="\d+" value={nyhandling.belop} onChange={e => endreBelop(e.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="jobtype" className="col-form-label col-5">Velg butikk</label>
                        <div className="col-7">
                            <select value={nyhandling.storeId} onChange={e => endreButikk(e.target.value)}>
                                {butikker.map(butikk => <option key={butikk.storeId} value={butikk.storeId}>{butikk.butikknavn}</option>)}
                            </select>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="date" className="col-form-label col-5">Dato</label>
                        <div className="col-7">
                            <DatePicker
                                selected={nyhandling.handletidspunkt.toDate()}
                                dateFormat="yyyy-MM-dd"
                                onChange={(selectedValue) => endreDato(selectedValue)}
                                onFocus={e => e.target.blur()}
                            />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5"/>
                        <div className="col-7">
                            <button className="btn btn-primary" disabled={nyhandling.belop <= 0} onClick={() => onRegistrerHandling(nyhandling, oversikt.brukernavn)}>Registrer handling</button>
                        </div>
                    </div>
                </form>
            </Container>
            <Container>
                <StyledLinkRight to="/handlereg/statistikk">Statistikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/nybutikk">Ny butikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/endrebutikk">Endre butikk</StyledLinkRight>
                <StyledLinkRight to="/handlereg/favoritter">Favoritter</StyledLinkRight>
            </Container>
        </div>
    );
}

const mapStateToProps = state => {
    const { loginresultat, oversikt, handlinger, butikker, nyhandling } = state;
    return {
        loginresultat,
        oversikt,
        handlinger,
        butikker,
        nyhandling,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        endreBelop: (belop) => dispatch(BELOP_ENDRE(belop)),
        endreButikk: (butikk) => dispatch(BUTIKK_ENDRE(butikk)),
        endreDato: (dato) => dispatch(DATO_ENDRE(dato)),
        onRegistrerHandling: (handling, username) => dispatch(NYHANDLING_REGISTRER({ ...handling, username })),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Home);
