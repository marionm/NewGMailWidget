package com.marionm.ngmw;

import android.content.Intent;

public class WidgetHelpers {
  //TODO: How to launch into the account selection menu?
  //      Maybe it happens automatically when multiple accounts have new messages?
  //      If not, try using some reflection to figure out what's available
  private static String GMAIL_PACKAGE = "com.google.android.gm";
  private static String GMAIL_CLASS = GMAIL_PACKAGE + ".ConversationListActivity";
  private static int GMAIL_FLAGS = 0;

  public static Intent getGmailIntent() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setClassName(GMAIL_PACKAGE, GMAIL_CLASS);
    intent.setFlags(GMAIL_FLAGS);
    return intent;
  }
}
