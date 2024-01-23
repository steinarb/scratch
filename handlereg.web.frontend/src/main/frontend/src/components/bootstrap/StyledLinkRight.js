import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight } from './ChevronRight';

export function StyledLinkRight(props) {
    const { className = '' } = props;
    return (
        <Link className={className + ' text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white'} to={props.to} >
            {props.children} &nbsp;<ChevronRight/>
        </Link>
    );
}
