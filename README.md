Leap Motion Projects
====================
In order to use this code you must properly install the leap motion software for your system so that it can be detected by the Java api.

Sign Language
-------------
**Main:** Leap.java

This package was built at a hackathon, and aims to read American sign language through a leap motion, and output it through audio.  This is best used while also viewing the included leap motion visualizer in order to get a good idea of what it is thinking.

**Usage:**
* This was origionaly configured using a right hand, and those values are stored in hands.txt and numberHands.txt
* The program uses statistical analysis to determine the best match between your hand and the values in these files.
* To generate new values to be placed in these config files change the value of SHOW_SERIALIZED_GUESTURE to true.
* All numbers are supported, but not all letters are because of their similarity in American sign language.
* If you would like to see a list of words that you can spell with your currently set letters set the value of SHOW_AVAILABLE_WORDS to true.
* During operation press ENTER at the console to seek a match from hands.txt and to seek a match from numberHands.txt briefly but calmly place your left hand in the field of view.
* Matched characters are put into a String and then that String is printed out.  In order to backspace do a counterclockwize guesture, and in order to clear the string and send it to audio do a clockwize guesture.

**News Article (skip the video ahead to about 0:30):**  http://cw33.com/2015/03/01/hack-city-dallas-hosts-hackathon/

Theremin
--------
**Main:** Theremin.java

This package is an experiment meant to replicate the sound of a theremin.
There are a number of static variables which can be adjusted in order to produce a different sound and functionality.

**Usage:**
* The vertical axis controls pitch.
* The horizontal axis controls volume.
