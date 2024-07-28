import React from 'react';
import { NavLink } from 'react-router-dom';
import { pictureTitle, formatMetadata } from './commonComponentCode';
import LeftButton from './LeftButton';
import RightButton from './RightButton';
import UpButton from './UpButton';
import DownButton from './DownButton';
import ThumbnailImg from './ThumbnailImg';
import PictureCheckbox from './PictureCheckbox';


function AlbumEntryOfTypePicture(props) {
    const { entry, className='' } = props;
    const title = pictureTitle(entry);
    const metadata = formatMetadata(entry);
    const anchor = entry.path.split('/').pop();

    return (
        <div id={anchor} className={className + ' col-sm-12 col-md-4 col-lg-3 col-xxl-2 mb-1 album-entry album-scroll-below-fixed-header'}>
            <div className="column w-100 btn btn-light">
                <div className="d-none d-md-flex">
                    <LeftButton item={entry} />
                    <PictureCheckbox className="left-align-cell" entry={entry} />
                    <div className="flex-grow-1"/>
                    <RightButton item={entry} />
                </div>
                <div className="d-flex d-md-none">
                    <PictureCheckbox className="align-self-start" entry={entry} />
                    <UpButton className="align-self-center m-auto" item={entry} />
                </div>
                <div className="row w-100">
                    <NavLink className=' btn btn-block left-align-cell' to={entry.path}>
                        <ThumbnailImg entry={entry} />
                        <div className="container">
                            <div className="row">{title}</div>
                            <div className="row text-nowrap">{metadata}</div>
                        </div>
                    </NavLink>
                </div>
                <div className="d-flex d-md-none">
                    <DownButton className="align-self-center m-auto" item={entry} />
                </div>
            </div>
        </div>
    );
}

export default AlbumEntryOfTypePicture;
