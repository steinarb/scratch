import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { parse } from 'qs';
import {
    ADD_PICTURE_BASENAME_FIELD_CHANGED,
    ADD_PICTURE_TITLE_FIELD_CHANGED,
    ADD_PICTURE_DESCRIPTION_FIELD_CHANGED,
    ADD_PICTURE_IMAGEURL_FIELD_CHANGED,
    ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED,
    ADD_PICTURE_UPDATE_BUTTON_CLICKED,
    ADD_PICTURE_CANCEL_BUTTON_CLICKED,
    IMAGE_METADATA_REQUEST,
} from '../reduxactions';

function AddPicture(props) {
    const {
        path,
        basename,
        title,
        description,
        imageUrl,
        thumbnailUrl,
        lastModified,
        contentLength,
        contentType,
        albums,
    } = props;
    const dispatch = useDispatch();
    const queryParams = parse(props.location.search, { ignoreQueryPrefix: true });
    const { parent } = queryParams;
    const parentId = parseInt(parent, 10);
    const parentalbum = albums.find(a => a.id === parentId);
    const uplocation = parentalbum.path || '/';
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
                <h1>Add picture to &quot;{parentalbum.title}&quot;</h1>
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
                                onChange={e => dispatch(ADD_PICTURE_BASENAME_FIELD_CHANGED(e.target.value))} />
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
                                onChange={e => dispatch(ADD_PICTURE_TITLE_FIELD_CHANGED(e.target.value))} />
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
                                onChange={e => dispatch(ADD_PICTURE_DESCRIPTION_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="imageUrl" className="col-form-label col-5">Image URL</label>
                        <div className="col-7">
                            <input
                                id="imageUrl"
                                className="form-control"
                                type="text"
                                value={imageUrl}
                                onChange={e => dispatch(ADD_PICTURE_IMAGEURL_FIELD_CHANGED(e.target.value))} />
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="thumbnailUrl" className="col-form-label col-5">Thumbnail URL</label>
                        <div className="col-7">
                            <input
                                id="thumbnailUrl"
                                className="form-control"
                                type="text"
                                value={thumbnailUrl}
                                onChange={e => dispatch(ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED(e.target.value))} />
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
                            onClick={() => dispatch(ADD_PICTURE_UPDATE_BUTTON_CLICKED())}>
                            Add</button>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={() => dispatch(ADD_PICTURE_CANCEL_BUTTON_CLICKED())}>
                            Cancel</button>
                    </div>
                </div>
            </form>
        </div>
    );
}

function mapStateToProps(state) {
    const parent = state.albumentryParent;
    const path = state.albumentryPath;
    const basename = state.albumentryBasename;
    const title = state.albumentryTitle;
    const description = state.albumentryDescription;
    const imageUrl = state.albumentryImageUrl;
    const thumbnailUrl = state.albumentryThumbnailUrl;
    const lastModified = state.albumentryLastModified;
    const contentLength = state.albumentryContentLength;
    const contentType = state.albumentryContentType;
    const albums = state.allroutes.filter(r => r.album) || [];
    return {
        parent,
        path,
        basename,
        title,
        description,
        imageUrl,
        thumbnailUrl,
        lastModified,
        contentLength,
        contentType,
        albums,
    };
}

export default connect(mapStateToProps)(AddPicture);
