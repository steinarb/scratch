import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useGetConfigQuery, usePostConfigModifyMutation, api } from '../api';
import { setConfig, setExcessiveFailedLoginLimit } from '../reducers/configSlice';
import Container from './bootstrap/Container';
import StyledLinkLeft from './bootstrap/StyledLinkLeft';
import FormRow from './bootstrap/FormRow';
import FormLabel from './bootstrap/FormLabel';
import FormField from './bootstrap/FormField';
import ModifyFailedErrorAlert from './ModifyFailedErrorAlert';
import { findSelectedUser } from './common';

export default function Configuration() {
    const { data } = useGetConfigQuery();
    const config = useSelector(state => state.config);
    const dispatch = useDispatch();
    const [ postConfigModify ] = usePostConfigModifyMutation();
    const onModifyConfigClicked = async () => await postConfigModify(config);
    useEffect(() => {
        if (data) {
            dispatch(setConfig(data));
        }
    }, [data, dispatch]);

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <StyledLinkLeft to="/">Up to user adminstration top</StyledLinkLeft>
                <h1>Configuration</h1>
                <div className="col-sm-2"></div>
            </nav>
            <ModifyFailedErrorAlert/>
            <form onSubmit={ e => { e.preventDefault(); }}>
                <Container>
                    <FormRow>
                        <FormLabel htmlFor="excessiveFailedLoginLimit">Excessive failed login limit</FormLabel>
                        <FormField>
                            <input
                                id="excessiveFailedLoginLimit"
                                className="form-control"
                                type="text"
                                value={config.excessiveFailedLoginLimit}
                                onChange={e => dispatch(setExcessiveFailedLoginLimit(e.target.value))} />
                        </FormField>
                    </FormRow>
                    <FormRow>
                        <div className="col-5"/>
                        <FormField>
                            <button
                                className="btn btn-primary form-control"
                                onClick={onModifyConfigClicked}>
                                Modify configuration</button>
'                        </FormField>
                    </FormRow>
                </Container>
            </form>
        </div>
    );
}
