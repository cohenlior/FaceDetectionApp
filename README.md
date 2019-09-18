# FaceDetectionApp
Face detection application with MVVM architecture and Repository pattern
## The app is written in kotlin and uses:

* AndroidX packages.
* Data binding.
* Rx Java/Android for background operations. (Kotlin coroutines in additional branches)


## The app has following packages:

* domain- contains a data entity class which represents each image from the device private directory.
* repository - handles the detection process and helps with abstracting the data layer from the viewModel.
* ui- main activity and fragments with a recyclerView adapter for binding images.
  also, conatins viewModel class for storing and managing UI ralated data.
* service - forground service for keeping alive the face detection process.
* utils - binding adpter class for view binding, constants class and
  also contains imageDetector helper for managing the firebase detector api. 

## The app follows the Android Architectue Components desgin and uses:

* LiveData.
* ViewModel.
* Repository pattern.
* LifeCycle.

## Uses of third-party libraries in the app:

* Firebase ML kit for detection of faces.
* Picasso library for image loading and caching support.

## Common design patterns used in this app:

* Observer: It allows objects to be notified of any change of state of another object on which it depends. 
  presents in android viewModel
* Adapter: It allows two incompatible classes can work together. When creating a list of data, to inflate in a RecyclerView the Adapter   is used to send data from a controller to a particular view.
* ViewHolder: Used in the Adapters to reuse views in the inflation process of the cells. This brings us main benefit the proper    	       management of memory and fluency in lists. it's a mendatory in RecyclerView.

## Design Principles implemented are:

* DRY (Don’t Repeat Yourself)
* SRP: “Single Responsibility Principle” (part of SOLID principles)
* "Separation of concerns"
* "Dependency inversion principle"
