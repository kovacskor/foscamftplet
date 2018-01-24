# foscamftplet
Ftplet used for signalling motion to Openhab2 via apache ftp server for FOSCAM IP cameras. Based on Martin Raepple's code (https://github.com/raepple/foscamFTPlet).
I have removed the dependency for glassfish and adjusted the logging libs to Apache FTP server 1.1.1.

Implementation:

0. Install and configure openhab2
1. Compile FoscamFTPlet using the pom file.
2. Put the FoscamFTPlet-1.0.jar to apache-ftpserver-1.1.1/common/lib
3. Edit your ftpd-typical.xml (rename it to f.e.: ftpd-conf.xml). Add the lines:
	- Into the server tag: xmlns:beans="http://www.springframework.org/schema/beans"
	- Somewhere into the config: 
<ftplets>
	    <ftplet name="foscamftplet">
    		<beans:bean class="foscamftplet.FoscamFtplet">
    		</beans:bean>
	    </ftplet>
</ftplets>

4. Do additional config on FTPD (users, port, etc.)
5. You may want to start Apache ftpd in daemon mode. I didn't find too much documentation over that I did it the following way:
	- Edit bin/ftpd.sh change MAIN_CLASS to org.apache.ftpserver.main.Daemon, and insert a "start" word after it for the startup.
	- Rasbian is now on Stretch version and it uses systemd for init since Jessie, so you need to configure the startup in systemd. If you (like I was) are not familiar with systemd, read a bit about it before progressing.
	- Create an ftpd.service, put/link it to etc/systemd/system. Register the service: systemctl enable ftpd.service.
	- Start ftpd

Your FTP users should be the name of the camera. The login event will trigger a rest call to openhab2 changing the (switch or string motion detection) items to on. The name of the item will be MotionDetector_FTPUSERNAME_Camera.

One sample for my openhab2 items configuration (/etc/openhab2/items/motion_detectors.items):

String MotionDetector_FTPUSERNAME_Camera "Bal kamera Motion detection [%s]" <camera> { expire="30s,state=OFF" }

You may notice the usage of the expire binding above. This sets back the ON state to OFF after 30 secs.
