package sinewave;


import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
//
//  SineContinuousjava (a part of PitchLab)
//  (Version 0.6)
//
//  Created by Gavin Shriver on 4/17/09.
//  This is a class written as part of PitchLab for a research project involving
//  human pitch perception.  This class can generate a continuous sine wave at 
//  a specified frequency. The frequency can be changed on the fly as nessasary.
//

public class SineContinuous implements Runnable
{
	public static int sampleRate = 48000; // 48 kbit/s sampling rate
	public static int sampleSizeInBits = 16; // bits
	public int sampleSizeInBytes;
	public static int lineBufferSize = 5000; // bytes
	
	// --- Math Constructs
	public double frequency = 400.0;
	private static boolean useDynAmp  = false;
	private static double amplitude = 12.0;
	private double RAD = 2.0 * Math.PI;
	
	private byte[] buffer;
	public boolean playing = false;		
	
	private final AudioFormat AF = new AudioFormat(sampleRate, sampleSizeInBits, 1, true, true);
	private final DataLine.Info INFO = new DataLine.Info(SourceDataLine.class, AF);
	
	
	public SourceDataLine line;
	
	private Thread player = null;
	
	//******************************************************************************
	// END VARIABLES
	// BEGIN CONSTRUCTORS
	//******************************************************************************
	public SineContinuous()
	{
		setSampleSizeInBytes();
		buffer = new byte[getSampleSizeInBytes()];
		
		try
		{
			line = (SourceDataLine)AudioSystem.getLine(INFO);
			line.open(AF, lineBufferSize);	
		}
		catch (Exception e) 
		{
			System.out.println(e);
		}
	}
	
	//******************************************************************************
	// END CONSTRUCTORS
	// BEGIN SOUND METHODS
	//******************************************************************************
	
	public void play(double frequency)
	{
		setFrequency(frequency);
		playing = true;
		player = new Thread(this,"Player");
		player.start();	
	}
	
	public void play()
	{
		playing = true;
		player = new Thread(this,"Player");
		player.start();			
	}
	
	public void run()
	{
		try 
		{
			line.start();	//begins the stream... starts playing it
			while (playing) 
			{
				for (int i=0; i <= (int)(((double)sampleRate/frequency)); i++)
				{
					int wave = calculateWaveSample(i);
					byte msb = (byte)(wave >>> 8);
					byte lsb = (byte) wave;
			        buffer[0] = msb;
			        buffer[1] = lsb;
			        line.write( buffer, 0, buffer.length );
			        
					if(!playing)
						break;
				}
				
				if (!playing)
					break;
			}//end while
		}//end try
		catch (Exception e)
		{
			e.printStackTrace();
		}//end catch
		line.stop();
		line.flush();
	}//end run

	public void stop()
	{
		setPlaying(false);
	}
	
	public void end()
	{
		line.close();
	}
	
	private int calculateWaveSample(int i)
	{
		if (useDynAmp )
			return (int)((sineAmplitude(Math.log(frequency))/100.0)*(32767.0 * Math.sin((i*RAD* frequency) / sampleRate)));
		else
			return (int)((amplitude/100.0)*(32767.0 * Math.sin((i*RAD* frequency) / sampleRate)));
	}
		
	//******************************************************************************
	// END SOUND METHODS
	// BEGIN GET/SET METHODS
	//******************************************************************************
	
	public synchronized void setFrequency(double frequency)
	{
		this.frequency = frequency;
	}
	public synchronized double getFrequency()
	{
		return this.frequency;
	}
	
	public synchronized void setPlaying(boolean playing)
	{
		this.playing = playing;
	}
	
	public boolean getPlaying()
	{
		return this.playing;
	}
	public boolean getPlaying(double frequency)
	{
		if(!getPlaying())
			return false;
		else if (frequency == getFrequency())
			return true;
		else 
			return false;
	}
	
	//------------------------------------------------------
	// --- ADVANCED VARIABLES
	//------------------------------------------------------ 
	public synchronized static void setSampleRate(int sampleRate)
	{
		SineContinuous.sampleRate = sampleRate;
	}
	public synchronized static int getSampleRate()
	{
		return SineContinuous.sampleRate;
	}
	
	public synchronized static void setSampleSizeInBits(int sampleSizeInBits)
	{
		SineContinuous.sampleSizeInBits = sampleSizeInBits;
	}
	public synchronized static int getSampleSizeInBits()
	{
		return SineContinuous.sampleSizeInBits; 
	}
	
