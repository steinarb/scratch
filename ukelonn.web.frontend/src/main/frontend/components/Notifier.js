import { spawnNotification } from './spawnnotification.js';
import { useDispatch } from 'react-redux';
import { useGetLoginQuery, useGetNotificationQuery, api } from '../api';

export default function Notifier() {
    const { data: loginResponse = {}, isSuccess: loginIsSuccess } = useGetLoginQuery();
    const { data: notifications, isLoading, isSuccess: notificationIsSuccess } = useGetNotificationQuery(loginResponse.username, {
        skip: !loginIsSuccess,
        pollingInterval: 60000,
    });
    const dispatch = useDispatch();
    console.log('Notifier(1)');
    if (isLoading) {
        console.log('Notifier(2)');
        return null;
    }

    console.log('Notifier(3)');
    if (!isLoading && notificationIsSuccess && notifications.length) {
        console.log('Notifier(4)');
        const notification = notifications[0];

        if (notification.message) {
            // Update the balance after payment
            dispatch(api.endpoints.getAccount.initiate(loginResponse.username));
            if (Notification) {
                spawnNotification(notification);
            } else {
                console.log('Notification not supported by browser');
            }
        }
    }

    return null;
}
