import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight } from './ChevronRight';

export function StyledLinkRight(props) {
    const { className = '' } = props;
    return (
        <Link className={className} to={props.to} >
            {props.children} &nbsp;<ChevronRight/>
        </Link>
    );
}
