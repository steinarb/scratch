import React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router';
import { Link } from 'react-router-dom';
import { userIsNotLoggedIn } from '../common/login';
import {
    LOGOUT_REQUEST,
    RECENTJOBS_REQUEST,
    UPDATE_JOBS,
    UPDATE_ACCOUNT,
    DELETE_JOBS_REQUEST,
} from '../actiontypes';
import Locale from './Locale';
import Accounts from './Accounts';

function AdminJobsDelete(props) {
    if (userIsNotLoggedIn(props)) {
        return <Redirect to="/ukelonn/login" />;
    }

    let { text, account, jobs, accounts, onLogout, onAccountsFieldChange, onCheckboxTicked, onDeleteMarkedJobs } = props;

    return (
        <div>
            <nav>
                <Link to="/ukelonn/admin/jobtypes">
                    &lt;-
                    &nbsp;
                    {text.administrateJobsAndJobTypes}
                </Link>
                <h1>{text.deleteErronouslyRegisteredJobsFor} {account.firstName}</h1>
                <Locale />
            </nav>

            <div>
                <p><em>{text.note}</em> {text.onlyMisregistrationsShouldBeDeleted}
                    <br/>
                    <em>{text.doNot}</em> {text.deleteJobsThatAreToBePaidFor}</p>

                <label htmlFor="account-selector">{text.chooseAccount}:</label>
                <Accounts  id="account-selector" value={account.accountId} accounts={accounts} onAccountsFieldChange={onAccountsFieldChange}/>

                <table className="table table-bordered">
                    <thead>
                        <tr>
                            <td>{text.delete}</td>
                            <td>{text.date}</td>
                            <td>{text.jobs}</td>
                            <td>{text.amount}</td>
                        </tr>
                    </thead>
                    <tbody>
                        {jobs.map((job) =>
                                  <tr key={job.id}>
                                      <td><input type="checkbox" checked={job.delete} onChange={(e) => onCheckboxTicked(e.target.checked, job, jobs)}/></td>
                                      <td>{job.transactionTime}</td>
                                      <td>{job.name}</td>
                                      <td>{job.transactionAmount}</td>
                                  </tr>
                                 )}
                    </tbody>
                </table>
                <button onClick={() => onDeleteMarkedJobs(account, jobs)}>{text.deleteMarkedJobs}</button>
            </div>
            <br/>
            <br/>
            <button onClick={() => onLogout()}>{text.logout}</button>
            <br/>
            <a href="../../../..">{text.returnToTop}</a>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        text: state.displayTexts,
        haveReceivedResponseFromLogin: state.haveReceivedResponseFromLogin,
        loginResponse: state.loginResponse,
        account: state.account,
        jobs: state.jobs,
        accounts: state.accounts,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        onLogout: () => dispatch(LOGOUT_REQUEST()),
        onJobs: (account) => dispatch(RECENTJOBS_REQUEST(account.accountId)),
        onAccountsFieldChange: (selectedValue, accounts) => {
            const selectedValueInt = parseInt(selectedValue, 10);
            let account = accounts.find(account => account.accountId === selectedValueInt);
            dispatch(UPDATE_ACCOUNT(account));
            dispatch(RECENTJOBS_REQUEST(account.accountId));
        },
        onCheckboxTicked: (deleteChecked, job, origJobs) => {
            const jobs = origJobs.map(j => (j.id === job.id) ? { ...job, delete: deleteChecked } : j);
            dispatch(UPDATE_JOBS(jobs));
        },
        onDeleteMarkedJobs: (account, jobs) => {
            const jobsToDelete = jobs.filter(job => job.delete);
            dispatch(DELETE_JOBS_REQUEST({ account, jobsToDelete }));
        },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminJobsDelete);
