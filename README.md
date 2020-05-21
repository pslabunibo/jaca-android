# The JaCa-Android Framework

This repository is under construction. The code of the new version of JaCa-Android will be added very soon.

## Team

* Prof. Alessandro Ricci, University of Bologna, Italy ([WebSite](https://www.unibo.it/sitoweb/a.ricci/en) | GitHub)
* Dr. Angelo Croatti, University of Bologna, Italy ([WebSite](https://www.unibo.it/sitoweb/a.croatti/en) | [GitHub](https://github.com/angelocroatti))

## Quick Guide

See the [Wiki](https://github.com/pslabunibo/jaca-android/wiki) related to this repository for all information on how to use JaCa-Android for creating MAS-based Android projects. 

To let your Android project accessing the JaCa-Android library, chose one of the following options.

### OPTION 1 - Import JaCa-Android via Maven Central

> Feature not available now. We are working to make JaCa-Android available on Maven Central Repository soon.

### OPTION 2 - Import JaCa-Android as Android Archive (AAR)

* Create a new Android Standard Project using Android Studio IDE
* Download the `jacaandroid-core-release.aar` from this repository
* Copy the AAR into the `/libs` folder of your project
* Include into the `build.gradle` file of your module (not the top-leve one) the local repositories access permission
```
repositories {
    flatDir {
        dirs 'libs'
    }
}
```
* Include in the same `build.gradle` file following dependences
```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    //Includes into the project the AAR availble into /libs
    implementation (name: 'jacaandroid-core-release', ext:'aar')

    //Include the legacy support used by JaCa-Android into the project
    implementation 'com.android.support:support-annotations:28.0.0'
    implementation 'com.android.support:support-v4:28.0.0'
}
```
