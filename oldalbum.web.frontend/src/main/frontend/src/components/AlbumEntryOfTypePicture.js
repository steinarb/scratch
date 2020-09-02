import React from 'react';
import { NavLink } from 'react-router-dom';
import { pictureTitle, formatMetadata } from './commonComponentCode';
import UpButton from './UpButton';
import LeftButton from './LeftButton';
import DownButton from './DownButton';
import RightButton from './RightButton';


function AlbumEntryOfTypePicture(props) {
    const { key, entry, className='' } = props;
    const title = pictureTitle(entry);
    const metadata = formatMetadata(entry);
    return (
        <div key={key} className={className}>
            <div className="d-sm-none">
                <div className="btn btn-primary left-align-cell">
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
            </div>
            <div className="d-none d-sm-block">
                <div className="btn btn-primary">
                    <div className="row align-items-center">
                        <div className="btn-group-vertical">
                            <LeftButton item={entry} />
                        </div>
                        <div className="col-auto">
                            <NavLink className="btn btn-block btn-primary left-align-cell" to={entry.path}>
                                <img className="img-thumbnail" src={entry.thumbnailUrl}/>
                                <div className="mx-1 container">
                                    <div className="row">{title}</div>
                                    <div className="row text-nowrap">{metadata}</div>
                                </div>
                            </NavLink>
                        </div>
                        <div className="btn-group-vertical">
                            <RightButton item={entry} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AlbumEntryOfTypePicture;
