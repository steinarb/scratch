import React from 'react';
import { useDispatch } from 'react-redux';
import { SELECT_PICTURE_ALBUMENTRY, UNSELECT_PICTURE_ALBUMENTRY } from '../reduxactions';

export default function PictureCheckbox(props) {
    const { entry, className='' } = props;
    const dispatch = useDispatch();
    const completeClassName = className + ' picture-checkbox';

    return (
        <input type="checkbox" className={completeClassName} onChange={e => e.target.checked ? dispatch(SELECT_PICTURE_ALBUMENTRY(entry)) : dispatch(UNSELECT_PICTURE_ALBUMENTRY(entry))}/>
    );
}
