package com.javadude.databinding2.example07

import android.app.Application
import androidx.databinding.BaseObservable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

private val executor = Executors.newSingleThreadExecutor()

class SampleViewModel(application: Application) : AndroidViewModel(application) {
    private var db = Room.databaseBuilder(application, Database::class.java, "PEOPLE")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("INSERT INTO Address (id, ownerId, street, city, state, zip) VALUES('a1', 'p1', '123 Sesame Street', 'Laurel', 'MD', '20723')")
                    db.execSQL("INSERT INTO Address (id, ownerId, street, city, state, zip) VALUES('a2', 'p2', '11 Animated Lane', 'New York', 'NY', '10011')")
                    db.execSQL("INSERT INTO Person (id, name, age) VALUES('p1', 'Scott', 51)")
                    db.execSQL("INSERT INTO Person (id, name, age) VALUES('p2', 'Alex', 24)")
                }
            })
            .build()

    val personId = MutableLiveData<String>()
    //    val person = MediatorLiveData<PersonWrapper2>().apply {
//        addSource(personId) {
//            executor.execute {
//                postValue(PersonWrapper2(
//                    db.personDao.getByIdSync(it),
//                    AddressWrapper2(db.addressDao.getForOwnerIdSync(it))
//                ))
//            }
//        }
//    }
    val person = MediatorLiveData<PersonWrapper3>().apply {
        addSource(personId) {
            executor.execute {
                postValue(PersonWrapper3(
                    db.personDao.getByIdSync(it),
                    AddressWrapper3(db.addressDao.getForOwnerIdSync(it))
                ))
            }
        }
    }

//    val people = db.personDao.getAll()

    abstract inner class Wrapper3<WRAPPER_TYPE, REAL_TYPE>(
        private val obj : REAL_TYPE) : BaseObservable() {
        abstract val dao : IDao<REAL_TYPE>

        inner class Property<PROPERTY_TYPE>(private val baseProperty : KMutableProperty1<REAL_TYPE, PROPERTY_TYPE>) : ReadWriteProperty<WRAPPER_TYPE, PROPERTY_TYPE> {
            override fun getValue(thisRef: WRAPPER_TYPE, property: KProperty<*>) = baseProperty.get(obj)
            override fun setValue(thisRef: WRAPPER_TYPE, property: KProperty<*>, value: PROPERTY_TYPE) {
                baseProperty.set(obj, value)
                executor.execute {
                    dao.insert(obj)
                    notifyChange()
                }
            }
        }
        fun <PROPERTY_TYPE> property(baseProperty : KMutableProperty1<REAL_TYPE, PROPERTY_TYPE>) = Property(baseProperty)
    }
    inner class PersonWrapper3(person: Person, val address: AddressWrapper3) : Wrapper3<PersonWrapper3, Person>(person) {
        override val dao = db.personDao
        var id by property(Person::id)
        var name by property(Person::name)
        var age by property(Person::age)
    }
    inner class AddressWrapper3(val address: Address) : Wrapper3<AddressWrapper3, Address>(address) {
        override val dao = db.addressDao
        var id by property(Address::id)
        var ownerId by property(Address::ownerId)
        var street by property(Address::street)
        var city by property(Address::city)
        var state by property(Address::state)
        var zip by property(Address::zip)
    }

//    inner class PersonWrapper2(val person: Person, val address: AddressWrapper2) : BaseObservable() {
//        private fun save() =
//            executor.execute {
//                db.personDao.insert(person)
//                notifyChange()
//            }
//        var id: String
//            get() = person.id
//            set(value) {
//                person.id = value
//                save()
//            }
//        var name: String
//            get() = person.name
//            set(value) {
//                person.name = value
//                save()
//            }
//        var age: Int
//            get() = person.age
//            set(value) {
//                person.age = value
//                save()
//            }
//    }
//    inner class AddressWrapper2(val address : Address) : BaseObservable() {
//        private fun save() =
//            executor.execute {
//                db.addressDao.insert(address)
//                notifyChange()
//            }
//        var id : String
//            get() = address.id
//            set(value) {
//                address.id = value
//                save()
//            }
//        var ownerId: String
//            get() = address.ownerId
//            set(value) {
//                address.ownerId = value
//                save()
//            }
//        var street: String
//            get() = address.street
//            set(value) {
//                address.street = value
//                save()
//            }
//        var city: String
//            get() = address.city
//            set(value) {
//                address.city = value
//                save()
//            }
//        var state: State
//            get() = address.state
//            set(value) {
//                address.state = value
//                save()
//            }
//        var zip: String
//            get() = address.zip
//            set(value) {
//                address.zip = value
//                save()
//            }
//    }
}