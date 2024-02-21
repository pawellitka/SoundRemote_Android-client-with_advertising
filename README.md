# SoundRemote android client

An Android app that, when paired up with [SoundRemote server](https://github.com/ashipo/SoundRemote-server), allows to:
- Capture and stream audio from a PC to an Android device
- Send back keyboard commands, media keys from the Android media notification and bind commands to certain events like device shake or phone call

<img src="https://github.com/ashipo/SoundRemote-android/assets/24320267/2086773b-536b-4240-a4c3-556b8c506f32" alt="Home screen" title="Home screen" width="300"/>
<img src="https://github.com/ashipo/SoundRemote-android/assets/24320267/ed2c87b1-e03e-4c03-9a29-9548f6bdbfa1" alt="Events screen" title="Events screen" width="300"/>
<img src="https://github.com/ashipo/SoundRemote-android/assets/24320267/7ad183d2-e749-434b-8e05-a63c4a69209b" alt="Notification" title="Notification" width="300"/>

## Build

App requires [JOpus](https://github.com/ashipo/JOpus) library. Put the `.aar` file to `libs` directory in the `app` module, for example `SoundRemote/app/libs/jopus-release.aar`
