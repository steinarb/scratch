
export function pictureTitle(item) {
    return item.title || (item.path && item.path.replace(/\/$/, "").split(/\//).pop()) || '';
}

export function formatLastModified(item) {
    return item.lastModified ? new Date(item.lastModified).toISOString().split('T')[0] + ' ' : '';
}

export function formatImageType(item)  {
    return item.contenttype === 'image/jpeg' ? 'JPEG ' : '';
}

export function formatImageSize(item) {
    const contentlengthInt = parseInt(item.contentLength, 10);
    if (contentlengthInt) {
        if (contentlengthInt / 1000000.0 > 1) {
            return Math.round(contentlengthInt / 1000000.0).toString() + 'MB';
        } else if (contentlengthInt / 1000.0 > 1) {
            return Math.round(contentlengthInt / 1000.0).toString() + 'kB';
        } else {
            return contentlengthInt.toString + 'B';
        }
    }
}

export function formatMetadata(item) {
    const lastmodified = formatLastModified(item);
    const imageType = formatImageType(item);
    let imagesize = formatImageSize(item);
    return lastmodified + imageType + imagesize;
}

export function viewSize() {
    const sizer = document.getElementById('sizer') || {};
    const divs = Array.prototype.slice.call(sizer.querySelectorAll('div'));
    const visibleDiv = divs.find(elem => elem.offsetWidth > 0 || elem.offsetHeight > 0);
    const selectedSize = (visibleDiv && visibleDiv.getAttribute('data-size'));
    return selectedSize;
}
