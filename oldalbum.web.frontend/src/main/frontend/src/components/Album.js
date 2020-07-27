import React from 'react';
import { connect } from 'react-redux';

function Album(props) {
    return (<div><h1>Album</h1></div>);
}

function mapStateToProps(state) {
    return {};
}

export default connect(mapStateToProps)(Album);
