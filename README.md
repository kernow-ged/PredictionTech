## PredictionTech

A magic/mentalism app for secretly inputting and deploying information via one's smartphone. Was first released at the end of 2011. As mentioned [on this early-2012 Magic Cafe thread](https://www.themagiccafe.com/forums/viewtopic.php?topic=453886&forum=273). 

Now open sourced. Excuse the spaghetti code. 

The code for the main app is in the **PWEngine** folder, an Android project that was created in Eclipse. 

Some precompiled APKs and the user manual, in PDF format, are in the **misc** folder.

This is ancient code, but the "wallpaper effect" is still kinda functional on modern phones. Make sure you use the right mode of input (numeric) for the "preset" card images.

The sample URLs in the documentation are no longer online. But as you will see it is the simplest possible task to set up your own web service responding to the secret writing. The number medallions are in the **misc** folder

If you look through the code, and again excuse the spaghetti, you will see an undocumented feature, not in the PDF, that was the most recent addition, albeit some time ago. This allows the user to deploy the information via an Intent. This can be picked up by other apps, a useful example being Tasker. 

Secondly, there is also a companion app, a thumper for a two-person act. This is in the folder **SimpleThumper**, it compiles OK but possibly needs some TLC to get along with modern phones. Likewise this is an Eclipse/Android project. 
