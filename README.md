# ShoLi - a simple short lists handler

ShoLi aims at being a very simple tool to edit short lists, and then to be a support for checking on those very lists. Its clean and efficient interface was heavily inspired by the one  [Trolly](http://code.google.com/p/trolly/) once had.

## Features

The fewer, the better.

* An edit mode which prevents entering doubles.
* A simple checking view.
* A layout locked in portrait mode to ease handling when you have a can of peas in the other hand.
* No special permission requirement at all.

However, useful features are also present for power users, like gestures or database export and import. The complete application documentation is provided [in this document](doc/manual.md).

See also the [CHANGES](CHANGES) file for a list of prominent updates with each version.

## Compatibility

Because this application is aimed at being available when doing shopping, it is designed for small devices. It thus provides no optimisation for tablet screens for example.

Also, for the sake of simplicity, ShoLi is written for Android 4.0+. Part of writing this software was the pleasure to discover the latest programmable interfaces, and I did not want to bother with the android compatibility library or external tools like ActionBarSherlock. Feel free to port it onto Android 2.2 or 2.3 if you really want to.

## Installation

ShoLi is free software (see below) and you are welcome to compile and distribute it from [its sources](https://github.com/dsoulayrol/android-sholi). If you prefer binary distributions, you can download it from the following application stores:

* [F-Droid](https://f-droid.org/repository/browse/?fdid=name.soulayrol.rhaa.sholi)
* [Google Play](https://play.google.com/store/apps/details?id=name.soulayrol.rhaa.sholi)

## License

This project is released under the [GPLv3 license](http://www.gnu.org/copyleft/gpl-3.0.html).

The program icon bitmaps were built from a slighly modified version of [List-Icon.svg](http://commons.wikimedia.org/wiki/File:List-Icon.svg), which is released either under GPLv2 or later, or MIT license.

Starting from version 1.4, ShoLi relies on [greenDAO](http://greendao-orm.com/) to handle the database.
