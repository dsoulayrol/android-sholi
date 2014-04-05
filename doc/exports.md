# Exporting and importing data

## The Data Overview activity

ShoLi requires no permission, but can rely on other software installed on the phone thanks to the [intents mechanism](http://developer.android.com/training/basics/intents/index.html) of Android.

The Data Overview activity first provides an explicit way to export the whole database. To achieve this, it uses the SEND intent, which is commonly supported by mailer or SMS applications, some text editors and others. When you click on the *export* button, you are presented a list of applications capable of handling the list of items. By choosing one, you may have your items exported on a file on the SD card, or sent by email.

On the opposite way, this activity is also able to receive a SEND intent, and thus to import a previously saved set of items. To do this, you must use an application able of loading this list from a file, a mail or whatever and to send it to ShoLi. Depending on the import behaviour setting, the status of items which were already present in database is either left untouched (items are then reported as *ignored*), or updated.

As an example, the 920 Text editor or the K-9 mailer are known to work in both use cases.

## Data Format

An exported set of items is a plain text file. Each line is the name of an item, preceeded by a single character which defines its status:

 * '*': the item is not listed.
 * '-': the item is listed.
 * '+': the item is listed, and checked.

When data is imported, only lines with this format are parsed, so any form of comment can be added in between.
