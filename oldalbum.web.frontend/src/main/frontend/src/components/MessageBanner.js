import React from 'react';
import { useSelector } from 'react-redux';

export default function MessageBanner() {
    const messageBanner = useSelector(state => state.messageBanner);

    if (!messageBanner) {
        return null;
    }

    return(
        <div className="alert alert-success" role="alert">{messageBanner}</div>
    );
}
