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
import { webcontext } from '../constants';

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
    if (!loginresult.canModifyAlbum) {
        if (parentalbum.path) {
            return <Redirect to={parentalbum.path} />;
        }

        return <Redirect to={webcontext} />;
    }

    return(
        <div>
            <h1>Add picture to "{parentalbum.title}"</h1>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <label htmlFor="path">Path</label>
                    <input id="path" type="text" value={addpicture.path} readOnly={true} />
                </div>
                <div>
                    <label htmlFor="basename">Base file name</label>
                    <input id="basename" type="text" value={addpicture.basename} onChange={(event) => onBasenameChange(event.target.value, parentalbum)}/>
                </div>
                <div>
                    <label htmlFor="title">Title</label>
                    <input id="title" type="text" value={addpicture.title} onChange={(event) => onTitleChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="description">Description</label>
                    <input id="description" type="text" value={addpicture.description} onChange={(event) => onDescriptionChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="imageUrl">Image URL</label>
                    <input id="imageUrl" type="text" value={addpicture.imageUrl} onChange={(event) => onImageUrlChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="thumbnailUrl">Thumbnail URL</label>
                    <input id="thumbnailUrl" type="text" value={addpicture.thumbnailUrl} onChange={(event) => onThumbnailUrlChange(event.target.value)}/>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onUpdate(addpicture.path)}>Add</button>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onCancel(addpicture.path)}>Cancel</button>
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
        onImageUrlChange: (imageUrl) => dispatch(ADD_PICTURE_IMAGEURL(imageUrl)),
        onThumbnailUrlChange: (thumbnailUrl) => dispatch(ADD_PICTURE_THUMBNAILURL(thumbnailUrl)),
        onUpdate: (path) => { dispatch(ADD_PICTURE_UPDATE()); dispatch(push(path)); },
        onCancel: (path) => { dispatch(ADD_PICTURE_CLEAR()); dispatch(push(path)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddPicture);
