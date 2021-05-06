import React from 'react';

function ThumbnailImg(props) {
    const { entry } = props;

    if (entry.thumbnailUrl) {
        return (
            <img className="img-thumbnail" src={entry.thumbnailUrl} />
        );
    }

    return (
        <img className="img-thumbnail fullsize-img-thumbnail" src={entry.imageUrl} />
    );
}

export default ThumbnailImg;
