:: This batch script will install & build a Spigot jar, and then place it into a /run/ directory.
:: NOTE: it is highly recommended that you add /spigot/ to your .gitignore file before running this script.

:: This workflow relies on .jar files being built into your /run/plugins/ folder.
:: Gradle users can add this simple copyJar task to their build.gradle file to do this automatically:
::
:: task copyJar(type: Copy) {
::     from jar
::     into 'run/plugins/'
:: }
::
:: build.dependsOn copyJar
::
:: * If you are using shadowJar, change 'from jar' to 'from shadowJar.

@echo off

:: Spigot files are build in the root /spigot/ folder.
:: If the directory does not exist, create it and install Spigot.
IF NOT EXIST "./spigot" (
    mkdir "./spigot"

    :: Spigot is built using BuildTools (https://www.spigotmc.org/wiki/buildtools/).
    :: We download the latest jar and tell it to build Spigot.
    cd spigot
    curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
    java -jar BuildTools.jar --rev 1.16.5
    cd ..
)

:: The output of the BuildTools jar contains .git folders for VSC tracking.
:: To prevent IDEs such as IntelliJ from tracking these repositories, we manually delete all .git folders.
RMDIR "spigot/Spigot/.git" /S /Q
RMDIR "spigot/Bukkit/.git" /S /Q
RMDIR "spigot/CraftBukkit/.git" /S /Q
RMDIR "spigot/BuildData/.git" /S /Q
RMDIR "spigot/Spigot/Spigot-Server/.git" /S /Q
RMDIR "spigot/Spigot/Spigot-API/.git" /S /Q

:: The Spigot jar has been built and is now present inside the /spigot/ folder.
:: We want to run from the /run/ directory, so we first ensure it exists:
IF NOT EXIST "run" (
    mkdir "run"
)

:: Move the Spigot .jar file into the /run/ directory
:: the /d flag ensures the Spigot jar will only be copied if it is newer than an existing file with the same name
xcopy /s /d "spigot/spigot-1.16.5.jar" "run"

:: Auto-accept the eula.txt file
cd run
IF NOT EXIST "eula.txt" (
    echo eula=true >> eula.txt
)

:: Move from /run/ to root
cd ..

:: Tell gradle to build the project. View the top of this file for information on how to make gradle spit artifacts into /run/plugins.
call gradlew build