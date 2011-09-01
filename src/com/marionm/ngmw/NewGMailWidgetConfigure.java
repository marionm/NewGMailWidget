package com.marionm.ngmw;

import static com.marionm.ngmw.NewGMailWidgetHelper.getGmailIntent;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;

//TODO: Probably want to expose the configuration separately as well, not just on 'widget add'
public class NewGMailWidgetConfigure extends Activity {
  private static int NUM_ACCOUNTS = 5;

  private int widgetId;
  private NewGMailWidgetConfigure context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;

    Bundle extras = getIntent().getExtras();
    widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

    setContentView(R.layout.new_gmail_widget_configure);

    if(gmailAppMissing()) return;

    String[] accountListItems = new String[NUM_ACCOUNTS];
    for(int i = 0; i < NUM_ACCOUNTS; i++) {
      accountListItems[i] = "Account " + (i + 1);
    }
    ArrayAdapter<String> accountListItemAdapter = new ArrayAdapter<String>(context, R.id.configuration_list);
    ListView configurationList = (ListView)findViewById(R.id.configuration_list);
    configurationList.setAdapter(accountListItemAdapter);

    configurationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      }
    });

    Button cancelButton = (Button)findViewById(R.id.config_cancel_btn);
    cancelButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        cancel();
      }
    });

    Button okButton = (Button)findViewById(R.id.config_ok_btn);
    okButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        //The onUpdate handler for the widget is not called on creation, must update it manually
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_gmail_widget);
        widgetManager.updateAppWidget(widgetId, views);

        ok();
      }
    });
  }

  private boolean gmailAppMissing() {
    if(getPackageManager().resolveActivity(getGmailIntent(), 0) == null) {
      fail("Gmail app not found!");
      return true;
    } else {
      return false;
    }
  }

  private Account[] getGmailAccounts() {
    AccountManager accountManager = AccountManager.get(context);
    Account[] accounts = accountManager.getAccountsByType("com.google");
    if(accounts.length == 0) {
      fail("No Gmail accounts found!");
    }
    return accounts;
  }

  private void fail(String message) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setMessage(message);
    alert.setNeutralButton("Close", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        cancel();
      }
    });
    alert.show();
  }

  private void ok() {
    setResult(RESULT_OK, result());
    finish();
  }

  private void cancel() {
    setResult(RESULT_CANCELED, result());
    finish();
  }

  private Intent result() {
    Intent result = new Intent();
    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
    return result;
  }
}
