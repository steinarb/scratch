import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronLeft } from './ChevronLeft';

export function StyledLinkLeft(props) {
    const { className = '' } = props;
    return (
        <Link className={className + ' text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white'} to={props.to} >
            <ChevronLeft/>&nbsp; {props.children}
        </Link>
    );
}
