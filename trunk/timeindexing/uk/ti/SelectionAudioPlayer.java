/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// -cp=/home/sclayman/java:/home/shared/fromnet/MpegAudioSPI1.9.4/mp3spi1.9.4.jar:/home/shared/fromnet/MpegAudioSPI1.9.4/lib/jl1.0.jar:/home/shared/fromnet/MpegAudioSPI1.9.4/lib/tritonus_share.jar

package uk.ti;

import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.appl.IndexInputStream;
import com.timeindexing.appl.IndexSelectionInputStream;

public class SelectionAudioPlayer
{
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;



	public static void main(String[] args)
	{
		/*
		  We check that there is exactely one command-line
		  argument.
		  If not, we display the usage message and exit.
		*/
		if (args.length != 1) {
			printUsageAndExit();
		}

		/*
		  Now, that we're shure there is an argument, we
		  take it as the filename of the soundfile
		  we want to play.
		*/
		String	strFilename = args[0];
	
		/*
		  We have to read in the sound file.
		*/
		AudioInputStream	audioInputStream = null;
		try {
		    TimeIndexFactory factory = new TimeIndexFactory();

		    Properties properties = new Properties();
		    properties.setProperty("indexpath", strFilename);

		    Index index = null;

		    try {
			index = factory.open(properties);
		    } catch (IndexOpenException ioe) {
			System.err.println("Couldn't open index \"" + strFilename + "\".");
			System.exit(0);
		    }

		    IndexProperties selProps = new IndexProperties();
		    selProps.putProperty("starttime", "2:00");
		    selProps.putProperty("for", "0:15");

		    //InputStream inStream = new BufferedInputStream( new IndexInputStream(index));
		    InputStream inStream = new BufferedInputStream( new IndexSelectionInputStream(index, selProps));


		    //InputStream inStream = new FileInputStream(strFilename);

		    AudioInputStream ins = AudioSystem.getAudioInputStream(inStream);
		    AudioFormat baseFormat = ins.getFormat();
		    AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
								baseFormat.getSampleRate(),
								16,
								2,
								4,
								44100,
								false);
		    System.err.println("Decodedformat=" + decodedFormat);
		    audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, ins);		

		} catch (Exception e) {
			/*
			  In case of an exception, we dump the exception
			  including the stack trace to the console output.
			  Then, we exit the program.
			*/
			e.printStackTrace();
			System.exit(1);
		}

		/*
		  From the AudioInputStream, i.e. from the sound file,
		  we fetch information about the format of the
		  audio data.
		  These information include the sampling frequency,
		  the number of
		  channels and the size of the samples.
		  These information
		  are needed to ask Java Sound for a suitable output line
		  for this audio file.
		*/
		AudioFormat	audioFormat = audioInputStream.getFormat();

		/*
		  Asking for a line is a rather tricky thing.
		  We have to construct an Info object that specifies
		  the desired properties for the line.
		  First, we have to say which kind of line we want. The
		  possibilities are: SourceDataLine (for playback), Clip
		  (for repeated playback)	and TargetDataLine (for
		  recording).
		  Here, we want to do normal playback, so we ask for
		  a SourceDataLine.
		  Then, we have to pass an AudioFormat object, so that
		  the Line knows which format the data passed to it
		  will have.
		  Furthermore, we can give Java Sound a hint about how
		  big the internal buffer for the line should be. This
		  isn't used here, signaling that we
		  don't care about the exact size. Java Sound will use
		  some default value for the buffer size.
		*/
		SourceDataLine	line = null;
		DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);

		System.err.println("DataLine.Info = " + info);

		try {
			line = (SourceDataLine) AudioSystem.getLine(info);

			/*
			  The line is there, but it is not yet ready to
			  receive audio data. We have to open the line.
			*/
			line.open(audioFormat);

		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		/*
		  Still not enough. The line now can receive data,
		  but will not pass them on to the audio output device
		  (which means to your sound card). This has to be
		  activated.
		*/
		line.start();

		/*
		  Ok, finally the line is prepared. Now comes the real
		  job: we have to write data to the line. We do this
		  in a loop. First, we read data from the
		  AudioInputStream to a buffer. Then, we write from
		  this buffer to the Line. This is done until the end
		  of the file is reached, which is detected by a
		  return value of -1 from the read method of the
		  AudioInputStream.
		*/
		int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];

		while (nBytesRead != -1) {
			try {
			    //System.err.println("Len="+ audioInputStream.getFrameLength());
			    //System.err.println("micros=" + line.getMicrosecondPosition() + " pos=" + line.getFramePosition());
			    nBytesRead = audioInputStream.read(abData, 0, abData.length);
			    //System.err.println("Read=" + nBytesRead);
			} catch (IOException e) {
			    e.printStackTrace();
			}

			if (nBytesRead >= 0) {
			    int	nBytesWritten = line.write(abData, 0, nBytesRead);
			}
		}

		/*
		  Wait until all data are played.
		  This is only necessary because of the bug noted below.
		  (If we do not wait, we would interrupt the playback by
		  prematurely closing the line and exiting the VM.)
		 
		  Thanks to Margie Fitch for bringing me on the right
		  path to this solution.
		*/
		line.drain();

		/*
		  All data are played. We can close the shop.
		*/
		line.close();

		/*
		  There is a bug in the jdk1.3/1.4.
		  It prevents correct termination of the VM.
		  So we have to exit ourselves.
		*/
		System.exit(0);
	}


	private static void printUsageAndExit()
	{
		out("SimpleAudioPlayer: usage:");
		out("\tjava SimpleAudioPlayer <soundfile>");
		System.exit(1);
	}


	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}

