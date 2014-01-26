# ShoLi - a simple *sho*rt *li*sts handler

ShoLi aims at being a very simple tool to edit shopping lists, and then to be a support for checking on those very lists. It was heavily inspired by [Trolly](http://code.google.com/p/trolly/) which proposed a clean and efficient interface to achieve the same goal.

## Features

The fewer, the better.

* An edit mode which prevents entering doubles.
* A simple checking view.
* A layout locked in portrait mode to ease handling when you have a can of peas in the other hand.
* Requires no special permission at all.

See the CHANGES file for a list of prominent updates with each version.

## Usage

ShoLi maintains a list of items, and those items are used to constitute a shopping list. So the usage of this program consists of, first, building or completing the set of known items and putting them on the list (typically at home, when you realise that you are out of milk for example) and then opening the list and checking the items when you drop them in your shopping cart.

When ShoLi is started the first time, you are brought face to the checking mode, which is quite useless at this moment because your list is empty. By clicking on the pen icon, you enter the edit mode. There, you can use the input field at the top of the screen to enter new items. Note how this field acts as filter on the view, and that you can't enter the same item name twice. Then, touch one item to put it on the list (it becomes green), or remove it from the list (it becomes grey).

When your list is complete, you can return to the checking mode by clicking on the application icon. Now, no new item can be entered, but you can strike them by touching them. In case of error, they are restored with another touch. In the option menu are some helpful actions, like checking or unchecking the whole list at once, or clearing it.

The set of known items is maintained by ShoLi so that they can quickly be used on a new list. You can definitively remove one item (or multiple items at once) with a long touch on it in the edit mode. You also can use the data overview mode (from menu) to destroy all of them, which is rarely a good idea if you are a simple user with a long list of memorised items. Lastly, the data overview mode allows you to export the whole set: see [Documentation about exports](doc/exports.md)

## Compatibility

Because this application is aimed at being available when doing shopping, it is designed for small devices. It thus provides no optimisation for tablet screens.

Also, for the sake of simplicity, ShoLi is written for Android 4.0+. Part of writing this software was the pleasure to discover the latest programmable interfaces, and I did not want to bother with the android compatibility library or external tools like ActionBarSherlock. Feel free to port it onto Android 2.2 or 2.3 if you really want to.

## License

This project is released under the [GPlv3 license](http://www.gnu.org/copyleft/gpl-3.0.html).

The program icon bitmaps were built from a slighly modified version of [List-Icon.svg](http://commons.wikimedia.org/wiki/File:List-Icon.svg), which is released either under GPLv2 or later, or MIT license.