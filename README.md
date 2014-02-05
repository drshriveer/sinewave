sinewave
========

A discrete and continuous audio sine wave generation library for Java.

This library was original written as part of PitchLab in 2009; a continuation of research into [human pitch perception](http://www.phys.washington.edu/~vladi/pitch/JASA_94/abs_pitch.html), written by ME! The project migrated an application written in the early 1990s to cross platform and modern Java. Unfortunately because this research is on-going I am unable to release the entire source of the project. However this library is fine. Also please note these files have not been used or modified prior to this release (other than a few more comments), so some of the methods are purely experimental and possibly broken.

##### NOTE: older windows machines sometimes have issues generating continuous tones. 
This is likely due to lack of buffer depth which would delay the response time when changing the tone on the fly. This responsiveness is crucial to the research.

Usage
-------------
Just like any other library, this may be included and used in your Java program. Both programs have main methods which can also be executed from the command-line to test its operation. The command-line interfaces are barren, but useful.


##### Sine.java (Discrete Sine Wave Generator)
The discrete wave generator has methods to generate any number of tones at random duration and frequency. It can also generate any specified tone (specified in Hz) for any discrete duration.

The command-line (testing) interface is a little funky for this. You will be prompted to `Enter a random number of tones:`  the number of tones selected will be played after selecting the next number of tones to play. (I don't know why this is.) Additionally typing `-3` will bring up a failed experiment (tone blending), and typing `0` will bring up a new interface. The new interface will ask you for a tone and a duration of time to play that tone.

##### SineContinuous.java (Continuous Sine Wave Generator)
This will generate a pure sine wave for a particular frequency until told to stop. The frequency can be changed on the fly.

The command-line (testing) interface is very straight forward. Simply type in the frequency you want to hear and it will play.... endlessly. Until you type the next frequency you wish to hear.  

##### To Compile and Run:
To compile and run the program navigate to the directory above `sinewave` in a terminal and type:

    $ javac sinewave/*.java             # to compile
    $ java sinewave/Sine                # to run the discrete sine wave
    $ java sinewave/SineContinuous      # to run the continuous sine wave


Questions & Comments
-------------
If you have any questions or comments about the code don’t hesitate to contact me at [gavin.shriver@gmail.com](mailto:gavin.shriver@gmail.com). Please enjoy this library! It's a great learning experience to play with it.

License
-------------
© Gavin Shriver. CC BY-SA.   
( You can find the full license [here](http://creativecommons.org/licenses/by-sa/4.0/legalcode) )
