import React from 'react';
import { NavLink } from 'react-router-dom';
import { pictureTitle, formatMetadata } from './commonComponentCode';
import UpButton from './UpButton';
import DownButton from './DownButton';

function AlbumEntryOfTypePicture(props) {
    const { key, entry, className='' } = props;
    const title = pictureTitle(entry);
    const metadata = formatMetadata(entry);
    return (
        <div key={key} className={className + ' btn btn-block btn-primary left-align-cell'}>
            <NavLink className="btn btn-block btn-primary left-align-cell" to={entry.path}>
                <img className="img-thumbnail" src={entry.thumbnailUrl}/>
                <div className="mx-1 container">
                    <div className="row">{title}</div>
                    <div className="row text-nowrap">{metadata}</div>
                </div>
            </NavLink>
            <div className="btn-group-vertical">
                <UpButton item={entry} />
                <DownButton item={entry} />
            </div>
        </div>
    );
}

export default AlbumEntryOfTypePicture;
