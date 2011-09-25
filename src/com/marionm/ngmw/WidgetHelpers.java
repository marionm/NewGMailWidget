package com.marionm.ngmw;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

public class WidgetHelpers {
  //TODO: How to launch into the account selection menu?
  //      Maybe it happens automatically when multiple accounts have new messages?
  //      If not, try using some reflection to figure out what's available
  private static String GMAIL_PACKAGE = "com.google.android.gm";
  private static String GMAIL_CLASS = GMAIL_PACKAGE + ".ConversationListActivity";
  private static int GMAIL_FLAGS = 0;

  protected static String PREFS = "com.marionm.ngmw.SHAREDPREFS";

  protected static int ACCOUNT_SLOTS = 4;
  protected static String NO_ACCOUNT = "None";

  protected static Intent getGmailIntent() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setClassName(GMAIL_PACKAGE, GMAIL_CLASS);
    intent.setFlags(GMAIL_FLAGS);
    return intent;
  }

  protected static String getSlotPrefKey(int slot) {
    return "slot" + slot;
  }

  protected static Account[] getGmailAccounts(Context context) {
    AccountManager accountManager = AccountManager.get(context);
    return accountManager.getAccountsByType("com.google");
  }
}
