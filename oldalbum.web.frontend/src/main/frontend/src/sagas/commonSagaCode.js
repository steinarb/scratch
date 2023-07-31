export function stripFieldsNotInAlbumEntryJavaBean(bean) {
    const { id, parent, path, album, title, description, imageUrl, thumbnailUrl, sort, lastModified, childcount } = bean;
    return { id, parent, path, album, title, description, imageUrl, thumbnailUrl, sort, lastModified, childcount };
}
