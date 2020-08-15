export function pictureTitle(item) {
    return item.title || (item.path && item.path.replace(/\/$/, "").split(/\//).pop()) || '';
}
