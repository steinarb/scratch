import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { push } from 'redux-first-history';
import { Helmet } from "react-helmet";
import { useSwipeable } from 'react-swipeable';
import { pictureTitle } from './commonComponentCode';
import Locale from './Locale';
import AlbumGroupByYearButton from './AlbumGroupByYearButton';
import AlbumHideShowYearsButton from './AlbumHideShowYearsButton';
import EditModeButton from './EditModeButton';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';
import DownloadButton from './DownloadButton';
import ModifyButton from './ModifyButton';
import AddAlbumButton from './AddAlbumButton';
import AddPictureButton from './AddPictureButton';
import DeleteButton from './DeleteButton';
import SortByDateButton from './SortByDateButton';
import BatchAddPictures from './BatchAddPictures';
import Previous from './Previous';
import Next from './Next';
import AlbumEntryOfTypeAlbum from './AlbumEntryOfTypeAlbum';
import AlbumEntryOfTypePicture from './AlbumEntryOfTypePicture';

export default function Album(props) {
    const { item } = props;
    const text = useSelector(state => state.displayTexts);
    const parent = useSelector(state => (state.albumentries[item.parent] || {}).path);
    const children = useSelector(state => state.childentries[item.id] || []);
    const childrenGroupedByYear = useSelector(state => state.childentriesByYear[item.id] || {});
    const previous = useSelector(state => state.previousentry[item.id]);
    const next = useSelector(state => state.nextentry[item.id]);
    const hash = useSelector(state => state.router.location.hash);
    const albumGroupByYear = useSelector(state => state.albumGroupByYear[item.id] === undefined ? true : state.albumGroupByYear[item.id]);
    const showEditControls = useSelector(state => state.showEditControls);
    const sortingStatus = useSelector(state => state.sortingStatus);
    const targetId = hash.substr(1);
    const dispatch = useDispatch();
    const title = pictureTitle(item);
    const anchor = 'entry' + item.id.toString();
    const swipeHandlers = useSwipeable({
        onSwipedLeft: () => next && dispatch(push(next.path)),
        onSwipedRight: () => previous && dispatch(push(previous.path)),
    });
    useEffect(() => {
        const elem = document.getElementById(targetId);
        if (elem) {
            elem.scrollIntoView();
        }
    }, [targetId]);

    return (
        <div>
            <Helmet>
                <title>{title}</title>
                <meta name="description" content={item.description}/>
            </Helmet>
            <nav className="navbar sticky-top navbar-light bg-light">
                { parent && (
                    <NavLink className="nav-link" to={parent + '#' + anchor}>
                        <div className="container">
                            <div className="column">
                                <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                                <div className="row">{text.up}</div>
                            </div>
                        </div>
                    </NavLink>
                ) }
                <h1>{title}</h1>
                <div className="d-flex flex-row">
                    <DownloadButton item={item} />
                    <Locale className="form-inline" />
                    <div className="dropdown">
                        <button className="dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                            <CopyLinkButton className="dropdown-item" />
                            <AlbumGroupByYearButton className="dropdown-item" album={item} />
                            <AlbumHideShowYearsButton key={'albumHideShowYears' + item.id.toString()} className="dropdown-item" album={item} />
                            <EditModeButton className="dropdown-item" />
                            <LoginLogoutButton className="dropdown-item" item={item}/>
                        </div>
                    </div>
                </div>
            </nav>
            <div className="btn-toolbar" role="toolbar">
                <Previous previous={previous} />
                <Next className="ml-auto" next={next} />
            </div>
            <div className="btn-group" role="group" aria-label="Modify album">
                <ModifyButton className="mx-1 my-1" item={item} />
                <AddAlbumButton className="mx-1 my-1" item={item} />
                <AddPictureButton className="mx-1 my-1" item={item} />
                <DeleteButton className="mx-1 my-1" item={item} />
                <SortByDateButton className="mx-1 my-1" item={item} />
                <BatchAddPictures className="" item={item} />
            </div>
            { showEditControls && sortingStatus && <div className="alert alert-primary" role="alert">{sortingStatus}</div> }
            { item.description && <div className="alert alert-primary" role="alert">{item.description}</div> }
            { renderChildren(children, childrenGroupedByYear, albumGroupByYear, swipeHandlers) }
        </div>
    );
}

function renderChildren(children, childrenGroupedByYear, albumGroupByYear, swipeHandlers) {
    if (albumGroupByYear) {
        return (
            <div className="column" {...swipeHandlers}>
                { Object.entries(childrenGroupedByYear).map(renderYear) }
            </div>
        );
    }

    return (
        <div className="row" {...swipeHandlers}>
            { children.slice().sort((a,b) => a.sort - b.sort).map(renderChild) }
        </div>
    );
}

function renderYear(entry) {
    const [ year, children ] = entry;
    const key = 'yearId' + year.toString();
    const collapseId = 'collapse' + year.toString();
    const collapseRef = '#' + collapseId;
    const expanded = true;
    return (
        <div id={year} className="column" key={key}>
            <div className="row">
                <a className="pl-5 btn" data-toggle="collapse" href={collapseRef} aria-expanded={expanded} aria-controls={collapseId}><h2>{year}</h2></a>
                <a className="album-yearlink" href={'#' + year.toString()}><h2>#</h2></a>
            </div>
            <div className="row collapse multi-collapse show pb-5" id={collapseId}>
                { children.slice().sort((a,b) => a.sort - b.sort).map(renderChild) }
            </div>
        </div>
    );
}

function renderChild(child, index) {
    if (child.album) {
        return <AlbumEntryOfTypeAlbum key={index} entry={child} />;
    }

    return <AlbumEntryOfTypePicture key={index} entry={child} />;
}
