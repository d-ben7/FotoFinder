
# FotoFinder 

FotoFinder is a small demo application that let users browse and download free photos from Unsplash.

<img src="https://github.com/d-ben7/FotoFinder/blob/master/screenshots/screenshot1.png" align="left" width="300">
<img src="https://github.com/d-ben7/FotoFinder/blob/master/screenshots/screenshot2.png" align="left" width="300">
<img src="https://github.com/d-ben7/FotoFinder/blob/master/screenshots/screenshot3.png" width="300">

## Tech-stacks & 3rd party libraries
- [Kotlin](https://kotlinlang.org/)  + [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) for asynchronous tasks
- Android JetPack
  - [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - dependency injection
  - [Navigation](https://developer.android.com/guide/navigation/navigation-getting-started) - fragments management 
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - replaces findViewById()
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - observable data holder for views
  - [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) - lifecycle aware components
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - store and manage UI-related data in a lifecycle conscious way
  - [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) - pagination
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - networking
- [Moshi](https://github.com/square/moshi/) - parsing JSON
- [Coil](https://github.com/coil-kt/coil) - loading images
- [Toasty](https://github.com/GrenderG/Toasty) - custom toasts
- [PhotoView](https://github.com/Baseflow/PhotoView) - pinch to zoom image view
- [Assent](https://github.com/afollestad/assent) - runtime permissions
- [Leak Canary](https://square.github.io/leakcanary/) - leak detection

## Architecture

FotoFinder is based on the MVVM architecture and a repository pattern. More info about this recommended app architecture from Google [here](https://developer.android.com/jetpack/guide).

![architecture](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)
## Unsplash API

The [Official Unsplash API](https://unsplash.com/developers)

## How to Install

1. Sign up and get your private Unplash API key [here](https://unsplash.com/developers).
2. Open the `local.properties` file and add this line `api_key="YOUR_API_KEY_HERE"`

Note: If you are having network connection error while running the app on the Android Studio emulators, make sure your computer is not connected to a VPN.

## TODO

- Add tests
