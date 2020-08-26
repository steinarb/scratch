import React from 'react';
import { NavLink } from 'react-router-dom';
import ChevronRight from './bootstrap/ChevronRight';

export default function Next(props) {
    const { className = '', next } = props;
    if (!next) {
        return null;
    }

    return (
        <div className={className + ' btn-group'}>
            <NavLink className="btn" to={next.path}><ChevronRight/></NavLink>
        </div>
    );
}
