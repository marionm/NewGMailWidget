package com.marionm.ngmw;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class NewGMailWidget extends AppWidgetProvider {

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);

    for(int widgetId : appWidgetIds) {
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, NewGMailWidgetHelper.getGmailIntent(), 0);
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_gmail_widget);
      views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

      appWidgetManager.updateAppWidget(widgetId, views);
    }
  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
    //TODO: Instead of including icons, try to use context.getPackageManager.getApplicationIcon/Logo
    //      to set the widget's icon to the exact Gmail one at runtime
  }
}