# ShoLi - a simple short lists handler

## Introduction

**ShoLi** aims at being a very simple tool to edit short lists, and then to be a support for checking on those very lists. Its clean and efficient interface was initially heavily inspired by the one [Trolly](http://code.google.com/p/trolly/) once had.

This document pretends to be the user manual of **ShoLi**. Users in a hurry may check in the following chapter where the application can be found to be installed, and then should jump to the **Usage** Chapter. Other parts of the document provide information developers may find interesting.

## Installation

**ShoLi** is free software and you are welcome to compile and distribute it from [its sources](https://github.com/dsoulayrol/android-sholi). If you prefer binary distributions, you can download it from the following application stores:

 * [F-Droid](https://f-droid.org/repository/browse/?fdid=name.soulayrol.rhaa.sholi). This is a repository dedicated to free software on **Android** devices. Applications are checked against bad practice, compiled and signed by [them](https://f-droid.org/about/).
 * [Google Play](https://play.google.com/store/apps/details?id=name.soulayrol.rhaa.sholi). The official repository, hosted by **Google**. By default, only applications from this repository are authorised to install on **Android** devices. So, if you don't understand what the previous line are about, simply use this link.

**ShoLi** is written for **Android** 4.0+. It is designed for small devices in portrait mode, so it may look quite terrible or not efficient at all on a pad, and that is normal.

## Usage

### First contact

**ShoLi** maintains a set of items, and those items are used to constitute lists. So a typical usage consists of, first, building or completing the set of grocery items and putting them on the list (typically at home, when you realise that you are out of milk for example) and then opening the list and checking the items when you drop them in your shopping cart. But **ShoLi** is content-agnostic, so you can build lists of whatever you want.

When **ShoLi** is started the first time, you face the *Checking view*, which is quite useless at this moment because your both your list and your set of items are empty. By clicking on the pen icon, you enter the *Edit view*. There, you can use the input field at the top of the screen to enter new items. Note how this field acts as a filter on the view, and that you can't enter the same item name twice. Then, touch one item to put it on the list (it becomes green), or remove it from the list (it becomes grey).

When your list is complete, you can return to the *Checking view* by clicking on the application icon or the **Android** back button. No new item can be entered now, but you can strike the ones you have previously added by touching them. In case of error, they are restored with another touch.

### Actions

The *Checking view* provides some actions that can be useful to speed interaction with the list. Clicking the *Action* entry from the option menu displays a pop-up listing these actions. They are:

 * *Check all*, to check all items in the list.
 * *Uncheck all*, to uncheck every item of the list.
 * *Remove checked items*, to remove all the items currently checked from the list. The items are still available in the set of items to choose from in the *Edit view* though.
 * *Remove all*, to empty the list. Again, all the removed items are still stored by **ShoLi** and can be put on a list again with the *Edit view*.

### Gestures

**ShoLi** is able to recognise simple gestures on the *Checking view* to make list manipulation more efficient if you feel comfortable with them. Gestures detection is triggered with multi-touch, so two fingers to the least are necessary (but toes should be ok too). Currently, **ShoLi** captures swipe movements to the left or to the right, and double-tap.

By default, a swipe to the left does nothing, and a swipe to the right removes checked items from the list. Double-tap always open the contextual menu with the possible actions: it is a shortcut for the *Action* entry in the options menu.

The action triggered on swipe gestures can be configured in the settings (reachable from the option menu). It can be one of the simple actions described in the previous chapter, or one of the more complex following actions.

* *Check or uncheck all* (in this order). If at least one item is still unchecked, then check all unchecked items. Else uncheck every item.
* *Uncheck or check all* (in this order). This is the opposite of the previous one: uncheck all if at least one item is checked, or check all.

### Handling the set of items

**ShoLi** stores all the items provided by the user and never deletes them implicitly. In particular, whatever the way an item is removed from the list in *Checking view*, it is always available in the *Edit view* to be used again.

The (nearly) only way to definitively get rid of items is to long-touch it *Edit view*. This brings up a special interface which allows you to select one or more items (the one you clicked to open this is already selected), and then to delete them by clicking on the icon in the upper right corner.

Actually, it is also possible to destroy all of the items at once using the *Data Overview Activity*, which is detailed in the next chapter. However, this is rarely a good idea if you are a simple user with a long list of memorised items.

### Exporting and importing data

#### The Data Overview activity

**ShoLi** requires no permission, but can rely on other software installed on the phone thanks to the [intents mechanism](http://developer.android.com/training/basics/intents/index.html) of **Android**.

The *Data Overview Activity* (reachable from the option menu) provides an explicit way to empty the whole database, or to export its content. To achieve the latter option, it uses the *SEND* intent, which is commonly supported by mailer or SMS applications, some text editors and others. When you click on the *Export* button, you are presented a list of applications capable of handling the list of items. By choosing one, you may have your items exported on a file on the SD card, or sent by email.

On the opposite way, this activity is also able to receive a *SEND* intent, and thus to import a previously saved set of items. To do this, you must use an application able of loading this list from a file, a mail or whatever and to send it to **ShoLi**. Depending on the import behaviour setting, the status of items which were already present in database is either left untouched (items are then reported as *ignored*), or updated.

As an example, the **920 Text Editor** or the **K-9** mailer are known to work in both use cases.

#### Data Format

An exported set of items is a plain text file. Each line is the name of an item, preceeded by a single character which defines its status.

 * `*`: the item is not listed.
 * `-`: the item is listed.
 * `+`: the item is listed, and checked.

When data is imported, only lines with this format are parsed, so any form of comment can be added in between.

## License

This project is released under the [GPLv3 license](http://www.gnu.org/copyleft/gpl-3.0.html). The README file in source distribution mentions other copyright holders.
