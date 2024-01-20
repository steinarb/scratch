import React from 'react';

export function FormLabel(props) {
    return (
        <label htmlFor={props.htmlFor}>{props.children}</label>
    );
}
