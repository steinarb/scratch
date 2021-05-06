import React from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { Redirect, NavLink } from 'react-router-dom';
import moment from 'moment';
import {
    MODIFY_PICTURE_PARENT,
    MODIFY_PICTURE_BASENAME,
    MODIFY_PICTURE_TITLE,
    MODIFY_PICTURE_DESCRIPTION,
    MODIFY_PICTURE_IMAGEURL,
    MODIFY_PICTURE_THUMBNAILURL,
    MODIFY_PICTURE_UPDATE,
    MODIFY_PICTURE_CLEAR,
    IMAGE_LOADED,
} from '../reduxactions';

function ModifyPicture(props) {
    const {
        loginresult,
        modifypicture,
        albums,
        uplocation,
        onParentChange,
        onBasenameChange,
        onTitleChange,
        onDescriptionChange,
        onImageUrlChange,
        onImageLoaded,
        onThumbnailUrlChange,
        onUpdate,
        onCancel,
    } = props;
    const imageUrl = modifypicture.imageUrl;
    const lastmodified = modifypicture.lastModified ? moment(modifypicture.lastModified).format("YYYY-MM-DD hh:mm:ss") : '';
    if (!loginresult.canModifyAlbum) {
        if (modifypicture.path) {
            return <Redirect to={modifypicture.path} />;
        }

        return <Redirect to="/" />;
    }

    return(
        <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <NavLink to={uplocation}>
                    <div className="container">
                        <div className="column">
                            <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                            <div className="row">Up</div>
                        </div>
                    </div>
                </NavLink>
                <h1>Modify picture</h1>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <img className="img-thumbnail fullsize-img-thumbnail" src={imageUrl} onLoad={() => onImageLoaded(imageUrl)} />
                    </div>
                    <div className="form-group row">
                        <label htmlFor="parent" className="col-form-label col-5">Parent</label>
                        <div className="col-7">
                            <select id="parent" className="form-control" value={modifypicture.parent} onChange={(event) => onParentChange(parseInt(event.target.value, 10), albums)}>
                                { albums.map((val) => <option key={'album_' + val.id} value={val.id}>{val.title}</option>) }
                            </select>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="path" className="col-form-label col-5">Path</label>
                        <div className="col-7">
                            <input id="path" type="text" value={modifypicture.path} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="basename" className="col-form-label col-5">Base file name</label>
                        <div className="col-7">
                            <input id="basename" className="form-control" type="text" value={modifypicture.basename} onChange={(event) => onBasenameChange(event.target.value, albums.find(a => a.id === modifypicture.parent))}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">Title</label>
                        <div className="col-7">
                            <input id="title" className="form-control" type="text" value={modifypicture.title} onChange={(event) => onTitleChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">Description</label>
                        <div className="col-7">
                            <input id="description" className="form-control" type="text" value={modifypicture.description} onChange={(event) => onDescriptionChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="imageUrl" className="col-form-label col-5">Image url</label>
                        <div className="col-7">
                            <input id="imageUrl" className="form-control" type="text" value={modifypicture.imageUrl} onChange={(event) => onImageUrlChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Thumbnail url</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" className="form-control" type="text" value={modifypicture.thumbnailUrl} onChange={(event) => onThumbnailUrlChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Content length (bytes)</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" readOnly className="form-control" type="text" value={modifypicture.contentLength}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Content type</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" readOnly className="form-control" type="text" value={modifypicture.contentType}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Last modified</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" readOnly className="form-control" type="text" value={lastmodified}/>
                        </div>
                    </div>
                    <div>
                        <button className="btn btn-primary ml-1" type="button" onClick={() => onUpdate(modifypicture.path)}>Update</button>
                        <button className="btn btn-primary ml-1" type="button" onClick={() => onCancel(modifypicture.path)}>Cancel</button>
                    </div>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    const modifypicture = state.modifypicture;
    const albums = state.allroutes.filter(r => r.album).filter(r => r.id !== modifypicture.id) || [];
    const albumentries = state.albumentries || {};
    const originalalbum = albumentries[modifypicture.id] || {};
    const uplocation = originalalbum.path || '/';
    return {
        loginresult,
        modifypicture,
        albums,
        uplocation,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onParentChange: (parent, albums) => dispatch(MODIFY_PICTURE_PARENT(albums.find(a => a.id === parent))),
        onBasenameChange: (basename, parentalbum) => dispatch(MODIFY_PICTURE_BASENAME({ basename, parentalbum })),
        onTitleChange: (title) => dispatch(MODIFY_PICTURE_TITLE(title)),
        onDescriptionChange: (description) => dispatch(MODIFY_PICTURE_DESCRIPTION(description)),
        onImageUrlChange: (imageUrl) => dispatch(MODIFY_PICTURE_IMAGEURL(imageUrl)),
        onImageLoaded: (imageUrl) => dispatch(IMAGE_LOADED(imageUrl)),
        onThumbnailUrlChange: (thumbnailUrl) => dispatch(MODIFY_PICTURE_THUMBNAILURL(thumbnailUrl)),
        onUpdate: (path) => { dispatch(MODIFY_PICTURE_UPDATE()); dispatch(push(path)); },
        onCancel: (path) => { dispatch(MODIFY_PICTURE_CLEAR()); dispatch(push(path)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(ModifyPicture);
