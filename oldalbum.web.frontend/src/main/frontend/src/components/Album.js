import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { push } from 'redux-first-history';
import { Helmet } from "react-helmet";
import { useSwipeable } from 'react-swipeable';
import { pictureTitle } from './commonComponentCode';
import EditModeButton from './EditModeButton';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';
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
    const parent = useSelector(state => (state.albumentries[item.parent] || {}).path);
    const children = useSelector(state => state.childentries[item.id] || []);
    const previous = useSelector(state => state.previousentry[item.id]);
    const next = useSelector(state => state.nextentry[item.id]);
    const hash = useSelector(state => state.router.location.hash);
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
            <nav className="navbar navbar-light bg-light">
                { parent && (
                    <NavLink to={parent + '#' + anchor}>
                        <div className="container">
                            <div className="column">
                                <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                                <div className="row">Up</div>
                            </div>
                        </div>
                    </NavLink>
                ) }
                <h1>{title}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
                        <CopyLinkButton className="nav-item" />
                        <EditModeButton className="nav-item" />
                        <LoginLogoutButton className="nav-item" item={item}/>
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
            {item.description && <div className="alert alert-primary" role="alert">{item.description}</div> }
            <div className="row" {...swipeHandlers}>
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
