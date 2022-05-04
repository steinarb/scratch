import React from 'react';
import { connect } from 'react-redux';
import { MOVE_ALBUMENTRY_LEFT } from '../reduxactions';
import ChevronLeft from './bootstrap/ChevronLeft';

function LeftButton(props) {
    const { loginresult, item, onMoveLeft } = props;
    // Button doesn't show up if: 1. edit not allowed, 2: this is the first entry in the album
    if (!loginresult.canModifyAlbum || item.sort < 2) {
        return null;
    }

    return(<button
               className={props.className}
               type="button"
               onClick={() => onMoveLeft(item)}>
               <ChevronLeft/>
           </button>);
}

function mapStateToProps(state) {
    return {
        loginresult: state.loginresult,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onMoveLeft: (item) => { dispatch(MOVE_ALBUMENTRY_LEFT(item)); },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(LeftButton);
