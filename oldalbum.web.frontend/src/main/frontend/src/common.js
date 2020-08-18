
export function addWebcontextToPath(albumentry, webcontext) {
    const path = webcontext + albumentry.path;
    return { ...albumentry, path };
}

export function removeWebcontextFromPath(albumentry, webcontext) {
    const path = albumentry.path.replace(webcontext, '');
    return { ...albumentry, path };
}
