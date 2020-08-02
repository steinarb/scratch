
export function prepareAlbumentryForEdit(albumentry) {
    if (albumentry === undefined) { return albumentry; }
    const { path } = albumentry;
    const basename = path !== undefined ? path.replace(/\/$/, "").split(/\//).pop() : undefined;
    return { ...albumentry, basename };
}

export function updateParent(state, action, addTrailingSlash) {
    const parentalbum = action.payload || {};
    const parent = parentalbum.id;
    const path = parentalbum.path + state.basename + (addTrailingSlash ? '/' : '');
    return { ...state, parent, path };
}

export function updateBasename(state, action, addTrailingSlash) {
    const { basename, parentalbum } = action.payload;
    const path = parentalbum.path + basename + (addTrailingSlash ? '/' : '');
    return { ...state, path, basename };
}
