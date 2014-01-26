# Exporting and importing data

## The Data Overview activity

ShoLi requires no permission, but can rely on other software installed on the phone thanks to the [intents mechanism](http://developer.android.com/training/basics/intents/index.html).

The Data Overview activity first provides an explicit way to export the whole database. To achieve this, it uses the SEND intent, which is commonly supported by mailer or SMS applications, some text editors and others. When you click on the *export* button, you are presented a list of applications capable of handling the list of item. Choosing one, he may save the exported list of items on the SD card, or send it by mail.

On the opposite way, this activity is also able to receive a SEND intent, and thus to import a previoulsy saved set of items. To do this, you must use an application able of loading this list from a file, a mail or whatever and to send it to ShoLi. The import is done with best effort: items which were already present in database are left untouched (they are reported as *ignored*), others are imported, and their status set accordingly to the input data.

As an example, the 920 Text editor or the K-9 mailer are known to work in both use cases.

## Data Format

An exported set of items is a plain text file. Each line is the name of an item, preceeded by a single character which defines its status:

 * '*': the item is not listed.
 * '-': the item is listed.
 * '+': the item is listed, and checked.

 When data is imported, only lines with this format are parsed, so any form of comment can be added in between.
