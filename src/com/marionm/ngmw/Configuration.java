package com.marionm.ngmw;

import static com.marionm.ngmw.WidgetHelpers.getGmailIntent;

import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;

//TODO: Probably want to expose the configuration separately as well, not just on 'widget add'
public class Configuration extends Activity {
  private static int NUM_ACCOUNTS = 5;
  private static String PREFS = "com.marionm.ngmw.SHAREDPREFS";

  private int widgetId;
  private Configuration context;

  private Account[] accounts;
  private String[] accountAddresses;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;

    Bundle extras = getIntent().getExtras();
    widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      cancel();
      return;
    }

    setContentView(R.layout.configuration);

    if(gmailAppMissing()) return;
    populateGmailAccounts();

    ListView configurationList = (ListView)findViewById(R.id.config_list);
    populateConfigurationList(configurationList);

    configurationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS, 0);
        Map<String, ?> preferences = sharedPreferences.getAll();

        final String slotId = "slot" + position;
        String slotSelection = (String)preferences.get(slotId);
        int slotSelectionIndex = 0;
        if(slotSelection != null) {
          for(int i = 0; i < accountAddresses.length; i++) {
            if(slotSelection.equals(accountAddresses[i])) {
              slotSelectionIndex = i;
              break;
            }
          }
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Tracked account " + position);
        dialog.setSingleChoiceItems(accountAddresses, slotSelectionIndex, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            //TODO: Should actually select the radio button visually on click
            //TODO: Should update the config list to show the selected address
            Editor editor = sharedPreferences.edit();
            editor.putString(slotId, accountAddresses[which]);
            editor.commit();
            dialog.dismiss();
          }
        });
        dialog.show();
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

  private void populateGmailAccounts() {
    AccountManager accountManager = AccountManager.get(context);
    accounts = accountManager.getAccountsByType("com.google");
    if(accounts.length == 0) {
      fail("No Gmail accounts found!");
    }

    accountAddresses = new String[accounts.length + 1];
    accountAddresses[0] = "None";
    for(int i = 1; i <= accounts.length; i++) {
      accountAddresses[i] = accounts[i - 1].name;
    }
  }

  private void populateConfigurationList(ListView configurationList) {
    String[] accountListArray = new String[NUM_ACCOUNTS];
    for(int i = 0; i < NUM_ACCOUNTS; i++) {
      accountListArray[i] = "Account " + (i + 1);
    }
    ArrayAdapter<String> accountListAdapter = new ArrayAdapter<String>(context, R.layout.configuration_list_item, accountListArray);
    configurationList.setAdapter(accountListAdapter);
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
