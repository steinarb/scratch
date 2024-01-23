import React from 'react';

export function Container(props) {
    return (
        <div className="container mx-auto">
            {props.children}
        </div>
    );
}
