import React from 'react';
import { connect } from 'react-redux';
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

function ModifyPicture(props) {
    const {
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
        uplocation,
        onParentChange,
        onBasenameChange,
        onTitleChange,
        onDescriptionChange,
        onImageUrlChange,
        onImageLoaded,
        onThumbnailUrlChange,
        onUpdate,
        onCancel,
    } = props;
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
                            onLoad={() => onImageLoaded(imageUrl)} />
                    </div>
                    <div className="form-group row">
                        <label htmlFor="parent" className="col-form-label col-5">Parent</label>
                        <div className="col-7">
                            <select
                                id="parent"
                                className="form-control"
                                value={parent}
                                onChange={onParentChange}>
                                { albums.map((val) => <option key={'album_' + val.id} value={JSON.stringify(val)}>{val.title}</option>) }
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
                                onChange={onBasenameChange} />
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
                                onChange={onTitleChange} />
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
                                onChange={onDescriptionChange} />
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
                                onChange={onImageUrlChange} />
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
                                onChange={onThumbnailUrlChange} />
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
                            onClick={onUpdate}>
                            Update</button>
                        <button
                            className="btn btn-primary ml-1"
                            type="button"
                            onClick={onCancel}>
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
    const imageUrl = state.albumentryImageUrl;
    const thumbnailUrl = state.albumentryThumbnailUrl;
    const lastModified = state.albumentryLastModified;
    const contentLength = state.albumentryContentLength;
    const contentType = state.albumentryContentType;
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
        imageUrl,
        thumbnailUrl,
        lastModified,
        contentLength,
        contentType,
        albums,
        uplocation,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onParentChange: e => dispatch(MODIFY_PICTURE_PARENT_SELECTED(JSON.parse(e.target.value))),
        onBasenameChange: e => dispatch(MODIFY_PICTURE_BASENAME_FIELD_CHANGED(e.target.value)),
        onTitleChange: e => dispatch(MODIFY_PICTURE_TITLE_FIELD_CHANGED(e.target.value)),
        onDescriptionChange: e => dispatch(MODIFY_PICTURE_DESCRIPTION_FIELD_CHANGED(e.target.value)),
        onImageUrlChange: e => dispatch(MODIFY_PICTURE_IMAGEURL_FIELD_CHANGED(e.target.value)),
        onImageLoaded: imageUrl => dispatch(IMAGE_METADATA_REQUEST(imageUrl)),
        onThumbnailUrlChange: e => dispatch(MODIFY_PICTURE_THUMBNAILURL_FIELD_CHANGED(e.target.value)),
        onUpdate: () => dispatch(MODIFY_PICTURE_UPDATE_BUTTON_CLICKED()),
        onCancel: () => dispatch(MODIFY_PICTURE_CANCEL_BUTTON_CLICKED()),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(ModifyPicture);
