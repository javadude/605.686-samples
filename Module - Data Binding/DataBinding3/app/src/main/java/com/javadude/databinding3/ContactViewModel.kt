package com.javadude.databinding3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    // the database
    private var db = Room.databaseBuilder(application, Database::class.java, "PEOPLE")
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL("INSERT INTO Address (id, ownerId, type, street, city, state, zip) VALUES('a11', 'p1', 'Home', '123 Sesame Street', 'Laurel', 'MD', '20723')")
                db.execSQL("INSERT INTO Address (id, ownerId, type, street, city, state, zip) VALUES('a12', 'p1', 'Work', '11100 Johns Hopkins Road', 'Laurel', 'MD', '20723')")
                db.execSQL("INSERT INTO Address (id, ownerId, type, street, city, state, zip) VALUES('a21', 'p2', 'Home', '11 Animated Lane', 'New York', 'NY', '10011')")
                db.execSQL("INSERT INTO Person (id, name, age) VALUES('p1', 'Scott', 52)")
                db.execSQL("INSERT INTO Person (id, name, age) VALUES('p2', 'Alex', 26)")
            }
        })
        .build()

    // All people to display in the list
    val people = db.dao.getContacts()

    // the currently-selected person (to edit)
    val selectedPerson = MutableLiveData<Person>()

    // the index of the selected person's selected address when editing
    // we need the selected index because that's how we'll communicate with the Spinner
    val selectedAddressIndex = MutableLiveData<Int>()

    // all addresses for the selected person
    val addresses = Transformations.switchMap(selectedPerson) {
        db.dao.getAddressesForOwner(it.id)
    }

    // an internal live data used to determine which address is the one at the selected index
    private val selectedAddress = Transformations.map(selectedAddressIndex) { index ->
        if (index >= 0) {
            addresses.value?.let {
                it[index]
            }
        } else {
            null
        }
    }

    // an "output" live data that wraps the selected person so any property changes will update
    //   the person in the database
    val personWrapper = Transformations.map(selectedPerson) {
        it?.let { person -> PersonWrapper(person) }
    }

    // an "output" live data that wraps the selected address so that any property changes will
    //   update the address in the database
    val selectedAddressWrapper = Transformations.map(selectedAddress) {
        it?.let { address -> AddressWrapper(address) }
    }

    // an executor to handle database updates in a background thread
    private val executor = Executors.newSingleThreadExecutor()

    // functions to perform the updates using the executor
    private fun save(person: Person) = executor.execute {
        db.dao.update(person)
    }
    private fun save(address: Address) = executor.execute {
        db.dao.update(address)
    }

    /**
     * A Decorator class for a Person that updates the person data in the database whenever
     * one of its properties change.
     *
     * Note that we could set up kotlin property delegation to eliminate the obvious duplication,
     * but that always adds more confusion to this example. Once you get very comfortable with
     * Kotlin, I recommend taking a look at property delegation. It's awesome. You can reduce this
     * down to something like
     *
     *    inner class PersonWrapper(realPerson : Person) : Wrapper<PersonWrapper, Person>(realPerson) {
     *        var name by Property(Person::name)
     *        var age by Property(Person::age)
     *    }
     */
    inner class PersonWrapper(private val realPerson : Person) {
        var name : String
            get() = realPerson.name
            set(value) {
                if (realPerson.name != value)  {
                    realPerson.name = value
                    save(realPerson)
                }
            }
        var age : Int
            get() = realPerson.age
            set(value) {
                if (realPerson.age != value) {
                    realPerson.age = value
                    save(realPerson)
                }
            }
    }

    /**
     * A Decorator class for an Address that updates the address data in the database whenever
     * one of its properties change.
     *
     * Note that we could set up kotlin property delegation to eliminate the obvious duplication,
     * but that always adds more confusion to this example. Once you get very comfortable with
     * Kotlin, I recommend taking a look at property delegation. It's awesome. You can reduce this
     * class down to something like
     *
     *     inner class AddressWrapper(realAddress : Address): Wrapper<AddressWrapper, Address>(realAddress) {
     *         var street by Property(Address::street)
     *         var city by Property(Address::city)
     *         var state by Property(Address::state)
     *         var zip by Property(Address::zip)
     *     }
     */
    inner class AddressWrapper(private val realAddress : Address) {
        var street : String
            get() = realAddress.street
            set(value) {
                if (realAddress.street != value) {
                    realAddress.street = value
                    save(realAddress)
                }
            }
        var city : String
            get() = realAddress.city
            set(value) {
                if (realAddress.city != value) {
                    realAddress.city = value
                    save(realAddress)
                }
            }
        var state : State
            get() = realAddress.state
            set(value) {
                if (realAddress.state != value) {
                    realAddress.state = value
                    save(realAddress)
                }
            }
        var zip : String
            get() = realAddress.zip
            set(value) {
                if (realAddress.zip != value) {
                    realAddress.zip = value
                    save(realAddress)
                }
            }
    }
}