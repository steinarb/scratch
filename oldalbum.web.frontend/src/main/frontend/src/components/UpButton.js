import React from 'react';
import { connect, useDispatch } from 'react-redux';
import { MOVE_ALBUMENTRY_UP_REQUEST } from '../reduxactions';

function UpButton(props) {
    const { item } = props;
    const {
        canModifyAlbum,
    } = props;
    const dispatch = useDispatch();
    // Button doesn't show up if: 1. edit not allowed, 2: this is the first entry in the album
    if (!canModifyAlbum || item.sort < 2) {
        return null;
    }

    return(<button
               className={props.className}
               type="button"
               onClick={() => dispatch(MOVE_ALBUMENTRY_UP_REQUEST(item))}>
               <span className="oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
           </button>);
}

function mapStateToProps(state) {
    return {
        canModifyAlbum: state.canModifyAlbum,
    };
}

export default connect(mapStateToProps)(UpButton);
