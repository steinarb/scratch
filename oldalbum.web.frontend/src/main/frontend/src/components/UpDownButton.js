import React from 'react';
import { useSelector } from 'react-redux';
import UpButton from './UpButton';
import DownButton from './DownButton';

export default function UpDownButton(props) {
    const { item } = props;
    const showEditControls = useSelector(state => state.showEditControls);

    if (!showEditControls) {
        return null;
    }

    return(
        <div className="d-block d-md-none btn-group-vertical justify-content-end">
            <UpButton item={item} />
            <DownButton item={item} />
        </div>
    );
}
