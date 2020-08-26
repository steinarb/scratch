import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { pictureTitle, formatMetadata } from './commonComponentCode';
import LoginLogoutButton from './LoginLogoutButton';
import ModifyButton from './ModifyButton';
import DeleteButton from './DeleteButton';
import Previous from './Previous';
import Next from './Next';

function Picture(props) {
    const { item, parent, previous, next } = props;
    const title = pictureTitle(item);
    const metadata = formatMetadata(item);
    const description = item.description ? metadata ? item.description + ' ' + metadata : item.description : metadata;

    return (
        <div>
            <nav className="navbar navbar-light bg-light">
                <NavLink to={parent}>
                    <div className="container">
                        <div className="column">
                            <span className="row oi oi-chevron-top" title="chevron top" aria-hidden="true"></span>
                            <div className="row">Up</div>
                        </div>
                    </div>
                </NavLink>
                <h1>{title}</h1>
                <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavDropdown">
                    <div className="navbar-nav">
                        <LoginLogoutButton className="nav-item" item={item}/>
                    </div>
                </div>
            </nav>
            <div className="btn-toolbar" role="toolbar">
                <Previous previous={previous} />
                <Next className="ml-auto" next={next} />
            </div>
            <div className="btn-group" role="group" aria-label="Modify album">
                <ModifyButton className="mx-1 my-1" item={item} />
                <DeleteButton className="mx-1 my-1" item={item} />
            </div>
            <div>
                <img className="img-fluid" src={item.imageUrl} />
                {description && <div className="alert alert-primary" role="alert">{description}</div> }
            </div>
        </div>
    );
}

function mapStateToProps(state, ownProps) {
    const { item } = ownProps;
    const parentEntry = state.albumentries[item.parent] || {};
    const parent = parentEntry.path;
    const previous = state.previousentry[item.id];
    const next = state.nextentry[item.id];
    return {
        parent,
        previous,
        next,
    };
}

export default connect(mapStateToProps)(Picture);
