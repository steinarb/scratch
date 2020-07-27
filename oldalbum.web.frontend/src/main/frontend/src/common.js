import { webcontext } from './constants';

export function addWebcontextToPath(albumentry) {
    const path = webcontext + albumentry.path;
    return { ...albumentry, path };
}
