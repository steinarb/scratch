export function spawnNotification(notification) {
    console.log('spawnNotification(1)');
    console.log(notification);
    var options = {
        body: notification.message,
    };
    new Notification(notification.title, options);
}
