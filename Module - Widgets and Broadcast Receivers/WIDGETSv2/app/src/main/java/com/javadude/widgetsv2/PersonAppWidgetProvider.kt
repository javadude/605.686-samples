package com.javadude.widgetsv2

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class PersonAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // could have multiple instances of the widget
        for (appWidgetId in appWidgetIds) {
            // create pending intent to go to main activity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            // use RemoteViews to add "onclick" to the text
            val views = RemoteViews(context.packageName, R.layout.appwidget_person)
            views.setOnClickPendingIntent(R.id.people_label, pendingIntent)

            // SET UP THE LIST ADAPTER
            // adapted from https://developer.android.com/guide/topics/appwidgets/index.html

            // create an intent to bind to the service that acts as the remote adapter for the list
            val serviceIntent = Intent(context, PersonListService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            // add the service as a remote adapter
            views.setRemoteAdapter(R.id.people_list, serviceIntent)

            // tell the remote views what to do if the list is empty
            views.setEmptyView(R.id.people_list, R.id.empty_people_list)


            // Set up a template for pending intents for the list items
            // Clicks will get sent to the Main Activity
            val itemClickIntent = Intent(context, MainActivity::class.java)
            itemClickIntent.action = Intent.ACTION_VIEW
            itemClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val itemClickPendingIntent = PendingIntent.getActivity(context, 0, itemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.people_list, itemClickPendingIntent)


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}