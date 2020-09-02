import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
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
    const { item, parent, children, previous, next } = props;

    return (
        <div>
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
                <h1>{item.title}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
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
            { children.sort((a,b) => a.sort - b.sort).map(renderChild) }
        </div>
    );
}

function renderChild(child, index) {
    if (child.album) {
        return <AlbumEntryOfTypeAlbum key={index} entry={child} />;
    }

    return <AlbumEntryOfTypePicture key={index} entry={child} />;
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

export default connect(mapStateToProps)(Album);
