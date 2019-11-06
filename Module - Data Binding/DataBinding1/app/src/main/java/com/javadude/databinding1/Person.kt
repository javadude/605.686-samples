package com.javadude.databinding1

import androidx.lifecycle.MutableLiveData

class Person(name:String, age:Int) {
    val name = MutableLiveData<String>()
    var age = MutableLiveData<Int>()

    init {
        this.name.value = name
        this.age.value = age
    }
}
//class Person(name:String, age:Int) {
//    val name = ObservableField<String>()
//    val age = ObservableInt()
//
//    init {
//        this.name.set(name)
//        this.age.set(age)
//    }
//}
//class Person(name:String, age:Int) : BaseObservable() {
//    var name : String = ""
//        @Bindable get
//        set(value) {
//            field = value
//            Log.d("!!!PERSON", "name=$value")
//            notifyPropertyChanged(BR.name)
//        }
//    var age : Int = 0
//        @Bindable get
//        set(value) {
//            field = value
//            Log.d("!!!PERSON", "age=$value")
//            notifyPropertyChanged(BR.age)
//        }
//
//    init {
//        this.name = name
//        this.age = age
//    }
//}