import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_LOCALE } from '../reduxactions';

export default function Locale(props) {
    const { className } = props;
    const locale = useSelector(state => state.locale);
    const availableLocales = useSelector(state => state.availableLocales);
    const dispatch = useDispatch();
    const completeClassName = (className || '') + ' bg-light form-select h-25';

    return (
        <select className={completeClassName} onChange={e => dispatch(SELECT_LOCALE(e.target.value))} value={locale}>
            {availableLocales.map((l) => <option key={'locale_' + l.code} value={l.code}>{l.displayLanguage}</option>)}
        </select>
    );
}
