import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronLeft } from './ChevronLeft';

export function StyledLinkLeft(props) {
    return (
        <Link to={props.to} >
            <ChevronLeft/>&nbsp; {props.children}
        </Link>
    );
}
