import { webcontext } from './constants';

export function addWebcontextToPath(albumentry) {
    const path = webcontext + albumentry.path;
    return { ...albumentry, path };
}

export function removeWebcontextFromPath(albumentry) {
    const path = albumentry.path.replace(webcontext, '');
    return { ...albumentry, path };
}
