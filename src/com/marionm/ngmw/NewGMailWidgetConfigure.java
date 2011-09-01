package com.marionm.ngmw;

import static com.marionm.ngmw.NewGMailWidgetHelper.getGmailIntent;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//TODO: Probably want to expose the configuration separately as well, not just on 'widget add'
public class NewGMailWidgetConfigure extends Activity {
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
