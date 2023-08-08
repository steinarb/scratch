import React from 'react';
import { useSelector } from 'react-redux';

export default function DownloadButton(props) {
    const { className = '', item } = props;
    const basename = useSelector(state => state.router.basename);
    const text = useSelector(state => state.displayTexts);
    const filename = item.imageUrl.split('/').at(-1);
    const buttonLabel = item.album ? text.downloadalbum : text.downloadpicture;

    return (
        <a
            className={className + ' alert alert-primary'}
            href={basename + '/api/image/download/' + item.id.toString()}
            download={filename}
            target="_blank"
            rel="noopener noreferrer"
        >
            {buttonLabel}
        </a>
    );
}
