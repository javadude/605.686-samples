package com.javadude.widgetsv2

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class PersonListService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = PersonRemoveViewsFactory(this)
}

class PersonRemoveViewsFactory(private val context : Context) : RemoteViewsService.RemoteViewsFactory {
    private lateinit var db : PersonDatabase
    private var people : List<Person>? = null

    override fun onCreate() {
        db = Room.databaseBuilder(context, PersonDatabase::class.java, "PEOPLE").build()
    }

    override fun getCount() = people?.size ?: 0
    override fun hasStableIds() = true
    override fun getViewTypeCount() = 1
    override fun getItemId(position: Int) = position.toLong()
    override fun getLoadingView() = null // use default loading view

    override fun onDataSetChanged() {
        people = db.personDao.getAll()
    }

    override fun getViewAt(position: Int): RemoteViews {
        Thread.sleep(200)
        // create a remote instance of the row, set the person name and return
        val remoteViews = RemoteViews(context.packageName, R.layout.person_item)
        remoteViews.setTextViewText(R.id.person_item, people!![position].name)


        // create a "fill intent", adding item data for when this item is clicked to the
        //   onItemClickIntent created in the PersonAppWidgetProvider
        val fillInIntent = Intent()
                .putExtra(MainActivity.EXTRA_ITEM_ID, people!![position].id)
                .putExtra(MainActivity.EXTRA_ITEM_NAME, people!![position].name)
        remoteViews.setOnClickFillInIntent(R.id.person_item, fillInIntent)

        return remoteViews
    }

    override fun onDestroy() {
        db.close()
    }
}