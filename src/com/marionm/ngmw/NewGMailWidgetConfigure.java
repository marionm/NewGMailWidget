package com.marionm.ngmw;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle extras = getIntent().getExtras();
    widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

    setContentView(R.layout.new_gmail_widget_configure);
    
    //Verify the presence of the Gmail app
    if(getPackageManager().resolveActivity(NewGMailWidgetHelper.getGmailIntent(), 0) == null) {
      //FIXME: This may not work, perhaps just a custom view will do instead, check it out
      AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
      alertBuilder.setMessage("Gmail app not found!");
      alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          cancel();
        }
      });
      alertBuilder.show();
    } else {
      Button cancelButton = (Button)findViewById(R.id.config_cancel_btn);
      cancelButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          cancel();
        }
      });
      
      Button okButton = (Button)findViewById(R.id.config_ok_btn);
      okButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          ok();
        }
      });
    }
  }
  
  private Intent result() {
    Intent result = new Intent();
    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
    return result;
  }
  
  private void ok() {
    setResult(RESULT_OK, result());
    finish();
  }
  
  private void cancel() {
    setResult(RESULT_CANCELED, result());
    finish();
  }
}
