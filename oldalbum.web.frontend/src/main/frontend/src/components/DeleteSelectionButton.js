import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { DELETE_SELECTION_REQUEST } from '../reduxactions';

export default function DeleteSelectionButton(props) {
    const text = useSelector(state => state.displayTexts);
    const showEditControls = useSelector(state => state.showEditControls);
    const selectedentries = useSelector(state => state.selectedentries);
    const dispatch = useDispatch();
    const selectionExists = !!selectedentries.length;

    // Button doesn't show up if: 1. edit not allowed, 2: there is no selection
    if (!showEditControls || !selectionExists) {
        return null;
    }

    return(<button
               className={(props.className || '') + ' btn btn-light'}
               type="button"
               onClick={() => dispatch(DELETE_SELECTION_REQUEST(selectedentries))}>
               {text.deleteselection}</button>);
}
