import { createReducer } from '@reduxjs/toolkit';
import {
    OPEN_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED,
    CLOSE_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED,
} from '../reduxactions';

const displayPasswordProtectionWarningDialogReducer = createReducer(false, (builder) => {
    builder
        .addCase(OPEN_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED, () => true)
        .addCase(CLOSE_WARNING_DIALOG_ENTRY_IS_PASSWORD_PROTECTED, () => false);
});

export default displayPasswordProtectionWarningDialogReducer;
