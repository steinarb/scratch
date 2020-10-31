import React from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import { push } from 'connected-react-router';
import { Helmet } from "react-helmet";
import { Swipeable } from 'react-swipeable';
import { pictureTitle, formatMetadata } from './commonComponentCode';
import LoginLogoutButton from './LoginLogoutButton';
import CopyLinkButton from './CopyLinkButton';
import ModifyButton from './ModifyButton';
import DeleteButton from './DeleteButton';
import Previous from './Previous';
import Next from './Next';

function Picture(props) {
    const { item, parent, previous, next, navigateTo } = props;
    const title = pictureTitle(item);
    const metadata = formatMetadata(item);
    const description = item.description ? metadata ? item.description + ' ' + metadata : item.description : metadata;

    return (
        <div>
            <Helmet>
                <title>{title}</title>
                <meta name="description" content={description}/>
            </Helmet>
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
                        <CopyLinkButton className="nav-item" />
                        <LoginLogoutButton className="nav-item" item={item}/>
                    </div>
                </div>
            </nav>
            <div className="btn-toolbar d-lg-none" role="toolbar">
                <Previous previous={previous} />
                <Next className="ml-auto" next={next} />
            </div>
            <div className="btn-group" role="group" aria-label="Modify album">
                <ModifyButton className="mx-1 my-1" item={item} />
                <DeleteButton className="mx-1 my-1" item={item} />
            </div>
            <Swipeable onSwipedLeft={() => navigateTo(next)} onSwipedRight={() => navigateTo(previous)}>
                <img className="img-fluid d-lg-none" src={item.imageUrl} />
                <div className="d-none d-lg-block">
                    <div className="row align-items-center d-flex justify-content-center">
                        <div className="col-auto">
                            <Previous previous={previous} />
                        </div>
                        <div className="col-auto">
                            <img className="img-fluid" src={item.imageUrl} />
                        </div>
                        <div className="col-auto">
                            <Next className="ml-auto" next={next} />
                        </div>
                    </div>
                </div>
                {description && <div className="alert alert-primary d-flex justify-content-center" role="alert">{description}</div> }
            </Swipeable>
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

function mapDispatchToProps(dispatch) {
    return {
        navigateTo: (item) => item && dispatch(push(item.path)),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Picture);
