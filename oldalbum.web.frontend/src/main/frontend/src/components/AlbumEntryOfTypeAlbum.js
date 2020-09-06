import React from 'react';
import { NavLink } from 'react-router-dom';
import { pictureTitle, viewSize } from './commonComponentCode';
import UpButton from './UpButton';
import LeftButton from './LeftButton';
import DownButton from './DownButton';
import RightButton from './RightButton';

function AlbumEntryOfTypeAlbum(props) {
    const { key, entry, className='' } = props;
    const title = pictureTitle(entry);
    const viewportSize = viewSize();
    const setWidth = (viewportSize === 'sm' || viewportSize === 'xs') ? ' w-100' : '';

    return (
        <div key={key} className={className + setWidth}>
            <div className="d-sm-none">
                <div className="btn btn-primary d-flex">
                    <NavLink className="btn-primary mr-auto text-left" to={entry.path}>Album: {title}</NavLink>
                    <div className="btn-group-vertical">
                        <UpButton item={entry} />
                        <DownButton item={entry} />
                    </div>
                </div>
            </div>
            <div className="d-none d-sm-block">
                <div className="btn btn-primary">
                    <div className="btn-group-vertical">
                        <LeftButton item={entry} />
                    </div>
                    <NavLink className="btn-primary p-2 mr-auto text-left" to={entry.path}>Album: {title}</NavLink>
                    <div className="btn-group-vertical">
                        <RightButton item={entry} />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AlbumEntryOfTypeAlbum;
