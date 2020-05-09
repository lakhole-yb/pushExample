// self.addEventListener('push',()=>{
//     self.registration.sendNotification('Hi doctor, You have a call on Touchbase.live');  
// })
self.addEventListener('push', event => {
    let body;
  
    if (event.data) {
      body = event.data.text();
    } else {
      body = 'Default body';
    }
  
    const options = {
      body: body,
      icon: 'images/notification-flat.png',
      vibrate: [100, 50, 100],
      data: {
        dateOfArrival: Date.now(),
        primaryKey: 1
      },
      actions: [
        {action: 'https://touchbase.live', title: 'Touchbase Live',
          icon: 'images/checkmark.png'},
        {action: 'close', title: 'Close the notification',
          icon: 'images/xmark.png'},
      ]
    };
  
    event.waitUntil(
      self.registration.showNotification('TouchBase Live', options)
    );
  });