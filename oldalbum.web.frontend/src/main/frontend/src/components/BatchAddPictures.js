import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    BATCH_ADD_URL_FIELD_CHANGED,
    BATCH_ADD_PICTURES_FROM_URL,
} from '../reduxactions';

export default function BatchAddPictures(props) {
    const { item } = props;
    const { id } = item;
    const parent = id; // The new pictures will have this as a parent
    const showEditControls = useSelector(state => state.showEditControls);
    const batchAddUrl = useSelector(state => state.batchAddUrl);
    const dispatch = useDispatch();

    if (!showEditControls) {
        return null;
    }

    return(
        <div className={props.className}>
            <div className="container">
                <div className="form-group row">
                    <label htmlFor="batchAddUrl" className="col-form-label col-1">URL</label>
                    <div className="col-7">
                        <input
                            id="batchAddUrl"
                            className="form-control"
                            type="text"
                            value={batchAddUrl}
                            onChange={e => dispatch(BATCH_ADD_URL_FIELD_CHANGED(e.target.value))}/>
                    </div>
                    <button
                        className="btn btn-primary col-4"
                        type="button"
                        onClick={() => dispatch(BATCH_ADD_PICTURES_FROM_URL({parent}))}>
                        Batch add pictures
                    </button>
                </div>
            </div>
        </div>
    );
}
