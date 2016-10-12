# Java test application

## Running the application

### Terminal/commandline
```
gradle run
```

*Enabling logging*
```
gradle -DloggingEnabled=true run
```

### Android Studio

Right click on Main.java and select 
```
Run 'Main.main()'
```

*Enabling logging*

Add the following line to, Run -> Edit Configurations -> VM Options
```
-DloggingEnabled=true
```

Next, run the application.
