import React from 'react';
import { useSelector } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { pictureTitle } from './commonComponentCode';
import LeftButton from './LeftButton';
import RightButton from './RightButton';
import UpDownButton from './UpDownButton';
import Thumbnail from './Thumbnail';

export default function AlbumEntryOfTypeAlbum(props) {
    const { entry, className='' } = props;
    const childentries = useSelector(state => state.childentries || {});
    const children = childentries[entry.id] || [];
    const title = pictureTitle(entry);
    const noOfThumbnails = title.length > 21 ? 3 : 2;
    const childrenWithThumbnails = findChildrenThumbnails(entry, children, childentries).slice(0, noOfThumbnails);
    const widthInCols =  noOfThumbnails===2 ? ' col-sm-12 col-md-4 col-lg-3 col-xl-2' : ' col-sm-12 col-md-5 col-lg-4 col-xl-3';
    const anchor = 'entry' + entry.id.toString();

    return (
        <div id={anchor} className={className + widthInCols + ' album-entry-album btn btn-light mx-1 my-1 album-scroll-below-fixed-header'}>
            <LeftButton item={entry} />
            <div className="col-auto">
                <NavLink className="btn btn-light p-2 text-left" to={entry.path}>Album: {title}</NavLink>
                <div className="d-none d-md-flex">
                    { childrenWithThumbnails.map(c => <Thumbnail key={'entry_' + c.id} entry={c} />) }
                </div>
            </div>
            <RightButton item={entry} />
            <UpDownButton item={entry} />
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
          .map(c => childitems[c.id] && childitems[c.id].slice().sort((a,b) => a.sort - b.sort).find(t => t.thumbnailUrl || c.imageUrl))
          .map(c => ( { ...c, path: entry.path } ));
    return indirectChildren;
}
