import React from 'react';

export default function PictureCheckbox(props) {
    const { className='' } = props;
    const completeClassName = className + ' picture-checkbox';

    return (
        <input type="checkbox" className={completeClassName} />
    );
}
