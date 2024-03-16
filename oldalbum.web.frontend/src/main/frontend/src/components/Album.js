import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { push } from 'redux-first-history';
import { Helmet } from "react-helmet";
import { useSwipeable } from 'react-swipeable';
import { pictureTitle } from './commonComponentCode';
import Locale from './Locale';
import AlbumSelectAllButton from './AlbumSelectAllButton';
import AlbumGroupByYearButton from './AlbumGroupByYearButton';
import AlbumHideShowYearsButton from './AlbumHideShowYearsButton';
import EditModeButton from './EditModeButton';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';
import ReloadShiroConfigButton from './ReloadShiroConfigButton';
import DownloadButton from './DownloadButton';
import ModifyButton from './ModifyButton';
import AddAlbumButton from './AddAlbumButton';
import AddPictureButton from './AddPictureButton';
import DeleteButton from './DeleteButton';
import SortByDateButton from './SortByDateButton';
import DeleteSelectionButton from './DeleteSelectionButton';
import BatchAddPictures from './BatchAddPictures';
import Previous from './Previous';
import Next from './Next';
import AlbumEntryOfTypeAlbum from './AlbumEntryOfTypeAlbum';
import AlbumEntryOfTypePicture from './AlbumEntryOfTypePicture';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';

export default function Album(props) {
    const { item } = props;
    const text = useSelector(state => state.displayTexts);
    const parent = useSelector(state => (state.albumentries[item.parent] || {}).path);
    const children = useSelector(state => state.childentries[item.id]);
    const childrenGroupedByYear = useSelector(state => state.childentriesByYear[item.id]);
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
            <div className="sticky-top">
                <nav className="navbar navbar-light bg-light">
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
                            <button className="dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown" aria-expanded="false">
                                <span className="navbar-toggler-icon"></span>
                            </button>
                            <ul className="dropdown-menu dropdown-menu-end">
                                <li><CopyLinkButton className="dropdown-item" /></li>
                                <li><ReloadShiroConfigButton className="dropdown-item"/></li>
                                <li><AlbumSelectAllButton className="dropdown-item" album={item} /></li>
                                <li><AlbumGroupByYearButton className="dropdown-item" album={item} /></li>
                                <li><AlbumHideShowYearsButton key={'albumHideShowYears' + item.id.toString()} className="dropdown-item" album={item} /></li>
                                <li><EditModeButton className="dropdown-item" /></li>
                                <li><LoginLogoutButton className="dropdown-item" item={item}/></li>
                            </ul>
                        </div>
                    </div>
                </nav>
                <div className="btn-group" role="group" aria-label="Modify album">
                    <ModifyButton className="mx-1 my-1" item={item} />
                    <AddAlbumButton className="mx-1 my-1" item={item} />
                    <AddPictureButton className="mx-1 my-1" item={item} />
                    <DeleteButton className="mx-1 my-1" item={item} />
                    <SortByDateButton className="mx-1 my-1" item={item} />
                    <DeleteSelectionButton className="mx-1 my-1" />
                    <BatchAddPictures className="" item={item} />
                </div>
                <div className="d-flex flex-fill" role="toolbar">
                    <Previous className="align-self-start" previous={previous} />
                    <div className="col"/>
                    <Next className="align-self-end" next={next} />
                </div>
                <ModifyFailedErrorAlert/>
                { showEditControls && sortingStatus && <div className="alert alert-primary" role="alert">{sortingStatus}</div> }
                { item.description && <div className="alert alert-primary" role="alert">{item.description}</div> }
            </div>
            { renderChildren(children || [], childrenGroupedByYear || [], albumGroupByYear, swipeHandlers) }
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
        <div id={year} className="column album-scroll-below-fixed-header" key={key}>
            <div className="d-flex ps-5">
                <a className="btn" data-bs-toggle="collapse" href={collapseRef} aria-expanded={expanded} aria-controls={collapseId}><h2>{year}</h2></a>
                <a className="album-yearlink col-1" href={'#' + year.toString()}><h2>#</h2></a>
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
