import { createAction } from '@reduxjs/toolkit';

export const SET_ALERT = createAction('SET_ALERT');
export const CLEAR_ALERT = createAction('CLEAR_ALERT');
export const ALLROUTES_REQUEST = createAction('ALLROUTES_REQUEST');
export const ALLROUTES_RECEIVE = createAction('ALLROUTES_RECEIVE');
export const ALLROUTES_FAILURE = createAction('ALLROUTES_FAILURE');
export const LOGIN_CHECK_REQUEST = createAction('LOGIN_CHECK_REQUEST');
export const LOGIN_CHECK_RECEIVE = createAction('LOGIN_CHECK_RECEIVE');
export const LOGIN_CHECK_FAILURE = createAction('LOGIN_CHECK_FAILURE');
export const LOGIN_REQUEST = createAction('LOGIN_REQUEST');
export const LOGIN_RECEIVE = createAction('LOGIN_RECEIVE');
export const LOGIN_FAILURE = createAction('LOGIN_FAILURE');
export const LOGOUT_REQUEST = createAction('LOGOUT_REQUEST');
export const LOGOUT_RECEIVE = createAction('LOGOUT_RECEIVE');
export const LOGOUT_FAILURE = createAction('LOGOUT_FAILURE');
export const SET_ALBUM_GROUP_BY_YEAR = createAction('SET_ALBUM_GROUP_BY_YEAR');
export const UNSET_ALBUM_GROUP_BY_YEAR = createAction('UNSET_ALBUM_GROUP_BY_YEAR');
export const TOGGLE_EDIT_MODE_ON = createAction('TOGGLE_EDIT_MODE_ON');
export const TOGGLE_EDIT_MODE_OFF = createAction('TOGGLE_EDIT_MODE_OFF');
export const SHOW_EDIT_CONTROLS = createAction('SHOW_EDIT_CONTROLS');
export const HIDE_EDIT_CONTROLS = createAction('HIDE_EDIT_CONTROLS');
export const FILL_MODIFY_ALBUM_FORM = createAction('FILL_MODIFY_ALBUM_FORM');
export const CLEAR_ALBUM_FORM = createAction('CLEAR_ALBUM_FORM');
export const MODIFY_ALBUM_PARENT_SELECTED = createAction('MODIFY_ALBUM_PARENT_SELECTED');
export const MODIFY_ALBUM_BASENAME_FIELD_CHANGED = createAction('MODIFY_ALBUM_BASENAME_FIELD_CHANGED');
export const MODIFY_ALBUM_TITLE_FIELD_CHANGED = createAction('MODIFY_ALBUM_TITLE_FIELD_CHANGED');
export const MODIFY_ALBUM_DESCRIPTION_FIELD_CHANGED = createAction('MODIFY_ALBUM_DESCRIPTION_FIELD_CHANGED');
export const MODIFY_ALBUM_REQUIRE_LOGIN_FIELD_CHANGED = createAction('MODIFY_ALBUM_REQUIRE_LOGIN_FIELD_CHANGED');
export const MODIFY_ALBUM_UPDATE_BUTTON_CLICKED = createAction('MODIFY_ALBUM_UPDATE_BUTTON_CLICKED');
export const MODIFY_ALBUM_CANCEL_BUTTON_CLICKED = createAction('MODIFY_ALBUM_CANCEL_BUTTON_CLICKED');
export const SAVE_MODIFIED_ALBUM_REQUEST = createAction('SAVE_MODIFIED_ALBUM_REQUEST');
export const SAVE_MODIFIED_ALBUM_RECEIVE = createAction('SAVE_MODIFIED_ALBUM_RECEIVE');
export const SAVE_MODIFIED_ALBUM_FAILURE = createAction('SAVE_MODIFIED_ALBUM_FAILURE');
export const FILL_ADD_ALBUM_FORM = createAction('FILL_ADD_ALBUM_FORM');
export const ADD_ALBUM_BASENAME_FIELD_CHANGED = createAction('ADD_ALBUM_BASENAME_FIELD_CHANGED');
export const ADD_ALBUM_TITLE_FIELD_CHANGED = createAction('ADD_ALBUM_TITLE_FIELD_CHANGED');
export const ADD_ALBUM_DESCRIPTION_FIELD_CHANGED = createAction('ADD_ALBUM_DESCRIPTION_FIELD_CHANGED');
export const ADD_ALBUM_REQUIRE_LOGIN_FIELD_CHANGED = createAction('ADD_ALBUM_REQUIRE_LOGIN_FIELD_CHANGED');
export const ADD_ALBUM_UPDATE_BUTTON_CLICKED = createAction('ADD_ALBUM_UPDATE_BUTTON_CLICKED');
export const ADD_ALBUM_CANCEL_BUTTON_CLICKED = createAction('ADD_ALBUM_CANCEL_BUTTON_CLICKED');
export const SAVE_ADDED_ALBUM_REQUEST = createAction('SAVE_ADDED_ALBUM_REQUEST');
export const SAVE_ADDED_ALBUM_RECEIVE = createAction('SAVE_ADDED_ALBUM_RECEIVE');
export const SAVE_ADDED_ALBUM_FAILURE = createAction('SAVE_ADDED_ALBUM_FAILURE');
export const BATCH_ADD_URL_FIELD_CHANGED = createAction('BATCH_ADD_URL_FIELD_CHANGED');
export const IMPORT_YEAR_FIELD_CHANGED = createAction('IMPORT_YEAR_FIELD_CHANGED');
export const BATCH_ADD_PICTURES_FROM_URL_REQUEST = createAction('BATCH_ADD_PICTURES_FROM_URL_REQUEST');
export const BATCH_ADD_PICTURES_FROM_URL_RECEIVE = createAction('BATCH_ADD_PICTURES_FROM_URL_RECEIVE');
export const BATCH_ADD_PICTURES_FROM_URL_FAILURE = createAction('BATCH_ADD_PICTURES_FROM_URL_FAILURE');
export const CLEAR_BATCH_ADD_URL_FIELD = createAction('CLEAR_BATCH_ADD_URL_FIELD');
export const FILL_MODIFY_PICTURE_FORM = createAction('FILL_MODIFY_PICTURE_FORM');
export const CLEAR_PICTURE_FORM = createAction('CLEAR_PICTURE_FORM');
export const MODIFY_PICTURE_PARENT_SELECTED = createAction('MODIFY_PICTURE_PARENT_SELECTED');
export const MODIFY_PICTURE_BASENAME_FIELD_CHANGED = createAction('MODIFY_PICTURE_BASENAME_FIELD_CHANGED');
export const MODIFY_PICTURE_TITLE_FIELD_CHANGED = createAction('MODIFY_PICTURE_TITLE_FIELD_CHANGED');
export const MODIFY_PICTURE_DESCRIPTION_FIELD_CHANGED = createAction('MODIFY_PICTURE_DESCRIPTION_FIELD_CHANGED');
export const MODIFY_PICTURE_IMAGEURL_FIELD_CHANGED = createAction('MODIFY_PICTURE_IMAGEURL_FIELD_CHANGED');
export const MODIFY_PICTURE_THUMBNAILURL_FIELD_CHANGED = createAction('MODIFY_PICTURE_THUMBNAILURL_FIELD_CHANGED');
export const MODIFY_PICTURE_LASTMODIFIED_FIELD_CHANGED = createAction('MODIFY_PICTURE_LASTMODIFIED_FIELD_CHANGED');
export const MODIFY_PICTURE_REQUIRE_LOGIN_FIELD_CHANGED = createAction('MODIFY_PICTURE_REQUIRE_LOGIN_FIELD_CHANGED');
export const MODIFY_PICTURE_UPDATE_BUTTON_CLICKED = createAction('MODIFY_PICTURE_UPDATE_BUTTON_CLICKED');
export const MODIFY_PICTURE_CANCEL_BUTTON_CLICKED = createAction('MODIFY_PICTURE_CANCEL_BUTTON_CLICKED');
export const SAVE_MODIFIED_PICTURE_REQUEST = createAction('SAVE_MODIFIED_PICTURE_REQUEST');
export const SAVE_MODIFIED_PICTURE_RECEIVE = createAction('SAVE_MODIFIED_PICTURE_RECEIVE');
export const SAVE_MODIFIED_PICTURE_FAILURE = createAction('SAVE_MODIFIED_PICTURE_FAILURE');
export const FILL_ADD_PICTURE_FORM = createAction('FILL_ADD_PICTURE_FORM');
export const ADD_PICTURE_BASENAME_FIELD_CHANGED = createAction('ADD_PICTURE_BASENAME_FIELD_CHANGED');
export const ADD_PICTURE_TITLE_FIELD_CHANGED = createAction('ADD_PICTURE_TITLE_FIELD_CHANGED');
export const ADD_PICTURE_DESCRIPTION_FIELD_CHANGED = createAction('ADD_PICTURE_DESCRIPTION_FIELD_CHANGED');
export const ADD_PICTURE_IMAGEURL_FIELD_CHANGED = createAction('ADD_PICTURE_IMAGEURL_FIELD_CHANGED');
export const ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED = createAction('ADD_PICTURE_THUMBNAILURL_FIELD_CHANGED');
export const ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED = createAction('ADD_PICTURE_LASTMODIFIED_FIELD_CHANGED');
export const ADD_PICTURE_REQUIRE_LOGIN_FIELD_CHANGED = createAction('ADD_PICTURE_REQUIRE_LOGIN_FIELD_CHANGED');
export const ADD_PICTURE_UPDATE_BUTTON_CLICKED = createAction('ADD_PICTURE_UPDATE_BUTTON_CLICKED');
export const ADD_PICTURE_CANCEL_BUTTON_CLICKED = createAction('ADD_PICTURE_CANCEL_BUTTON_CLICKED');
export const SAVE_ADDED_PICTURE_REQUEST = createAction('SAVE_ADDED_PICTURE_REQUEST');
export const SAVE_ADDED_PICTURE_RECEIVE = createAction('SAVE_ADDED_PICTURE_RECEIVE');
export const SAVE_ADDED_PICTURE_FAILURE = createAction('SAVE_ADDED_PICTURE_FAILURE');
export const IMAGE_METADATA_REQUEST = createAction('IMAGE_METADATA_REQUEST');
export const IMAGE_METADATA_RECEIVE = createAction('IMAGE_METADATA_RECEIVE');
export const IMAGE_METADATA_FAILURE = createAction('IMAGE_METADATA_FAILURE');
export const DELETE_ALBUMENTRY_REQUEST = createAction('DELETE_ALBUMENTRY_REQUEST');
export const DELETE_ALBUMENTRY_RECEIVE = createAction('DELETE_ALBUMENTRY_RECEIVE');
export const DELETE_ALBUMENTRY_FAILURE = createAction('DELETE_ALBUMENTRY_FAILURE');
export const SORT_ALBUM_ENTRIES_BY_DATE_REQUEST = createAction('SORT_ALBUM_ENTRIES_BY_DATE_REQUEST');
export const SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE = createAction('SORT_ALBUM_ENTRIES_BY_DATE_RECEIVE');
export const SORT_ALBUM_ENTRIES_BY_DATE_FAILURE = createAction('SORT_ALBUM_ENTRIES_BY_DATE_FAILURE');
export const MOVE_ALBUMENTRY_UP_REQUEST = createAction('MOVE_ALBUMENTRY_UP_REQUEST');
export const MOVE_ALBUMENTRY_UP_RECEIVE = createAction('MOVE_ALBUMENTRY_UP_RECEIVE');
export const MOVE_ALBUMENTRY_UP_FAILURE = createAction('MOVE_ALBUMENTRY_UP_FAILURE');
export const MOVE_ALBUMENTRY_DOWN_REQUEST = createAction('MOVE_ALBUMENTRY_DOWN_REQUEST');
export const MOVE_ALBUMENTRY_DOWN_RECEIVE = createAction('MOVE_ALBUMENTRY_DOWN_RECEIVE');
export const MOVE_ALBUMENTRY_DOWN_FAILURE = createAction('MOVE_ALBUMENTRY_DOWN_FAILURE');
export const MOVE_ALBUMENTRY_LEFT_REQUEST = createAction('MOVE_ALBUMENTRY_LEFT_REQUEST');
export const MOVE_ALBUMENTRY_LEFT_RECEIVE = createAction('MOVE_ALBUMENTRY_LEFT_RECEIVE');
export const MOVE_ALBUMENTRY_LEFT_FAILURE = createAction('MOVE_ALBUMENTRY_LEFT_FAILURE');
export const MOVE_ALBUMENTRY_RIGHT_REQUEST = createAction('MOVE_ALBUMENTRY_RIGHT_REQUEST');
export const MOVE_ALBUMENTRY_RIGHT_RECEIVE = createAction('MOVE_ALBUMENTRY_RIGHT_RECEIVE');
export const MOVE_ALBUMENTRY_RIGHT_FAILURE = createAction('MOVE_ALBUMENTRY_RIGHT_FAILURE');
export const SELECT_PICTURE_ALBUMENTRY = createAction('SELECT_PICTURE_ALBUMENTRY');
export const UNSELECT_PICTURE_ALBUMENTRY = createAction('UNSELECT_PICTURE_ALBUMENTRY');
export const CLEAR_SELECTION = createAction('CLEAR_SELECTION');
export const START_SELECTION_DOWNLOAD = createAction('START_SELECTION_DOWNLOAD');
export const DEFAULT_LOCALE_REQUEST = createAction('DEFAULT_LOCALE_REQUEST');
export const DEFAULT_LOCALE_RECEIVE = createAction('DEFAULT_LOCALE_RECEIVE');
export const DEFAULT_LOCALE_FAILURE = createAction('DEFAULT_LOCALE_FAILURE');
export const SELECT_LOCALE = createAction('SELECT_LOCALE');
export const AVAILABLE_LOCALES_REQUEST = createAction('AVAILABLE_LOCALES_REQUEST');
export const AVAILABLE_LOCALES_RECEIVE = createAction('AVAILABLE_LOCALES_RECEIVE');
export const AVAILABLE_LOCALES_FAILURE = createAction('AVAILABLE_LOCALES_FAILURE');
export const DISPLAY_TEXTS_REQUEST = createAction('DISPLAY_TEXTS_REQUEST');
export const DISPLAY_TEXTS_RECEIVE = createAction('DISPLAY_TEXTS_RECEIVE');
export const DISPLAY_TEXTS_FAILURE = createAction('DISPLAY_TEXTS_FAILURE');
