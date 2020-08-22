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

function ModifyAlbum(props) {
    const {
        loginresult,
        modifyalbum,
        albums,
        uplocation,
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
                <h1>Modify album</h1>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="parent" className="col-form-label col-5">Parent</label>
                        <div className="col-7">
                            <select id="parent" className="form-control" value={modifyalbum.parent} onChange={(event) => onParentChange(parseInt(event.target.value, 10), albums)}>
                                { albums.map((val) => <option key={'album_' + val.id} value={val.id}>{val.title}</option>) }
                            </select>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="path" className="col-form-label col-5">Path</label>
                        <div className="col-7">
                            <input id="path" className="form-control" type="text" value={modifyalbum.path} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="basename" className="col-form-label col-5">Base file name</label>
                        <div className="col-7">
                            <input id="basename" className="form-control" type="text" value={modifyalbum.basename} onChange={(event) => onBasenameChange(event.target.value, albums.find(a => a.id === modifyalbum.parent))}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">Title</label>
                        <div className="col-7">
                            <input id="title" className="form-control" type="text" value={modifyalbum.title} onChange={(event) => onTitleChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">Description</label>
                        <div className="col-7">
                            <input id="description" className="form-control" type="text" value={modifyalbum.description} onChange={(event) => onDescriptionChange(event.target.value)}/>
                        </div>
                    </div>
                    <div className="container">
                        <button className="btn btn-primary ml-1" type="button" onClick={() => onUpdate(modifyalbum.path)}>Update</button>
                        <button className="btn btn-primary ml-1" type="button" onClick={() => onCancel(modifyalbum.path)}>Cancel</button>
                    </div>
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
    const albumentries = state.albumentries || {};
    const originalalbum = albumentries[modifyalbum.id] || {};
    const uplocation = originalalbum.path || '/';
    return {
        loginresult,
        modifyalbum,
        albums,
        uplocation,
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