	public synchronized static void setLineBufferSize(int lineBufferSize)
	{
		SineContinuous.lineBufferSize = lineBufferSize;
	}
	public synchronized static int getLineBufferSize()
	{
		return SineContinuous.lineBufferSize;
	}
	
	public synchronized static void setAmplitude(double amplitude)
	{
		SineContinuous.amplitude = amplitude;
		System.out.println("amp changed to:" + amplitude);
	}
	public synchronized static double getAmplitude()
	{
		return SineContinuous.amplitude;
	}
	
	public synchronized static void setUseDynAmp(boolean useDynAmp )
	{
		SineContinuous.useDynAmp  = useDynAmp ;
	}
	public synchronized static boolean getUseDynAmp()
	{
		return SineContinuous.useDynAmp ;
	}
	
	private int getSampleSizeInBytes()
	{
		return this.sampleSizeInBytes;
	}
	
	private void setSampleSizeInBytes()
	{
		this.sampleSizeInBytes =  (int)(getSampleSizeInBits()/8);
		
	}
	
	//------------------------------------------------------
	// --- DEFAULTS
	//------------------------------------------------------ 
	public static void setDefaults()
	{
		SineContinuous.setLineBufferSize(5000);
		SineContinuous.setSampleRate(48000);
		SineContinuous.setSampleSizeInBits(16);
		SineContinuous.setUseDynAmp(true);
		SineContinuous.setAmplitude(85.0);
		
	}


	//******************************************************************************
	// START (odd) amplitude shift
	// Amplitude shift was an attempt to normalize the perceived amplitude of a note
	// it was a recomendation from a friend however whether or not it works
	// is still a mystery!
	//******************************************************************************


	private double sineAmplitude(double freqNlog)
	{
		double	amp, spl;
		
		spl=0.0;
		/*	
		 if (freqNlog<=5.05) {
		 spl=50.0+(freqNlog-4.1)*(50.0-43.3)/(4.1-5.05);
		 }
		 else if (freqNlog<=6.0) {
		 spl=43.3+(freqNlog-5.05)*(43.3-36.0)/(5.05-6.0);
		 }
		 else if (freqNlog<=6.5) {
		 spl=36.0;
		 }
		 else if (freqNlog<=7.75) {
		 spl=40.0-4/pow((7.75-7.125),2)*pow((freqNlog-7.125),2);
		 }
		 else if (freqNlog<=8.6) {
		 spl=32.5+3.5/pow((8.6-8.175),2)*pow((freqNlog-8.175),2);
		 }
		 */
		
		if (freqNlog<=4.1) 
		{
			spl=Math.pow(freqNlog,2)*(-285)+1187*(freqNlog);
		}
		if (freqNlog<=4.8) 
		{
			spl=83.0+(freqNlog-4.1)*(83.0-80.0)/(4.1-4.8);
		}
		else if (freqNlog<=5.5) 
		{
			spl=80.0+(freqNlog-4.8)*(80.0-72.0)/(4.8-5.5);
		}
		else if (freqNlog<=6.2) 
		{
			spl=72.0+(freqNlog-5.5)*(72.0-68.0)/(5.5-6.2);
		}
		else if (freqNlog<=6.9) 
		{
			spl=68.0+(freqNlog-6.2)*(68.0-70.0)/(6.2-6.9);
		}
		else if (freqNlog<=7.6) 
		{
			spl=70.0+(freqNlog-6.9)*(70.0-65.0)/(6.9-7.6);
		}
		else /*if (freqNlog<=9.3)*/ 
		{
			spl=65.0+(freqNlog-7.6)*(65.0-62.5)/(7.6-8.3);
		}
		
		amp=(.75*Math.pow(Math.log(20*spl)/Math.log(10),.5)/Math.pow(Math.log(20*83.0)/Math.log(10),.5))*amplitude;
		
		return (amp);
	}
	
	
	
	//******************************************************************************
	// END amplitude shift
	// MAIN (for testing purposes)
	//******************************************************************************

	public static void main(String[] args) 
	{
		
		Scanner chooser = new Scanner(System.in);
		
		double choice=1;
		
		try 
		{
			SineContinuous test = new SineContinuous();
			test.play();
			
			while(choice > 0)
			{
				System.out.print("Frequency: ");
				choice = chooser.nextDouble();
				if (choice > 0)
					test.setFrequency(choice);
			}
			test.stop();
			test.end();
			
		} 		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		System.exit(0);
		
	}
	
	
	
}
