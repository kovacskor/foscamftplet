package foscamftplet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FoscamFtplet extends DefaultFtplet {
	
	private static Logger log = LoggerFactory.getLogger(FoscamFtplet.class);
	
	public FoscamFtplet() {
		super();		
	}
	
	@Override
    public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {
        String cameraName = session.getUser().getName();
		
		log.info(cameraName + " logged in");
		
		try {
			 //rest call to openhab
			URL url = new URL("http://localhost:8080/rest/items/MotionDetector_" + cameraName + "_Camera");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/plain");
			conn.setRequestProperty("Accept", "application/json");
	
			String input = "ON";
	
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
	
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				log.debug("HTTP return code : "
						+ conn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
	
			String output;
			log.debug("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				log.debug(output);
			}
			
			conn.disconnect();
			
		}catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		  }
		
		log.debug("Disconnect session");
		return FtpletResult.DISCONNECT;
    }
}