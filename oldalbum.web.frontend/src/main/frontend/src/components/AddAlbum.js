import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { NavLink, useSearchParams } from 'react-router-dom';
import {
    ADD_ALBUM_BASENAME_FIELD_CHANGED,
    ADD_ALBUM_TITLE_FIELD_CHANGED,
    ADD_ALBUM_DESCRIPTION_FIELD_CHANGED,
    ADD_ALBUM_LASTMODIFIED_FIELD_CHANGED,
    ADD_ALBUM_SET_LASTMODIFIED_FIELD_TO_CURRENT_DATE,
    ADD_ALBUM_CLEAR_LASTMODIFIED_FIELD,
    ADD_ALBUM_REQUIRE_LOGIN_FIELD_CHANGED,
    ADD_ALBUM_UPDATE_BUTTON_CLICKED,
    ADD_ALBUM_CANCEL_BUTTON_CLICKED,
} from '../reduxactions';

export default function AddAlbum() {
    const text = useSelector(state => state.displayTexts);
    const path = useSelector(state => state.albumentryPath);
    const basename = useSelector(state => state.albumentryBasename);
    const title = useSelector(state => state.albumentryTitle);
    const description = useSelector(state => state.albumentryDescription);
    const lastModified = useSelector(state => state.albumentryLastModified);
    const requireLogin = useSelector(state => state.albumentryRequireLogin);
    const albums = useSelector(state => state.allroutes.filter(r => r.album) || []);
    const dispatch = useDispatch();
    const [ queryParams ] = useSearchParams();
    const parent = queryParams.get('parent');
    const parentId = parseInt(parent, 10);
    const parentalbum = albums.find(a => a.id === parentId);
    const uplocation = parentalbum.path || '/';
    const lastmodified = lastModified ? lastModified.split('T')[0] : '';

    return(
        <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <NavLink to={uplocation}>
                    <div className="container">
                        <div className="column">
                            <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                            <div className="row">{text.up}</div>
                        </div>
                    </div>
                </NavLink>
                <h1>{text.addalbumto} &quot;{parentalbum.title}&quot;</h1>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <label htmlFor="path" className="col-form-label col-5">{text.path}</label>
                        <div className="col-7">
                            <input id="path" className="form-control" type="text" value={path} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="basename" className="col-form-label col-5">{text.basefilename}</label>
                        <div className="col-7">
                            <input
                                id="basename"
                                className="form-control"
                                type="text"
                                value={basename}
                                onChange={e => dispatch(ADD_ALBUM_BASENAME_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input
                                id="title"
                                className="form-control"
                                type="text"
                                value={title}
                                onChange={e => dispatch(ADD_ALBUM_TITLE_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={description}
                                onChange={e => dispatch(ADD_ALBUM_DESCRIPTION_FIELD_CHANGED(e.target.value))}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="lastmodified" className="col-form-label col-5">{text.lastmodified}</label>
                        <div className="col-7">
                            <input
                                id="lastmodified"
                                className="form-control"
                                type="date"
                                value={lastmodified}
                                onChange={e => dispatch(ADD_ALBUM_LASTMODIFIED_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <div className="col-5" />
                        <div className="col-7">
                            <button
                                className="btn btn-light ml-1"
                                type="button"
                                onClick={() => dispatch(ADD_ALBUM_SET_LASTMODIFIED_FIELD_TO_CURRENT_DATE())}
                            >
                                {text.setTodaysDate}
                            </button>
                            <button
                                className="btn btn-light ml-1"
                                type="button"
                                onClick={() => dispatch(ADD_ALBUM_CLEAR_LASTMODIFIED_FIELD())}
                            >
                                {text.clearDate}
                            </button>
                        </div>
                    </div>
                    <div className="form-group row">
                        <input
                            id="require-login"
                            className="form-check col-1"
                            type="checkbox"
                            checked={requireLogin}
                            onChange={e => dispatch(ADD_ALBUM_REQUIRE_LOGIN_FIELD_CHANGED(e.target.checked))} />
                        <label htmlFor="require-login" className="form-check-label col-11">{text.requireloggedinuser}</label>
                    </div>
                    <div>
                        <button
                            className="btn btn-light ml-1"
                            type="button"
                            onClick={() => dispatch(ADD_ALBUM_UPDATE_BUTTON_CLICKED())}>
                            {text.add}</button>
                        <button
                            className="btn btn-light ml-1"
                            type="button"
                            onClick={() => dispatch(ADD_ALBUM_CANCEL_BUTTON_CLICKED())}>
                            {text.cancel}</button>
                    </div>
                </div>
            </form>
        </div>
    );
}
