# Examples

## [Android Client-Server](./android-client-server)

This is an example of running the client and server on an Android device. Depending on the user's choice within a single application, it determines whether to run the client or the server. The client sends data obtained from the accelerometer sensor to the server, and the server moves a point on the screen based on the received data.

## [Motion controller](./motion-controller)

This is an example of using an Android device as a game controller for another device. When the "ACC" switch in the app is turned on, you can use the mobile device as a motion controller through the accelerometer sensor. The server receives UI events and accelerometer data from the client and maps them to specific keys on the keyboard, allowing you to enjoy existing games without modification.

## [Accelerometer Cursor](./accelerometer-cursor)

This is an example where tilting the client device using the accelerometer sensor of an Android device results in the movement of the mouse cursor on the server device according to the tilt.

## [Digital Ink](./digital-ink)

This is an example where handwriting drawn on the client device is recognized and transmitted to the server device for use.

## License

All projects in this repository are distributed under the [Apache License, Version 2.0](LICENSE).
