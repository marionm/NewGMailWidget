package com.marionm.ngmw;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NewMailWidget extends AppWidgetProvider {

  //TODO: How to launch into the account selection menu?
  //      Maybe it happens automatically when multiple accounts have new messages?
  //      If not, try using some reflection to figure out what's available
  private static String GMAIL_PACKAGE = "com.google.android.gm";
  private static String GMAIL_CLASS = GMAIL_PACKAGE + ".ConversationListActivity";
  private static int GMAIL_FLAGS = 0;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for(int appWidgetId : appWidgetIds) {
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, getGmailIntent(), 0);
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_gmail_widget);
      views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  @Override
  public void onEnabled(Context context) {
    //TODO: Instead of including icons, try to use context.getPackageManager.getApplicationIcon/Logo
    //      to set the widget's icon to the exact Gmail one at runtime

    if(context.getPackageManager().resolveActivity(getGmailIntent(), 0) == null) {
      //TODO: Find a way to show dialogs from outside an Activity
      Toast.makeText(context, "Gmail app not found, widget will be unable to launch app", 1).show();

      //TODO: Find a way to cancel the addition of the widget (or remove it)
      //      This doesn't work:
      //      new RemoteViews(context.getPackageName(), R.layout.new_gmail_widget).removeAllViews(R.layout.new_gmail_widget);
    } else {
      //TODO: Launch a config activity
      //TODO: Probably want to expose the config separately as well, not just on 'widget add'
    }
  }

  private Intent getGmailIntent() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setClassName(GMAIL_PACKAGE, GMAIL_CLASS);
    intent.setFlags(GMAIL_FLAGS);
    return intent;
  }

}