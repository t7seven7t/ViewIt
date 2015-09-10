# ViewIt
A resource for displaying information on Bukkit servers. ViewIt is intended to make it easier for Spigot developers to manage scoreboards and other visual elements commonly used in the Minecraft client.

**NOTICE:** ViewIt requires that your server runs **Java 8 or it will not function**. Issues and pull requests regarding Java compatibility will be ignored. You can learn how to install Java 8 for your operating system on [Oracle's website](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html). Alternatively you can use [OpenJDK](http://openjdk.java.net/install/). You can even install it in another directory so that you have no down time - no excuses :)

## Usage
ViewIt can be used like any other Spigot plugin by adding it to the plugins directory. Server owners will find that the options provided by the plugin are relatively limited as it is designed to be used by developers to enhance interoperability between scoreboard plugins.

Developers should note that ViewIt is licensed under the Apache License v2 and that they can copy the source from this plugin as long as they retain the original license for the copied code and specify any changes they made. 

## Documentation
Most of the interfaces are already documented with some classes receiving documentation too. More information on how to use the API can be found on the [wiki](https://github.com/t7seven7t/ViewIt/wiki).

## Downloading
You can find the latest release of ViewIt along with a list of changes on the [releases page](https://github.com/t7seven7t/ViewIt/releases). To install just place the jar into your plugins folder.

## Compiling
Use Gradle to compile ViewIt

ViewIt has a dependency that you must download first. Create a new directory **/libs** and put the PlaceholderAPI.jar into it. You can download PlaceholderAPI at https://www.spigotmc.org/resources/placeholderapi.6245/

If you are on Linux or Mac OS X, run the following in your terminal:

    ./gradlew clean build

If you are on Windows, run the following in your command prompt:

    gradlew clean build
