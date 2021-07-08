import React from 'react';

function PictureDescription(props) {
    const { className, description } = props;

    if (!description) {
        return null;
    }

    return (
        <div className={className}>
            <div className="alert alert-primary d-flex justify-content-center" role="alert">{description}</div>
        </div>
    );
}

export default PictureDescription;
