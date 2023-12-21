import React from 'react';
import DataTransferDownload from './bootstrap/DataTransferDownload';
import { useSelector, useDispatch } from 'react-redux';
import { START_SELECTION_DOWNLOAD } from '../reduxactions';
import { stringify } from 'qs';

export default function DownloadButton(props) {
    const { className = '', item } = props;
    const routerBasename = useSelector(state => state.router.basename);
    const selectedentries = useSelector(state => state.selectedentries.map(e => e.id));
    const text = useSelector(state => state.displayTexts);
    const dispatch = useDispatch();
    const filename = item.album ? item.path.split('/').at(-2) + '.zip' : item.imageUrl.split('/').at(-1);
    const buttonLabel = item.album ? selectedentries.length ? text.downloadselection : text.downloadalbum : text.downloadpicture;
    const basename = routerBasename == '/' ? '' : routerBasename;
    const href = selectedentries.length ?
          basename + '/api/image/downloadselection/' + item.id.toString() + '?' + stringify({ id: selectedentries }, { indices: false}):
          basename + '/api/image/download/' + item.id.toString();

    return (
        <a
            className={className + ' download-button alert'}
            href={href}
            download={filename}
            target="_blank"
            rel="noopener noreferrer"
            onClick={() => dispatch(START_SELECTION_DOWNLOAD())}
        >
            <DataTransferDownload/>
            &nbsp;
            {buttonLabel}
        </a>
    );
}
