package com.javadude.databinding2.example08

import android.app.Application
import androidx.lifecycle.*
import java.util.concurrent.Executors
import kotlin.reflect.KMutableProperty


class SampleViewModel(application: Application) : AndroidViewModel(application) {
    val db = getApplication<SampleApplication>().db
    val personId = MutableLiveData<String>()
    val person = PersonWrapper(switch(personId, {db.personDao.getById(it)}))
    val people = db.personDao.getAll()

    open inner class Wrapper<S :Any>(private val sourceLD : LiveData<S?>,
                                     private val dao : IDao<S>) {
        fun <T> property(propertyRef : KMutableProperty<T>, defaultValue : T? = null) =
            AutoSaveTwoWayMediatorLiveData(sourceLD, propertyRef, dao, defaultValue)
    }

    inner class PersonWrapper(personLD : LiveData<Person?>) : Wrapper<Person>(personLD, db.personDao) {
        val name = property(Person::name)
        val age = property(Person::age, 0)
        val address : AddressWrapper by lazy {
            AddressWrapper(switch(personLD, { db.addressDao.getForOwnerId(it.id) }))
        }
        override fun toString(): String {
            return "PersonWrapper(name=${name.value}, age=${age.value}, address=$address)"
        }
    }

    inner class AddressWrapper(addressLD : LiveData<Address?>) : Wrapper<Address>(addressLD, db.addressDao) {
        val street = property(Address::street)
        val city = property(Address::city)
        val state = property(Address::state)
        val zip = property(Address::zip)
        override fun toString(): String {
            return "AddressWrapper(street=${street.value}, city=${city.value}, state=${state.value}, zip=${zip.value})"
        }
    }
}



fun <T, S> switch(source : LiveData<S?>,
                  get: (S) -> LiveData<T>) : LiveData<T?> =
    Transformations.switchMap(source, {
        if (it == null) {
            null
        } else {
            get(it)
        }
    })

class AutoSaveTwoWayMediatorLiveData<S: Any, T>(
        private val source: LiveData<S?>,
        private val property : KMutableProperty<T>,
        private val dao : IDao<S>,
        private val defaultValue : T? = null
        ) : MediatorLiveData<T?>() {

    companion object {
        private val executor = Executors.newSingleThreadExecutor()
    }
    init {
        addSource(source, {
            val newValue : T? = if (it == null) {
                null
            } else {
                property.getter.call(it)
            }
            if (value != newValue) {
                value = newValue
            }
        })
    }

    override fun setValue(newValue: T?) {
        if (value != newValue) {
            source.value?.let { sourceValue ->
                val toSet = newValue ?: defaultValue
                super.setValue(toSet)
                property.setter.call(sourceValue, toSet)
                executor.execute {
                    dao.insert(sourceValue)
                }
            }
        }
    }
}