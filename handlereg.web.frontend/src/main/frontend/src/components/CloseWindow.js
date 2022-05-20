import React from 'react';

export default function CloseWindow() {
    return (
        <input className="btn btn-primary" type="submit" value="Lukk" onClick={() => open(location, '_self').close()} />
    );
}
