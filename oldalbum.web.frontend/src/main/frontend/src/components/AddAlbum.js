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

function AddAlbum(props) {
    const {
        loginresult,
        addalbum,
        albums,
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
                <h1>Add album to &quot;{parentalbum.title}&quot;</h1>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="path" className="col-form-label col-5">Path</label>
                        <div className="col-7">
                            <input id="path" className="form-control" type="text" value={addalbum.path} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="basename" className="col-form-label col-5">Base file name</label>
                        <div className="col-7">
                            <input
                                id="basename"
                                className="form-control"
                                type="text"
                                value={addalbum.basename}
                                onChange={(event) => onBasenameChange(event.target.value, parentalbum)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">Title</label>
                        <div className="col-7">
                            <input
                                id="title"
                                className="form-control"
                                type="text"
                                value={addalbum.title}
                                onChange={(event) => onTitleChange(event.target.value)} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">Description</label>
                        <div className="col-7">
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={addalbum.description}
                                onChange={(event) => onDescriptionChange(event.target.value)}/>
                        </div>
                    </div>
                    <div>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => onUpdate(addalbum.path)}>
                            Add</button>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => onCancel(uplocation)}>
                            Cancel</button>
                    </div>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const loginresult = state.loginresult;
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
