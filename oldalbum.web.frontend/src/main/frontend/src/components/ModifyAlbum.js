import React from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { Redirect, NavLink } from 'react-router-dom';
import { stringify } from 'qs';
import {
    MODIFY_ALBUM_PARENT,
    MODIFY_ALBUM_BASENAME,
    MODIFY_ALBUM_TITLE,
    MODIFY_ALBUM_DESCRIPTION,
    MODIFY_ALBUM_UPDATE,
    MODIFY_ALBUM_CLEAR,
} from '../reduxactions';
import { webcontext } from '../constants';

function ModifyAlbum(props) {
    const {
        loginresult,
        modifyalbum,
        albums,
        onParentChange,
        onBasenameChange,
        onTitleChange,
        onDescriptionChange,
        onUpdate,
        onCancel,
    } = props;
    if (!loginresult.canModifyAlbum) {
        if (modifyalbum.path) {
            return <Redirect to={modifyalbum.path} />;
        }

        return <Redirect to={webcontext} />;
    }

    return(
        <div>
            <h1>Modify album</h1>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div>
                    <label htmlFor="parent">Parent</label>
                    <select id="parent" value={modifyalbum.parent} onChange={(event) => onParentChange(parseInt(event.target.value, 10), albums)}>
                        { albums.map((val) => <option key={'album_' + val.id} value={val.id}>{val.title}</option>) }
                    </select>
                </div>
                <div>
                    <label htmlFor="path">Path</label>
                    <input id="path" type="text" value={modifyalbum.path} readOnly={true} />
                </div>
                <div>
                    <label htmlFor="basename">Base file name</label>
                    <input id="basename" type="text" value={modifyalbum.basename} onChange={(event) => onBasenameChange(event.target.value, albums.find(a => a.id === modifyalbum.parent))}/>
                </div>
                <div>
                    <label htmlFor="title">Title</label>
                    <input id="title" type="text" value={modifyalbum.title} onChange={(event) => onTitleChange(event.target.value)}/>
                </div>
                <div>
                    <label htmlFor="description">Description</label>
                    <input id="description" type="text" value={modifyalbum.description} onChange={(event) => onDescriptionChange(event.target.value)}/>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onUpdate(modifyalbum.path)}>Update</button>
                </div>
                <div>
                    <button className="btn btn-default" type="button" onClick={() => onCancel(modifyalbum.path)}>Cancel</button>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const login = state.login || {};
    const loginresult = login.loginresult || { success: false };
    const modifyalbum = state.modifyalbum;
    const albums = state.allroutes.filter(r => r.album).filter(r => r.id !== modifyalbum.id) || [];
    return {
        loginresult,
        modifyalbum,
        albums,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onParentChange: (parent, albums) => dispatch(MODIFY_ALBUM_PARENT(albums.find(a => a.id === parent))),
        onBasenameChange: (basename, parentalbum) => dispatch(MODIFY_ALBUM_BASENAME({ basename, parentalbum })),
        onTitleChange: (title) => dispatch(MODIFY_ALBUM_TITLE(title)),
        onDescriptionChange: (description) => dispatch(MODIFY_ALBUM_DESCRIPTION(description)),
        onUpdate: (path) => { dispatch(MODIFY_ALBUM_UPDATE()); dispatch(push(path)); },
        onCancel: (path) => { dispatch(MODIFY_ALBUM_CLEAR()); dispatch(push(path)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(ModifyAlbum);
