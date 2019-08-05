package com.javadude.databinding2.example09

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

private val executor = Executors.newSingleThreadExecutor()

class SampleViewModel(application: Application) : AndroidViewModel(application) {

    val db = getApplication<SampleApplication>().db
    val personId = MutableLiveData<String>()
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

    val people = db.personDao.getAll()

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

    fun onItemNameSelected(context : Context, item : Person, position : Int) {
        Toast.makeText(context, "Person name ${item.name} tapped at position $position", Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(context, ItemActivity::class.java).putExtra("id", item.id))
    }
    fun onItemAgeSelected(context : Context, item : Person, position : Int) {
        Toast.makeText(context, "Person age ${item.age} tapped at position $position", Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(context, ItemActivity::class.java).putExtra("id", item.id))
    }
}
