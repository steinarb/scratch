import React, { Component } from 'react';
function DynamicComponent(props) {
    const {title, content} = props;
    return (
        <div>
            <h1>{title}</h1>
            <p>{content}</p>
        </div>
    );
}
export default DynamicComponent;
