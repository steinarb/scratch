import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import {
    MODIFY_PICTURE_PARENT_SELECTED,
    MODIFY_PICTURE_BASENAME_FIELD_CHANGED,
    MODIFY_PICTURE_TITLE_FIELD_CHANGED,
    MODIFY_PICTURE_DESCRIPTION_FIELD_CHANGED,
    MODIFY_PICTURE_IMAGEURL_FIELD_CHANGED,
    MODIFY_PICTURE_THUMBNAILURL_FIELD_CHANGED,
    MODIFY_PICTURE_UPDATE_BUTTON_CLICKED,
    MODIFY_PICTURE_CANCEL_BUTTON_CLICKED,
    IMAGE_METADATA_REQUEST,
} from '../reduxactions';

export default function ModifyPicture() {
    const parent = useSelector(state => state.albumentryParent);
    const path = useSelector(state => state.albumentryPath);
    const basename = useSelector(state => state.albumentryBasename);
    const title = useSelector(state => state.albumentryTitle);
    const description = useSelector(state => state.albumentryDescription);
    const imageUrl = useSelector(state => state.albumentryImageUrl);
    const thumbnailUrl = useSelector(state => state.albumentryThumbnailUrl);
    const lastModified = useSelector(state => state.albumentryLastModified);
    const contentLength = useSelector(state => state.albumentryContentLength);
    const contentType = useSelector(state => state.albumentryContentType);
    const albums = useSelector(state => state.allroutes.filter(r => r.album).filter(r => r.id !== state.albumentryid) || []);
    const uplocation = useSelector(state => (state.albumentries[state.albumentryid] || {}).path || '/');
    const dispatch = useDispatch();
    const lastmodified = lastModified ? new Date(lastModified).toISOString() : '';

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
                <h1>Modify picture</h1>
            </nav>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <div className="container">
                    <div className="form-group row">
                        <img
                            className="img-thumbnail fullsize-img-thumbnail"
                            src={imageUrl}
                            onLoad={() => dispatch(IMAGE_METADATA_REQUEST(imageUrl))} />
                    </div>
                    <div className="form-group row">
                        <label htmlFor="parent" className="col-form-label col-5">Parent</label>
                        <div className="col-7">
                            <select
                                id="parent"
                                className="form-control"
                                value={parent}
                                onChange={e => dispatch(MODIFY_PICTURE_PARENT_SELECTED(albums.find(a => a.id === parseInt(e.target.value))))}>
                                { albums.map((val) => <option key={'album_' + val.id} value={val.id}>{val.title}</option>) }
                            </select>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="path" className="col-form-label col-5">Path</label>
                        <div className="col-7">
                            <input id="path" type="text" value={path} readOnly={true} />
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
                                onChange={e => dispatch(MODIFY_PICTURE_BASENAME_FIELD_CHANGED(e.target.value))} />
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
                                onChange={e => dispatch(MODIFY_PICTURE_TITLE_FIELD_CHANGED(e.target.value))} />
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
                                onChange={e => dispatch(MODIFY_PICTURE_DESCRIPTION_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="imageUrl" className="col-form-label col-5">Image url</label>
                        <div className="col-7">
                            <input
                                id="imageUrl"
                                className="form-control"
                                type="text"
                                value={imageUrl}
                                onChange={e => dispatch(MODIFY_PICTURE_IMAGEURL_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Thumbnail url</label>
                        <div className="col-7">
                            <input
                                id="thumbnailUrl"
                                className="form-control"
                                type="text"
                                value={thumbnailUrl}
                                onChange={e => dispatch(MODIFY_PICTURE_THUMBNAILURL_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Content length (bytes)</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" readOnly className="form-control" type="text" value={contentLength}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Content type</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" readOnly className="form-control" type="text" value={contentType}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Last modified</label>
                        <div className="col-7">
                            <input id="thumbnailUrl" readOnly className="form-control" type="text" value={lastmodified}/>
                        </div>
                    </div>
                    <div>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => dispatch(MODIFY_PICTURE_UPDATE_BUTTON_CLICKED())}>
                            Update</button>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => dispatch(MODIFY_PICTURE_CANCEL_BUTTON_CLICKED())}>
                            Cancel</button>
                    </div>
                </div>
            </form>
        </div>
    );
}
