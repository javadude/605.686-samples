package com.javadude.widgetsv2

import android.app.Application
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.Room
import android.content.ComponentName
import java.util.concurrent.Executors


class SampleViewModel(application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newSingleThreadExecutor()
    private val db = Room.databaseBuilder(application, PersonDatabase::class.java, "PEOPLE").build()

    val selectedName = MutableLiveData<String>()
    val people = db.personDao.getAllLD()

    fun addPerson(name : String) {
        executor.execute {
            db.personDao.save(Person().apply { this.name = name })
            updateWidgetList()
        }
    }

    private fun updateWidgetList() {
        // update the widget
        // note that this should go into your Repository class if you have one
        val application = getApplication<Application>()
        val appWidgetManager = AppWidgetManager.getInstance(application)
        val thisAppWidget = ComponentName(application.packageName, PersonAppWidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.people_list)
    }
}