import React from 'react';
import { useSelector } from 'react-redux';

export default function DownloadButton(props) {
    const { className = '', item } = props;
    const basename = useSelector(state => state.router.basename);
    const text = useSelector(state => state.displayTexts);
    const filename = item.imageUrl.split('/').at(-1);

    return (
        <a
            className={className}
            href={basename + '/api/image/download/' + item.id.toString()}
            download={filename}
            target="_blank"
            rel="noopener noreferrer"
        >
            <button>{text.downloadpicture}</button>
        </a>
    );
}
