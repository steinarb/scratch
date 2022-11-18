import { createReducer } from '@reduxjs/toolkit';
import Cookies from 'js-cookie';
import {
    TOGGLE_EDIT_MODE_ON,
    TOGGLE_EDIT_MODE_OFF,
} from '../reduxactions';

const currentEditMode = Cookies.get('editMode') || false;

const localeReducer = createReducer(currentEditMode, {
    [TOGGLE_EDIT_MODE_ON]: () => true,
    [TOGGLE_EDIT_MODE_OFF]: () => false,
});

export default localeReducer;
