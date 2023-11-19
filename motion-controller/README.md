# Motion Controller

<video src="https://user-images.githubusercontent.com/6410412/284037217-6dbbdcce-1cf4-4c92-b903-15f670bfa9bc.mp4" muted controls></video>

This is an example of using an Android device as a game controller for another device. When the "ACC" switch in the app is turned on, you can use the mobile device as a motion controller through the accelerometer sensor. The server receives UI events and accelerometer data from the client and maps them to specific keys on the keyboard, allowing you to enjoy existing games without modification.

## Server

```
$ cd examples/accelerometer-cursor
$ npm install
$ npm build
$ npm start
```

## Client

In Android Studio, open the `examples/motion-controller` project, then build and run it.
