# QuizLand
[![Build](https://github.com/Haid-Faiz/QuizLand/actions/workflows/gradle_build_ci.yml/badge.svg)](https://github.com/Haid-Faiz/QuizLand/actions/workflows/gradle_build_ci.yml)
[![Lint](https://github.com/Haid-Faiz/QuizLand/actions/workflows/lint_check_ci.yml/badge.svg)](https://github.com/Haid-Faiz/QuizLand/actions/workflows/lint_check_ci.yml)
[![Platform](https://img.shields.io/badge/platform-android-blue.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-23%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=23)


**QuizLand** is a full fledge **Online Quiz Organiser Android app** that helps you create/organise quizes easily. You can share a quiz among the participants by it's unique id and get the results of participants right on the app, participants can also see thier result.

***You can Install the latest QuizLand app from below 👇***

[![QuizLand App](https://img.shields.io/badge/QuizLand-APK-%23121212?style=for-the-badge&logo=android)](https://github.com/Haid-Faiz/QuizLand/releases/download/v1.0.0/quiz_land_v1.0.0.apk)

## ScreenShots
<table>
   <ul>
      <li>
         <h4>App Intro | Quiz participation<h4>
      </li>
   </ul>
   <tr>
<td><img src = "https://user-images.githubusercontent.com/56159740/143897423-8cdb958e-3bfe-483a-a8d3-5a5b53eabaf1.gif" height = "370" width="200"></td>
<td><img src = "https://user-images.githubusercontent.com/56159740/144014767-43680b1f-49fd-4c4a-9749-07321fcb8633.jpg" height = "370" width="200"></td>
<td><img src = "https://user-images.githubusercontent.com/56159740/144013269-d8209228-6707-4aff-815a-68b83021b582.gif" height = "370" width="200"></td>
  </tr>
</table>

<table>
      <ul>
      <li>
         <h4>Create Quiz | Participants result<h4>
          </li>
   </ul>
  <tr>
<td><img src = "https://user-images.githubusercontent.com/56159740/143897402-f3074c6e-af6e-494e-a0f5-58ee26ca6378.gif" height = "370" width="200"></td>
<td><img src = "https://user-images.githubusercontent.com/56159740/143898449-0fe8d1da-68a8-4f08-9a20-f06e6f7071bf.jpg" height = "370" width="200"></td>
<td><img src = "https://user-images.githubusercontent.com/56159740/144015396-3711a2e6-4e41-43f5-b6c1-d73b5d08a7dc.jpg" height = "370" width="200"></td>
<td><img src = "https://user-images.githubusercontent.com/56159740/143995430-0fd56021-4847-4b83-9667-200ce5fd2478.gif" height = "370" width="200"></td>
  </tr>
</table>
   
## About

- **Uses Firebase Firestore, Storage, Google authentication**
- **MVVM Archiecture with ***Single Activity Structure*** (having around 14 fragments).**
- Clean and Simple Material UI.
- **Low APK Size (only 3.6 MB).**


### Following libraries were used in development ->

- [Firebase]() - Firebase for server side backend
- [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
  - [DataBinding](https://developer.android.com/topic/libraries/data-binding) - The Data Binding Library is a support library that allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
- [Dagger-Hilt](https://dagger.dev/hilt/) - Standard library to incorporate Dagger dependency injection into an Android application.
- [Jetpack Navigation](https://developer.android.com/guide/navigation) - Android Jetpack's Navigation component helps you implement navigation, from simple button clicks to more complex patterns.
- [Coil-kt](https://coil-kt.github.io/coil/) - An image loading library for Android backed by Kotlin Coroutines.
- [Lottie Animation](https://airbnb.io/lottie/#/android) - Lottie is a library for that parses animations exported as json and renders them natively.

