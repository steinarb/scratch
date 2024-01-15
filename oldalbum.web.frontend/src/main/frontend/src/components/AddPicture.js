import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { NavLink, useSearchParams } from 'react-router-dom';
import {
    ADD_PICTURE_BASENAME_FIELD_CHANGED,
    ADD_PICTURE_TITLE_FIELD_CHANGED,
    ADD_PICTURE_DESCRIPTION_FIELD_CHANGED,
    ADD_PICTURE_IMAGEURL_FIELD_CHANGED,
    ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED,
    ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED,
    ADD_PICTURE_REQUIRE_LOGIN_FIELD_CHANGED,
    ADD_PICTURE_UPDATE_BUTTON_CLICKED,
    ADD_PICTURE_CANCEL_BUTTON_CLICKED,
    IMAGE_METADATA_REQUEST,
} from '../reduxactions';

export default function AddPicture() {
    const text = useSelector(state => state.displayTexts);
    const path = useSelector(state => state.albumentryPath);
    const basename = useSelector(state => state.albumentryBasename);
    const title = useSelector(state => state.albumentryTitle);
    const description = useSelector(state => state.albumentryDescription);
    const imageUrl = useSelector(state => state.albumentryImageUrl);
    const thumbnailUrl = useSelector(state => state.albumentryThumbnailUrl);
    const lastModified = useSelector(state => state.albumentryLastModified);
    const contentLength = useSelector(state => state.albumentryContentLength);
    const contentType = useSelector(state => state.albumentryContentType);
    const requireLogin = useSelector(state => state.albumentryRequireLogin);
    const albums = useSelector(state => state.allroutes).filter(r => r.album);
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
                <h1>{text.addpictureto} &quot;{parentalbum.title}&quot;</h1>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row mb-2">
                        <img
                            className="img-thumbnail fullsize-img-thumbnail"
                            src={imageUrl}
                            onLoad={() => dispatch(IMAGE_METADATA_REQUEST(imageUrl))} />
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="path" className="col-form-label col-5">{text.path}</label>
                        <div className="col-7">
                            <input id="path" className="form-control" type="text" value={path} readOnly={true} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="basename" className="col-form-label col-5">{text.basefilename}</label>
                        <div className="col-7">
                            <input
                                id="basename"
                                className="form-control"
                                type="text"
                                value={basename}
                                onChange={e => dispatch(ADD_PICTURE_BASENAME_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="title" className="col-form-label col-5">{text.title}</label>
                        <div className="col-7">
                            <input
                                id="title"
                                className="form-control"
                                type="text"
                                value={title}
                                onChange={e => dispatch(ADD_PICTURE_TITLE_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="description" className="col-form-label col-5">{text.description}</label>
                        <div className="col-7">
                            <input
                                id="description"
                                className="form-control"
                                type="text"
                                value={description}
                                onChange={e => dispatch(ADD_PICTURE_DESCRIPTION_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="imageUrl" className="col-form-label col-5">{text.imageurl}</label>
                        <div className="col-7">
                            <input
                                id="imageUrl"
                                className="form-control"
                                type="text"
                                value={imageUrl}
                                onChange={e => dispatch(ADD_PICTURE_IMAGEURL_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">{text.thumbnailurl}</label>
                        <div className="col-7">
                            <input
                                id="thumbnailUrl"
                                className="form-control"
                                type="text"
                                value={thumbnailUrl}
                                onChange={e => dispatch(ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="contentLength" className="col-form-label col-5">{text.contentlengthinbytes}</label>
                        <div className="col-7">
                            <input id="contentLength" readOnly className="form-control" type="text" value={contentLength}/>
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="contentType" className="col-form-label col-5">{text.contenttype}</label>
                        <div className="col-7">
                            <input id="contentType" readOnly className="form-control" type="text" value={contentType}/>
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <label htmlFor="lastmodified" className="col-form-label col-5">{text.lastmodified}</label>
                        <div className="col-7">
                            <input
                                id="lastmodified"
                                className="form-control"
                                type="date"
                                value={lastmodified}
                                onChange={e => dispatch(ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row mb-2">
                        <input
                            id="require-login"
                            className="form-check col-1"
                            type="checkbox"
                            checked={requireLogin}
                            onChange={e => dispatch(ADD_PICTURE_REQUIRE_LOGIN_FIELD_CHANGED(e.target.checked))} />
                        <label htmlFor="require-login" className="form-check-label col-11">{text.requireloggedinuser}</label>
                    </div>
                    <div>
                        <button
                            className="btn btn-light me-1"
                            type="button"
                            onClick={() => dispatch(ADD_PICTURE_UPDATE_BUTTON_CLICKED())}>
                            {text.add}</button>
                        <button
                            className="btn btn-light me-1"
                            type="button"
                            onClick={() => dispatch(ADD_PICTURE_CANCEL_BUTTON_CLICKED())}>
                            {text.cancel}</button>
                    </div>
                </div>
            </form>
        </div>
    );
}
