# Logical Zoom

This is a simple Minecraft Fabic mod that adds a configurable zoom key.

There are other mods that have this same feature but this is my first mod and I'm using this as an excuse to learn Minecraft modding.

The motivation for this particular mod is that Minecraft 1.15 is out and I personally use Optifine which includes this same zoom feature. At the time of writing,
Optifine is being updated but I would like to have the zoom feature for when I make Minecraft videos.

By default the C key is mapped for zooming (as with Optifine) but you can change this in the Controls settings of Minecraft.

## Installation

1. Download and install Fabric from here: https://fabricmc.net/use
2. Download Fabric API mod from here: https://www.curseforge.com/minecraft/mc-mods/fabric-api and place in your mods folder.
3. Download Logical Zoom from https://www.curseforge.com/minecraft/mc-mods/logical-zoom/files and place in your mods folder.
4. Start Minecraft and go to Options > Controls > Logical Zoom to change the hotkey.

This is a client-side mod and so doesn't require Fabric on the server.

## Updating for new Minecraft version

1. Visit https://modmuss50.me/fabric.html for latest settings
2. Paste settings into gradle.properties and increase mod version
3. Run `.\gradlew vscode`
4. F5 and test the mod is working
5. Run `.\gradlew build`
6. Publish /build/libs/logical_zoom-x.jar
