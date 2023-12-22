import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
    BATCH_ADD_URL_FIELD_CHANGED,
    IMPORT_YEAR_FIELD_CHANGED,
    BATCH_ADD_PICTURES_FROM_URL_REQUEST,
} from '../reduxactions';

export default function BatchAddPictures(props) {
    const { item, className='' } = props;
    const { id } = item;
    const parent = id; // The new pictures will have this as a parent
    const text = useSelector(state => state.displayTexts);
    const showEditControls = useSelector(state => state.showEditControls);
    const batchAddUrl = useSelector(state => state.batchAddUrl);
    const importYear = useSelector(state => state.batchAddImportYear);
    const dispatch = useDispatch();

    if (!showEditControls) {
        return null;
    }

    return(
        <div className={className + ' d-none d-lg-block'}>
            <div className="container rounded border border-primary pt-2">
                <div className="form-group row">
                    <label htmlFor="batchAddUrl" className="col-form-label col-1">URL</label>
                    <div className="col-4">
                        <input
                            id="batchAddUrl"
                            className="form-control"
                            type="text"
                            value={batchAddUrl}
                            onChange={e => dispatch(BATCH_ADD_URL_FIELD_CHANGED(e.target.value))}/>
                    </div>
                    <label htmlFor="importYear" className="col-form-label col-1">{text.year}</label>
                    <div className="col-2">
                        <input
                            id="importYear"
                            className="form-control"
                            type="text"
                            value={importYear}
                            onChange={e => dispatch(IMPORT_YEAR_FIELD_CHANGED(e.target.value))}/>
                    </div>
                    <button
                        className="btn btn-light col-4"
                        type="button"
                        onClick={() => dispatch(BATCH_ADD_PICTURES_FROM_URL_REQUEST({ parent, batchAddUrl, importYear }))}>
                        {text.batchaddpictures}
                    </button>
                </div>
            </div>
        </div>
    );
}
