package com.marionm.ngmw;

import static com.marionm.ngmw.WidgetHelpers.*;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;

//TODO: Probably want to expose the configuration separately as well, not just on 'widget add'
public class Configuration extends Activity {
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

    final SharedPreferences preferences = getSharedPreferences(PREFS, 0);

    //Populate the configuration list with options
    final String[] configurationArray = new String[ACCOUNT_SLOTS];
    for(int i = 0; i < ACCOUNT_SLOTS; i++) {
      configurationArray[i] = getAccountSlotText(i, preferences.getString(getSlotPrefKey(i), NO_ACCOUNT));
    }
    final ArrayAdapter<String> configurationAdapter = new ArrayAdapter<String>(context, R.layout.configuration_list_item, configurationArray);
    configurationList.setAdapter(configurationAdapter);

    //Handle click events
    configurationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final String slotId = getSlotPrefKey(position);
        String slotSelection = preferences.getString(slotId, NO_ACCOUNT);
        int slotSelectionIndex = 0;
        for(int i = 0; i < accountAddresses.length; i++) {
          if(slotSelection.equals(accountAddresses[i])) {
            slotSelectionIndex = i;
            break;
          }
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Tracked account " + (position + 1));
        dialog.setSingleChoiceItems(accountAddresses, slotSelectionIndex, new DialogInterface.OnClickListener() {
          public void onClick(final DialogInterface dialog, int which) {
            String selectedAddress = accountAddresses[which];

            Editor editor = preferences.edit();
            editor.putString(slotId, selectedAddress);
            editor.commit();

            configurationArray[position] = getAccountSlotText(position, selectedAddress);
            configurationAdapter.notifyDataSetChanged();

            //Let this method return before dismissing, so that the radio buttons refresh
            new Handler(new Handler.Callback() {
              public boolean handleMessage(Message msg) {
                dialog.dismiss();
                return true;
              }
            }).sendEmptyMessage(0);
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
    accounts = getGmailAccounts(context);
    if(accounts.length == 0) {
      fail("No Gmail accounts found!");
    }

    accountAddresses = new String[accounts.length + 1];
    accountAddresses[0] = NO_ACCOUNT;
    for(int i = 1; i <= accounts.length; i++) {
      accountAddresses[i] = accounts[i - 1].name;
    }
  }



  private String getAccountSlotText(int slot, String address) {
    String text = "Account " + (slot + 1);
    if(address != null && !address.equals(NO_ACCOUNT)) {
      text += " - " + address;
    }
    return text;
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
