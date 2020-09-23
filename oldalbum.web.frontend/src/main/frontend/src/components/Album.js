import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { Helmet } from "react-helmet";
import { pictureTitle } from './commonComponentCode';
import LoginLogoutButton from './LoginLogoutButton';
import ModifyButton from './ModifyButton';
import AddAlbumButton from './AddAlbumButton';
import AddPictureButton from './AddPictureButton';
import DeleteButton from './DeleteButton';
import Previous from './Previous';
import Next from './Next';
import AlbumEntryOfTypeAlbum from './AlbumEntryOfTypeAlbum';
import AlbumEntryOfTypePicture from './AlbumEntryOfTypePicture';

function Album(props) {
    const { item, parent, children, previous, next, canLogin } = props;
    const title = pictureTitle(item);

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
                {canLogin && (
                    <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                ) }
                { !canLogin && <div>&nbsp;</div> }
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
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
            <div className="row">
                { children.sort((a,b) => a.sort - b.sort).map(renderChild) }
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
    const login = state.login || {};
    const loginresult = login.loginresult || {};
    const canLogin = loginresult.canLogin;
    return {
        parent,
        children,
        previous,
        next,
        canLogin,
    };
}

export default connect(mapStateToProps)(Album);
