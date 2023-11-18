import React from 'react';
import { NavLink } from 'react-router-dom';
import { pictureTitle, formatLastModifiedDate } from './commonComponentCode';
import LeftButton from './LeftButton';
import RightButton from './RightButton';
import UpDownButton from './UpDownButton';
import ThumbnailImg from './ThumbnailImg';


function AlbumEntryOfTypePicture(props) {
    const { entry, className='' } = props;
    const title = pictureTitle(entry);
    const lastModifiedDate = formatLastModifiedDate(entry);
    const anchor = 'entry' + entry.id.toString();

    return (
        <div id={anchor} className={className + ' col-sm-12 col-md-4 col-lg-3 col-XL-2 album-entry btn btn-primary mx-1 my-1'}>
            <LeftButton item={entry} />
            <NavLink className=' btn btn-primary btn-block left-align-cell' to={entry.path}>
                <div className="container">
                    <div className="row">{title}</div>
                    <ThumbnailImg entry={entry} />
                    <div className="row text-nowrap">{lastModifiedDate}</div>
                </div>
            </NavLink>
            <RightButton item={entry} />
            <UpDownButton item={entry} />
        </div>
    );
}

export default AlbumEntryOfTypePicture;
