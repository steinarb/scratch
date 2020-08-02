import React from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { Redirect, NavLink } from 'react-router-dom';
import { stringify } from 'qs';
import {
    MODIFY_PICTURE_PARENT,
    MODIFY_PICTURE_BASENAME,
    MODIFY_PICTURE_TITLE,
    MODIFY_PICTURE_DESCRIPTION,
    MODIFY_PICTURE_IMAGEURL,
    MODIFY_PICTURE_THUMBNAILURL,
    MODIFY_PICTURE_UPDATE,
    MODIFY_PICTURE_CLEAR,
} from '../reduxactions';
import { webcontext } from '../constants';

function ModifyPicture(props) {
    const {
        loginresult,
        modifypicture,
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
    if (!loginresult.canModifyAlbum) {
        if (modifypicture.path) {
            return <Redirect to={modifypicture.path} />;
        }

        return <Redirect to={webcontext} />;
    }

    return(
        <div>
            <h1>Modify picture</h1>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <label htmlFor="parent">Parent</label>
                    <select id="parent" value={modifypicture.parent} onChange={(event) => onParentChange(parseInt(event.target.value, 10), albums)}>
                        { albums.map((val) => <option key={'album_' + val.id} value={val.id}>{val.title}</option>) }
                    </select>
                </div>
                <div>
                    <label htmlFor="path">Path</label>
                    <input id="path" type="text" value={modifypicture.path} readOnly={true} />
                </div>
                <div>
                    <label htmlFor="basename">Base file name</label>
                    <input id="basename" type="text" value={modifypicture.basename} onChange={(event) => onBasenameChange(event.target.value, albums.find(a => a.id === modifypicture.parent))}/>
                </div>
                <div>
                    <label htmlFor="title">Title</label>
                    <input id="title" type="text" value={modifypicture.title} onChange={(event) => onTitleChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="description">Description</label>
                    <input id="description" type="text" value={modifypicture.description} onChange={(event) => onDescriptionChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="imageUrl">Image url</label>
                    <input id="imageUrl" type="text" value={modifypicture.imageUrl} onChange={(event) => onImageUrlChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="thumbnailUrl">Thumbnail url</label>
                    <input id="thumbnailUrl" type="text" value={modifypicture.thumbnailUrl} onChange={(event) => onThumbnailUrlChange(event.target.value)}/>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onUpdate(modifypicture.path)}>Update</button>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onCancel(modifypicture.path)}>Cancel</button>
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
    return {
        loginresult,
        modifypicture,
        albums,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onParentChange: (parent, albums) => dispatch(MODIFY_PICTURE_PARENT(albums.find(a => a.id === parent))),
        onBasenameChange: (basename, parentalbum) => dispatch(MODIFY_PICTURE_BASENAME({ basename, parentalbum })),
        onTitleChange: (title) => dispatch(MODIFY_PICTURE_TITLE(title)),
        onDescriptionChange: (description) => dispatch(MODIFY_PICTURE_DESCRIPTION(description)),
        onImageUrlChange: (imageUrl) => dispatch(MODIFY_PICTURE_IMAGEURL(imageUrl)),
        onThumbnailUrlChange: (thumbnailUrl) => dispatch(MODIFY_PICTURE_THUMBNAILURL(thumbnailUrl)),
        onUpdate: (path) => { dispatch(MODIFY_PICTURE_UPDATE()); dispatch(push(path)); },
        onCancel: (path) => { dispatch(MODIFY_PICTURE_CLEAR()); dispatch(push(path)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(ModifyPicture);
