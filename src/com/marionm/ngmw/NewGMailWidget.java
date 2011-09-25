package com.marionm.ngmw;

import static com.marionm.ngmw.WidgetHelpers.ACCOUNT_SLOTS;
import static com.marionm.ngmw.WidgetHelpers.NO_ACCOUNT;
import static com.marionm.ngmw.WidgetHelpers.PREFS;
import static com.marionm.ngmw.WidgetHelpers.getGmailAccounts;
import static com.marionm.ngmw.WidgetHelpers.getGmailIntent;
import static com.marionm.ngmw.WidgetHelpers.getSlotPrefKey;
import android.accounts.Account;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class NewGMailWidget extends AppWidgetProvider {

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);

    for(int widgetId : appWidgetIds) {
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, getGmailIntent(), 0);
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_gmail_widget);
      views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

      SharedPreferences preferences = context.getSharedPreferences(PREFS, 0);
      for(int i = 0; i < ACCOUNT_SLOTS; i++) {
        String accountAddress = preferences.getString(getSlotPrefKey(i), NO_ACCOUNT);
        if(NO_ACCOUNT.equals(accountAddress)) continue;

        Account account = null;
        for(Account gmailAccount : getGmailAccounts(context)) {
          if(gmailAccount.name.equals(accountAddress)) {
            account = gmailAccount;
            break;
          }
        }
        if(account != null) {
          updateUnreadCount(appWidgetManager, widgetId, context, account);
        }
      }

      appWidgetManager.updateAppWidget(widgetId, views);
    }
  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
    //TODO: Instead of including icons, try to use context.getPackageManager.getApplicationIcon/Logo
    //      to set the widget's icon to the exact Gmail one at runtime
  }

  private void updateUnreadCount(final AppWidgetManager appWidgetManager, final int widgetId, final Context context, Account account) {
    int unreadCount = getUnreadCount(context, account);
    //TODO: Do stuff
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_gmail_widget);
    appWidgetManager.updateAppWidget(widgetId, views);
  }

  private int getUnreadCount(Context context, Account account) {
    return 0;
  }
}