import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { parse } from 'qs';
import {
    MODIFY_ALBUM_BASENAME_FIELD_CHANGED,
    MODIFY_ALBUM_TITLE_FIELD_CHANGED,
    MODIFY_ALBUM_DESCRIPTION_FIELD_CHANGED,
    ADD_ALBUM_UPDATE_BUTTON_CLICKED,
    ADD_ALBUM_CANCEL_BUTTON_CLICKED,
} from '../reduxactions';

function AddAlbum(props) {
    const {
        path,
        basename,
        title,
        description,
        albums,
    } = props;
    const dispatch = useDispatch();
    const queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parent } = queryParams;
    const parentId = parseInt(parent, 10);
    const parentalbum = albums.find(a => a.id === parentId);
    const uplocation = parentalbum.path || '/';

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
                            <input id="path" className="form-control" type="text" value={path} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="basename" className="col-form-label col-5">Base file name</label>
                        <div className="col-7">
                            <input
                                id="basename"
                                className="form-control"
                                type="text"
                                value={basename}
                                onChange={e => dispatch(MODIFY_ALBUM_BASENAME_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">Title</label>
                        <div className="col-7">
                            <input
                                id="title"
                                className="form-control"
                                type="text"
                                value={title}
                                onChange={e => dispatch(MODIFY_ALBUM_TITLE_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">Description</label>
                        <div className="col-7">
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={description}
                                onChange={e => dispatch(MODIFY_ALBUM_DESCRIPTION_FIELD_CHANGED(e.target.value))}/>
                        </div>
                    </div>
                    <div>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => dispatch(ADD_ALBUM_UPDATE_BUTTON_CLICKED())}>
                            Add</button>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => dispatch(ADD_ALBUM_CANCEL_BUTTON_CLICKED())}>
                            Cancel</button>
                    </div>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const path = state.albumentryPath;
    const basename = state.albumentryBasename;
    const title = state.albumentryTitle;
    const description = state.albumentryDescription;
    const albums = state.allroutes.filter(r => r.album) || [];
    return {
        path,
        basename,
        title,
        description,
        albums,
    };
}

export default connect(mapStateToProps)(AddAlbum);
