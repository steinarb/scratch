import React from 'react';
import { NavLink } from 'react-router-dom';
import { pictureTitle, formatMetadata } from './commonComponentCode';
import LeftButton from './LeftButton';
import RightButton from './RightButton';
import UpDownButton from './UpDownButton';
import ThumbnailImg from './ThumbnailImg';
import PictureCheckbox from './PictureCheckbox';


function AlbumEntryOfTypePicture(props) {
    const { entry, className='' } = props;
    const title = pictureTitle(entry);
    const metadata = formatMetadata(entry);
    const anchor = 'entry' + entry.id.toString();

    return (
        <div id={anchor} className={className + ' col-sm-12 col-md-4 col-lg-3 col-xxl-2 album-entry album-scroll-below-fixed-header'}>
            <LeftButton item={entry} />
            <div className="column w-100">
                <PictureCheckbox className="left-align-cell" entry={entry} />
                <NavLink className=' btn btn-light btn-block left-align-cell' to={entry.path}>
                    <ThumbnailImg entry={entry} />
                    <div className="mx-1 container">
                        <div className="row">{title}</div>
                        <div className="row text-nowrap">{metadata}</div>
                    </div>
                </NavLink>
            </div>
            <RightButton item={entry} />
            <UpDownButton item={entry} />
        </div>
    );
}

export default AlbumEntryOfTypePicture;
