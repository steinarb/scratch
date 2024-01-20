import React from 'react';

export function Header(props) {
    return (
        <header>
            <div>
                {props.children}
            </div>
        </header>
    );
}
