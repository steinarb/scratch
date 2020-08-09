import React from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { Redirect, NavLink } from 'react-router-dom';
import { parse } from 'qs';
import {
    ADD_ALBUM_BASENAME,
    ADD_ALBUM_TITLE,
    ADD_ALBUM_DESCRIPTION,
    ADD_ALBUM_UPDATE,
    ADD_ALBUM_CLEAR,
} from '../reduxactions';
import { webcontext } from '../constants';

function AddAlbum(props) {
    const {
        loginresult,
        addalbum,
        albums,
        onParentChange,
        onBasenameChange,
        onTitleChange,
        onDescriptionChange,
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
            <h1>Add album to "{parentalbum.title}"</h1>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <label htmlFor="path">Path</label>
                    <input id="path" type="text" value={addalbum.path} readOnly={true} />
                </div>
                <div>
                    <label htmlFor="basename">Base file name</label>
                    <input id="basename" type="text" value={addalbum.basename} onChange={(event) => onBasenameChange(event.target.value, parentalbum)}/>
                </div>
                <div>
                    <label htmlFor="title">Title</label>
                    <input id="title" type="text" value={addalbum.title} onChange={(event) => onTitleChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="description">Description</label>
                    <input id="description" type="text" value={addalbum.description} onChange={(event) => onDescriptionChange(event.target.value)}/>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onUpdate(addalbum.path)}>Add</button>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onCancel(addalbum.path)}>Cancel</button>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    const addalbum = state.addalbum;
    const albums = state.allroutes.filter(r => r.album) || [];
    return {
        loginresult,
        addalbum,
        albums,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onBasenameChange: (basename, parentalbum) => dispatch(ADD_ALBUM_BASENAME({ basename, parentalbum })),
        onTitleChange: (title) => dispatch(ADD_ALBUM_TITLE(title)),
        onDescriptionChange: (description) => dispatch(ADD_ALBUM_DESCRIPTION(description)),
        onUpdate: (path) => { dispatch(ADD_ALBUM_UPDATE()); dispatch(push(path)); },
        onCancel: (path) => { dispatch(ADD_ALBUM_CLEAR()); dispatch(push(path)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddAlbum);
