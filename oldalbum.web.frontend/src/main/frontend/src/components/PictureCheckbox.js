import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_PICTURE_ALBUMENTRY, UNSELECT_PICTURE_ALBUMENTRY } from '../reduxactions';

export default function PictureCheckbox(props) {
    const { entry, className='' } = props;
    const pictureIsSelected = useSelector(state => state.selectedentries.findIndex(e => e.id === entry.id) > -1);
    const dispatch = useDispatch();
    const completeClassName = className + ' picture-checkbox';

    return (
        <input type="radio" className={completeClassName} checked={pictureIsSelected} onChange={e => e.target.checked ? dispatch(SELECT_PICTURE_ALBUMENTRY(entry)) : dispatch(UNSELECT_PICTURE_ALBUMENTRY(entry))}/>
    );
}
