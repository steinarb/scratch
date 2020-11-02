import React from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { Redirect, NavLink } from 'react-router-dom';
import { parse } from 'qs';
import {
    ADD_PICTURE_BASENAME,
    ADD_PICTURE_TITLE,
    ADD_PICTURE_DESCRIPTION,
    ADD_PICTURE_IMAGEURL,
    ADD_PICTURE_THUMBNAILURL,
    ADD_PICTURE_UPDATE,
    ADD_PICTURE_CLEAR,
} from '../reduxactions';

function AddPicture(props) {
    const {
        loginresult,
        addpicture,
        albums,
        onParentChange,
        onBasenameChange,
        onTitleChange,
        onDescriptionChange,
        onImageUrlChange,
        onThumbnailUrlChange,
        onUpdate,
        onCancel,
    } = props;
    const queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parent } = queryParams;
    const parentId = parseInt(parent, 10);
    const parentalbum = albums.find(a => a.id === parentId);
    const uplocation = parentalbum.path || '/';
    if (!loginresult.canModifyAlbum) {
        return <Redirect to={uplocation} />;
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
                <h1>Add picture to "{parentalbum.title}"</h1>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <img className="img-thumbnail fullsize-img-thumbnail" src={addpicture.imageUrl} />
                    </div>
                    <div className="form-group row">
                        <label htmlFor="path" className="col-form-label col-5">Path</label>
                        <div className="col-7">
                            <input id="path" className="form-control" type="text" value={addpicture.path} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="basename" className="col-form-label col-5">Base file name</label>
                        <div className="col-7">
                            <input id="basename" className="form-control" type="text" value={addpicture.basename} onChange={(event) => onBasenameChange(event.target.value, parentalbum)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">Title</label>
                        <div className="col-7">
                            <input id="title" className="form-control" type="text" value={addpicture.title} onChange={(event) => onTitleChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">Description</label>
                        <div className="col-7">
                            <input id="description" className="form-control" type="text" value={addpicture.description} onChange={(event) => onDescriptionChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="imageUrl" className="col-form-label col-5">Image URL</label>
                        <div className="col-7">
                            <input id="imageUrl" className="form-control" type="text" value={addpicture.imageUrl} onChange={(event) => onImageUrlChange(event.target.value, parentalbum)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Thumbnail URL</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" className="form-control" type="text" value={addpicture.thumbnailUrl} onChange={(event) => onThumbnailUrlChange(event.target.value, parentalbum)}/>
                        </div>
                    </div>
                    <div>
                        <button className="btn btn-primary ml-1" type="button" onClick={() => onUpdate(addpicture.path)}>Add</button>
                        <button className="btn btn-primary ml-1" type="button" onClick={() => onCancel(addpicture.path)}>Cancel</button>
                    </div>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    const addpicture = state.addpicture;
    const albums = state.allroutes.filter(r => r.album) || [];
    return {
        loginresult,
        addpicture,
        albums,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onBasenameChange: (basename, parentalbum) => dispatch(ADD_PICTURE_BASENAME({ basename, parentalbum })),
        onTitleChange: (title) => dispatch(ADD_PICTURE_TITLE(title)),
        onDescriptionChange: (description) => dispatch(ADD_PICTURE_DESCRIPTION(description)),
        onImageUrlChange: (imageUrl, parentalbum) => dispatch(ADD_PICTURE_IMAGEURL({ imageUrl, parentalbum })),
        onThumbnailUrlChange: (thumbnailUrl, parentalbum) => dispatch(ADD_PICTURE_THUMBNAILURL({ thumbnailUrl, parentalbum })),
        onUpdate: (path) => { dispatch(ADD_PICTURE_UPDATE()); dispatch(push(path)); },
        onCancel: (path) => { dispatch(ADD_PICTURE_CLEAR()); dispatch(push(path)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddPicture);
