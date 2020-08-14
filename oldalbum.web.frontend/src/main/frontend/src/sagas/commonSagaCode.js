export function stripFieldsNotInAlbumEntryJavaBean(bean) {
    const { id, parent, path, album, title, description, imageUrl, thumbnailUrl, sort, childcount } = bean;
    return { id, parent, path, album, title, description, imageUrl, thumbnailUrl, sort, childcount };
}
