import { createReducer } from '@reduxjs/toolkit';
import { emptyUser, emptyUserAndPasswords, emptyRole, emptyPermission } from '../constants';
import { PASSWORDS_UPDATE } from '../actiontypes';

const passwordsReducer = createReducer({ ...emptyUserAndPasswords }, {
    [PASSWORDS_UPDATE]: (state, action) => {
        const passwords = { ...state, ...action.payload };
        const passwordsNotIdentical = checkIfPasswordsAreNotIdentical(passwords);
        return { ...passwords, passwordsNotIdentical };
    },
});

export default passwordsReducer;


function checkIfPasswordsAreNotIdentical(passwords) {
    let { password1, password2 } = passwords;
    if (!password2) {
        return false; // if second password is empty we don't compare because it probably hasn't been typed into yet
    }

    return password1 !== password2;
}
