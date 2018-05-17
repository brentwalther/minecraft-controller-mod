-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code (aka
srgnames) - this means that you will not be able to read them directly against
normal code.

Source pack installation information:

## Setting up MinecraftForge

__Step 1:__ Download the latest MDK from [MinecraftForge](https://files.minecraftforge.net/)

__Step 2:__ Extract the ZIP file

__Step 3:__ Open a terminal/shell in the folder you unzipped the files in

__Step 4:__ Setup the MinecraftForge sources:
* Windows: `gradlew setupDecompWorkspace`
* Linux/Mac OS: `./gradlew setupDecompWorkspace`

__Step 5:__ Set up your workspace:
* Eclipse
  1. For eclipse, run "gradlew eclipse" (./gradlew eclipse if you are on Mac/Linux)
* IntelliJ IDEA
  1. Open IDEA, and import project.
  1. Select your build.gradle file and have it import.
  1. Once it's finished you must close IntelliJ and run the following command: `gradlew genIntellijRuns`

__Step 6:__ Finally, open up your IDE. If Eclipse, change your workspace to ./eclipse

If at any point you are missing libraries in your IDE, or you've run into problems you can run "gradlew --refresh-dependencies" to refresh the local cache. "gradlew clean" to reset everything {this does not affect your code} and then start the processs again.

See the Forge Documentation online for more detailed instructions:
http://mcforge.readthedocs.io/en/latest/gettingstarted/

__Step 7:__ Move the sources over into the MDK folder. Run! Enjoy.

## Contributing
If you would like to contribute:
1. Follow the steps above to setup your workspace
1. Build your feature or fix your bug on a different branch than master
1. Create a pull request
1. I'll review your request. Please address comments and squash your commits into a single commit.
1. I'll pull your changes.

