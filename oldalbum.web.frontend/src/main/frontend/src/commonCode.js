export function findPathname(pathname, basename) {
    if (basename === '/') {
        return pathname;
    }

    return pathname.replace(new RegExp('^' + basename + '(.*)'), '$1');
}
