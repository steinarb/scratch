import moment from 'moment';

export function pictureTitle(item) {
    return item.title || (item.path && item.path.replace(/\/$/, "").split(/\//).pop()) || '';
}

export function formatMetadata(item) {
    const lastmodified = item.lastmodified ? moment(item.lastmodified).format("YYYY-MM-DD ") : '';
    const imageType = item.contenttype = 'image/jpeg' ? 'JPEG ' : '';
    const contentlengthInt = parseInt(item.contentlength, 10);
    let imagesize = '';
    if (contentlengthInt) {
        if (contentlengthInt / 1000000.0 > 1) {
            imagesize = Math.round(contentlengthInt / 1000000.0).toString() + 'MB';
        } else if (contentlengthInt / 1000.0 > 1) {
            imagesize = Math.round(contentlengthInt / 1000.0).toString() + 'kB';
        } else {
            imagesize = contentlengthInt.toString + 'B';
        }
    }
    return lastmodified + imageType + imagesize;
}

export function viewSize() {
    return $('#sizer').find('div:visible').data('size');
}
