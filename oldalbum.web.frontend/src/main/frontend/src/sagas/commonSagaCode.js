export function stripFieldsNotInAlbumEntryJavaBean(bean) {
    const { id, parent, path, album, title, description, imageUrl, thumbnailUrl } = bean;
    return { id, parent, path, album, title, description, imageUrl, thumbnailUrl };
}
