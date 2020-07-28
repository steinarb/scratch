import React, { Component } from 'react';
import { connect } from 'react-redux';
function DynamicComponent(props) {
    const {title, content} = props;
    return (
        <div>
            <h1>{title}</h1>
            <p>{content}</p>
        </div>
    );
}

function mapStateToProps(state) {
    return {};
}

export default connect(mapStateToProps)(DynamicComponent);
