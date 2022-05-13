import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { pictureTitle, viewSize } from './commonComponentCode';
import UpButton from './UpButton';
import LeftButton from './LeftButton';
import DownButton from './DownButton';
import RightButton from './RightButton';
import Thumbnail from './Thumbnail';

export default function AlbumEntryOfTypeAlbum(props) {
    const { key, entry, className='' } = props;
    const childentries = useSelector(state => state.childentries || {});
    const children = childentries[entry.id] || [];
    const title = pictureTitle(entry);
    const viewportSize = viewSize();
    const setWidth = (viewportSize === 'sm' || viewportSize === 'xs') ? ' w-100' : '';
    const noOfThumbnails = title.length > 29 ? 3 : 2;
    const childrenWithThumbnails = findChildrenThumbnails(entry, children, childentries).slice(0, noOfThumbnails);

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
                <div className="btn btn-primary d-flex">
                    <div className="btn-group-vertical">
                        <LeftButton item={entry} />
                    </div>
                    <div className="col-auto">
                        <NavLink className="btn-primary p-2 text-left" to={entry.path}>Album: {title}</NavLink>
                        <div className="d-flex">
                            { childrenWithThumbnails.map(c => <Thumbnail key={'entry_' + c.id} entry={c} />) }
                        </div>
                    </div>
                    <div className="btn-group-vertical">
                        <RightButton item={entry} />
                    </div>
                </div>
            </div>
        </div>
    );
}

function findChildrenThumbnails(entry, children, childitems) {
    // First try to find thumbnails of direct children of the album
    const directChildren = children.filter(c => !c.album).filter(c => c.thumbnailUrl || c.imageUrl).sort((a,b) => a.sort - b.sort);
    if (directChildren.length) { return directChildren; }

    // If the children of the album have no thumbnails, find the first thumbnail of each child
    // and set the navigation link on thumbnails the to the album itself
    const indirectChildren = children
          .filter(c => c.album)
          .sort((a,b) => a.sort - b.sort)
          .map(c => childitems[c.id].slice().sort((a,b) => a.sort - b.sort).find(t => t.thumbnailUrl || c.imageUrl))
          .map(c => ( { ...c, path: entry.path } ));
    return indirectChildren;
}
