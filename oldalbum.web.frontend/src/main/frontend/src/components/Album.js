import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { push } from 'connected-react-router';
import { Helmet } from "react-helmet";
import { useSwipeable } from 'react-swipeable';
import { pictureTitle } from './commonComponentCode';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';
import ModifyButton from './ModifyButton';
import AddAlbumButton from './AddAlbumButton';
import AddPictureButton from './AddPictureButton';
import DeleteButton from './DeleteButton';
import Previous from './Previous';
import Next from './Next';
import AlbumEntryOfTypeAlbum from './AlbumEntryOfTypeAlbum';
import AlbumEntryOfTypePicture from './AlbumEntryOfTypePicture';

function Album(props) {
    const { item, parent, children, previous, next, navigateTo } = props;
    const title = pictureTitle(item);
    const swipeHandlers = useSwipeable({
        onSwipedLeft: () => navigateTo(next),
        onSwipedRight: () => navigateTo(previous),
    });

    return (
        <div>
            <Helmet>
                <title>{title}</title>
                <meta name="description" content={item.description}/>
            </Helmet>
            <nav className="navbar navbar-light bg-light">
                { parent && (
                    <NavLink to={parent}>
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
        return <AlbumEntryOfTypeAlbum className="col-2-sm mx-1 my-1" key={index} entry={child} />;
    }

    return <AlbumEntryOfTypePicture className="col-2-sm mx-1 my-1" key={index} entry={child} />;
}

function mapStateToProps(state, ownProps) {
    const { item } = ownProps;
    const parentEntry = state.albumentries[item.parent] || {};
    const parent = parentEntry.path;
    const children = state.childentries[item.id] || [];
    const previous = state.previousentry[item.id];
    const next = state.nextentry[item.id];
    return {
        parent,
        children,
        previous,
        next,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        navigateTo: (item) => item && dispatch(push(item.path)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Album);
