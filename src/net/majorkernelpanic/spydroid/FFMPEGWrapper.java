package net.majorkernelpanic.spydroid;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

public class FFMPEGWrapper {

	String[] libraryAssets = {"ffmpeg"};
	File fileBinDir;
	Context context;

	public FFMPEGWrapper(Context _context) throws FileNotFoundException, IOException {
		context = _context;
		fileBinDir = context.getDir("bin",0);

		if (!new File(fileBinDir,libraryAssets[0]).exists())
		{
			BinaryInstaller bi = new BinaryInstaller(context,fileBinDir);
			bi.installFromRaw();
		}
	}

	
	private void execProcess(String[] cmds) throws Exception {		
		
			ProcessBuilder pb = new ProcessBuilder(cmds);
			pb.redirectErrorStream(true);
	    	Process process = pb.start();      
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	
			String line;
			
			while ((line = reader.readLine()) != null)
			{
				System.out.println(line);
			}

			
		    if (process != null) {
		    	process.destroy();        
		    }

	}
	
	public void processVideo(String url) throws Exception {
				    	
    	String ffmpegBin = new File(fileBinDir,"ffmpeg").getAbsolutePath();
    	
//    	ffmpeg -re -i /usr/local/WowzaMediaServer/content/sample.mp4 -acodec copy -vcodec copy -f rtsp -muxdelay 0.1 rtsp://myuser:mypassword@127.0.0.1:1935/live/myStream.sdp

    	String[] ffmpegCommand = {ffmpegBin, "-v", "10",    			
    			"-i", url,
    			"-f", "rtsp",
    			"-muxdelay", "0.1",
    			"-acodec", "copy",
				"-vcodec", "copy", 
				"rtsp://live:live@192.168.1.9:1935/live/fromphone.sdp"
    	};
    	
    	execProcess(ffmpegCommand);
	}
	

	class FileMover {

		InputStream inputStream;
		File destination;
		
		public FileMover(InputStream _inputStream, File _destination) {
			inputStream = _inputStream;
			destination = _destination;
		}
		
		public void moveIt() throws IOException {
		
			OutputStream destinationOut = new BufferedOutputStream(new FileOutputStream(destination));
				
			int numRead;
			byte[] buf = new byte[1024];
			while ((numRead = inputStream.read(buf) ) >= 0) {
				destinationOut.write(buf, 0, numRead);
			}
			    
			destinationOut.flush();
			destinationOut.close();
		}
	}

}


