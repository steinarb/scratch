import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { SELECT_PICTURE_ALBUMENTRY } from '../reduxactions';

export default function PictureCheckbox(props) {
    const { entry, className='' } = props;
    const selectedentries = useSelector(state => state.selectedentries);
    const dispatch = useDispatch();
    const pictureIsSelected = selectedentries.findIndex(e => e.id === entry.id) > -1;
    const completeClassName = className + ' picture-checkbox';

    return (
        <input type="checkbox" className={completeClassName} checked={pictureIsSelected} onChange={e => dispatch(SELECT_PICTURE_ALBUMENTRY({ entry, selected: e.target.checked }))} />
    );
}
