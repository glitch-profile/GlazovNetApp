** THIS APPLICATION WAS MADE AS A GRADUATION PROJECT AND HAS NO RELATION TO THE PROVIDER GLAZOV.NET **

Mobile application for managing personal account and viewing information on provider "Glazov.NET". The developed [API](https://github.com/glitch-profile/api-glazovnet) is used as a server part.

- The application is written in Kotlin language only for Android 10 and higher systems. Jetpack Compose is used as the UI. 
- KTOR client is used to send web requests via REST and WebSockets.
- Dependencies injection is done with the help of Gagger-Hilt library. 
- Coil and Lottie libraries are used to load images and animations
- Work with PUSH-notifications is performed with the help of Google Firebase

The application is designed for clients and employees of the company. Depending on the level of access and available user roles, the application interface changes.

The whole list of supported functionality:
- Authentication 
- News management
- Connection of tariffs and additional services
- Creating requests to tech support and chat for each request (WebSockets)
- Sending announcements to specific addresses or for specific customers
- Adding service news for company employees
- Managing mailings and processing PUSH notifications
- Transaction history for each user with additional description for each transaction
