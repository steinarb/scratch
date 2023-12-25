import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    SET_ALBUM_GROUP_BY_YEAR,
    UNSET_ALBUM_GROUP_BY_YEAR,
} from '../reduxactions';

export default function AlbumGroupByYearButton(props) {
    const { album } = props;
    const { id } = album;
    const text = useSelector(state => state.displayTexts);
    const albumGroupByYear = useSelector(state => !!state.albumGroupByYear[id]);
    const dispatch = useDispatch();

    if (albumGroupByYear) {
        return (
            <div className="{props.styleName} alert" role="alert">
                <span onClick={() => dispatch(UNSET_ALBUM_GROUP_BY_YEAR(id))}>{text.albumdontgroupbyyear}</span>
            </div>
        );
    }

    return(
        <div className="alert" role="alert">
            <span onClick={() => dispatch(SET_ALBUM_GROUP_BY_YEAR(id))}>{text.albumgroupbyyear}</span>
        </div>
    );
}
