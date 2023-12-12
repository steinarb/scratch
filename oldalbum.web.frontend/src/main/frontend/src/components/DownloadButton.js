import React from 'react';
import DataTransferDownload from './bootstrap/DataTransferDownload';
import { useSelector } from 'react-redux';

export default function DownloadButton(props) {
    const { className = '', item } = props;
    const routerBasename = useSelector(state => state.router.basename);
    const text = useSelector(state => state.displayTexts);
    const filename = item.album ? item.path.split('/').at(-2) + '.zip' : item.imageUrl.split('/').at(-1);
    const buttonLabel = item.album ? text.downloadalbum : text.downloadpicture;
    const basename = routerBasename == '/' ? '' : routerBasename;

    return (
        <a
            className={className + ' alert alert-primary'}
            href={basename + '/api/image/download/' + item.id.toString()}
            download={filename}
            target="_blank"
            rel="noopener noreferrer"
        >
            <DataTransferDownload/>
            &nbsp;
            {buttonLabel}
        </a>
    );
}
