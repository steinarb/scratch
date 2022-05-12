import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import {
    MODIFY_ALBUM_PARENT_SELECTED,
    MODIFY_ALBUM_BASENAME_FIELD_CHANGED,
    MODIFY_ALBUM_TITLE_FIELD_CHANGED,
    MODIFY_ALBUM_DESCRIPTION_FIELD_CHANGED,
    MODIFY_ALBUM_UPDATE_BUTTON_CLICKED,
    MODIFY_ALBUM_CANCEL_BUTTON_CLICKED,
} from '../reduxactions';

function ModifyAlbum(props) {
    const {
        parent,
        path,
        basename,
        title,
        description,
        albums,
        uplocation,
    } = props;
    const dispatch = useDispatch();

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
                            <select
                                id="parent"
                                className="form-control"
                                value={parent}
                                onChange={e => dispatch(MODIFY_ALBUM_PARENT_SELECTED(JSON.parse(e.target.value)))}>
                                { albums.map((val) => <option key={'album_' + val.id} value={JSON.stringify(val)}>{val.title}</option>) }
                            </select>
                        </div>
                    </div>
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
                                disabled={path === '/'}
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
                                onChange={e => dispatch(MODIFY_ALBUM_DESCRIPTION_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="container">
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => dispatch(MODIFY_ALBUM_UPDATE_BUTTON_CLICKED())}>
                            Update</button>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => dispatch(MODIFY_ALBUM_CANCEL_BUTTON_CLICKED())}>
                            Cancel</button>
                    </div>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const albumentryid = state.albumentryid;
    const parent = state.albumentryParent;
    const path = state.albumentryPath;
    const basename = state.albumentryBasename;
    const title = state.albumentryTitle;
    const description = state.albumentryDescription;
    const albums = state.allroutes.filter(r => r.album).filter(r => r.id !== albumentryid) || [];
    const albumentries = state.albumentries || {};
    const originalalbum = albumentries[albumentryid] || {};
    const uplocation = originalalbum.path || '/';
    return {
        parent,
        path,
        basename,
        title,
        description,
        albums,
        uplocation,
    };
}

export default connect(mapStateToProps)(ModifyAlbum);
