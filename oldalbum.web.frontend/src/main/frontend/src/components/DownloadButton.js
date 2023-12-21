import React from 'react';
import DataTransferDownload from './bootstrap/DataTransferDownload';
import { useSelector } from 'react-redux';

export default function DownloadButton(props) {
    const { className = '', item } = props;
    const routerBasename = useSelector(state => state.router.basename);
    const selectedentries = useSelector(state => state.selectedentries);
    const text = useSelector(state => state.displayTexts);
    const filename = item.album ? item.path.split('/').at(-2) + '.zip' : item.imageUrl.split('/').at(-1);
    const buttonLabel = item.album ? selectedentries.length ? text.downloadselection : text.downloadalbum : text.downloadpicture;
    const basename = routerBasename == '/' ? '' : routerBasename;

    return (
        <a
            className={className + ' download-button alert'}
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
