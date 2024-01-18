import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { ALBUM_SELECT_ALL } from '../reduxactions';

export default function AlbumSelectAllButton(props) {
    const { album } = props;
    const text = useSelector(state => state.displayTexts);
    const dispatch = useDispatch();

    return(
        <div className={props.className}>
            <span onClick={() => dispatch(ALBUM_SELECT_ALL(album))}>{text.selectall}</span>
        </div>
    );
}
