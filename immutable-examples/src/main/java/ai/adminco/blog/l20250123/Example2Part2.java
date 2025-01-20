package ai.adminco.blog.l20250123;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.util.stream.Stream;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.Driver;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

// Create an Object model that allows for the following:
// 1. Use the Jsch Library to establish a list of tunnels through an SSH connection
// 2. Also allow an SFTP to transfer files to the server and copy files down from the server
// 3. The user of the classes could authenticate using basic (user/password) authentication or private key
// 4. Should implement Closeable

// Part 2
// User Environment variables to determine login credentials
// Remember what ports have been tunneled
// Upload file to sftp
// Change directory on the sftp connection
// Get the current directory
public class Example2Part2 {

   public static class MutableAnswer {
	  public static void main(String[] args) throws Exception {
		 final String user = "developer";
		 final String host = "query";
		 final int port = 22;
		 final String keyPath = "/home/developer/.ssh/id_rsa";

		 try (final Ssh ssh = new Ssh();) {
			ssh.setHost( host );
			ssh.setPort( port );
			ssh.setUser( user );
			ssh.setPrivateKeyPath( keyPath );

			ssh.connect();

			ssh.portForward( "localhost", 5432, "db", 5432 );
			testConnection();

			ssh.ls().forEach( System.out::println );

			final String content = ssh.downloadAsString( ".pam_environment" );
			System.out.println( "------ File Content ------" );
			System.out.println( content );
			System.out.println( "------ End File ----------" );
		 }
	  }

	  private static void testConnection() throws Exception {
		 // 1a. Test Connection
		 final String dbHost = "localhost";
		 final int dbPort = 5432;
		 final String name = "yaas";
		 final String dbUser = "super";
		 final String dbPass = "postgres";
		 final BasicDataSource ds = new BasicDataSource();
		 ds.setDriverClassName( Driver.class.getName() );
		 ds.setUrl( String.format( "jdbc:postgresql://%s:%s/%s?stringtype=unspecified",
			   dbHost, dbPort, name ) );
		 ds.setUsername( dbUser );
		 ds.setPassword( dbPass );
		 ds.setMaxTotal( -1 );

		 try (
			   final Connection conn = ds.getConnection();
			   final Statement stmt = conn.createStatement();
			   final ResultSet resultSet = stmt.executeQuery( "SELECT 1" );) {

			if (resultSet.next()) {
			   System.out.println( "Test query result: " + resultSet.getInt( 1 ) );
			}
		 } finally {
			ds.close();
		 }

	  }

	  public static class Ssh implements AutoCloseable {
		 private JSch jsch;
		 private Session session;
		 private String user;
		 private String password;
		 private String privateKeyPath;
		 private ChannelSftp sftpChannel;
		 private String host = "localhost";
		 private int port = 22;

		 public void connect() {
			jsch = new JSch();
			try {
			   if (StringUtils.isNotBlank( privateKeyPath )) {
				  jsch.addIdentity( privateKeyPath );
			   }
			   session = jsch.getSession( user, host, port );
			   if (StringUtils.isNotBlank( password )) {
				  session.setPassword( password );
			   }

			   session.setConfig( "StrictHostKeyChecking", "no" );
			   session.connect();

			   sftpChannel = (ChannelSftp) session.openChannel( "sftp" );
			   sftpChannel.connect();

			} catch (Throwable exception) {
			   throw new RuntimeException( String.format(
					 "Failed to connect to: %s", host ), exception );
			}
		 }

		 public void portForward(String host, int port, String remoteHost, int remotePort) {
			try {
			   session.setPortForwardingL( host, port, remoteHost, remotePort );
			} catch (Throwable exception) {
			   throw new RuntimeException( String.format(
					 "Failed to forward %s:%s to %s:%s", remoteHost,
					 remotePort, host, port ), exception );
			}
		 }

		 public String getUser() {
			return user;
		 }

		 public Stream<ChannelSftp.LsEntry> ls() {
			try {
			   final Vector<ChannelSftp.LsEntry> entries = sftpChannel.ls( "." );
			   return entries.stream();
			} catch (Throwable exception) {
			   throw new RuntimeException( String.format( "Failed to list files" ), exception );
			}
		 }

		 public void setUser(String user) {
			this.user = user;
		 }

		 public String downloadAsString(String path) {
			try {
			   final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			   sftpChannel.get( path, baos );
			   final String content = new String( baos.toByteArray(), StandardCharsets.UTF_8 );
			   return content;
			} catch (Throwable exception) {
			   throw new RuntimeException( String.format(
					 "Failed to download: %s", path ), exception );
			}
		 }

		 public String getPassword() {
			return password;
		 }

		 public void setPassword(String password) {
			this.password = password;
		 }

		 public String getPrivateKeyPath() {
			return privateKeyPath;
		 }

		 public void setPrivateKeyPath(String privateKeyPath) {
			this.privateKeyPath = privateKeyPath;
		 }

		 public String getHost() {
			return host;
		 }

		 public void setHost(String host) {
			this.host = host;
		 }

		 public int getPort() {
			return port;
		 }

		 public void setPort(int port) {
			this.port = port;
		 }

		 @Override
		 public void close() throws Exception {
			if (sftpChannel != null) {
			   sftpChannel.disconnect();
			}
			if (session != null) {
			   session.disconnect();
			}
		 }

	  }
   }

   public static class JschExample {

	  public static void main(String[] args) throws Exception {
		 final String user = "developer";
		 final String host = "query";
		 final int port = 22;
		 final String keyPath = "/home/developer/.ssh/id_rsa";

		 // Open SSH Session
		 final JSch jsch = new JSch();
		 jsch.addIdentity( keyPath );
		 final com.jcraft.jsch.Session session = jsch.getSession( user, host, port );
		 try {
			session.setConfig( "StrictHostKeyChecking", "no" );
			session.connect();

			// 1. Create Tunnel (Server "db", postgresql port
			session.setPortForwardingL( "localhost", 5432, "db", 5432 );

			// 1a. Test Connection
			final String dbHost = "localhost";
			final int dbPort = 5432;
			final String name = "yaas";
			final String dbUser = "super";
			final String dbPass = "postgres";
			final BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName( Driver.class.getName() );
			ds.setUrl( String.format( "jdbc:postgresql://%s:%s/%s?stringtype=unspecified",
				  dbHost, dbPort, name ) );
			ds.setUsername( dbUser );
			ds.setPassword( dbPass );
			ds.setMaxTotal( -1 );

			try (
				  final Connection conn = ds.getConnection();
				  final Statement stmt = conn.createStatement();
				  final ResultSet resultSet = stmt.executeQuery( "SELECT 1" );) {

			   if (resultSet.next()) {
				  System.out.println( "Test query result: " + resultSet.getInt( 1 ) );
			   }
			} finally {
			   ds.close();
			}

			// 2. SFTP
			final ChannelSftp channel = (ChannelSftp) session.openChannel( "sftp" );
			channel.connect();
			try {

			   // List Files
			   final Vector<ChannelSftp.LsEntry> entries = channel.ls( "." );
			   entries.forEach( System.out::println );

			   // Download File
			   final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			   channel.get( ".pam_environment", baos );
			   final String content = new String( baos.toByteArray(), StandardCharsets.UTF_8 );
			   System.out.println( "------ File Content ------" );
			   System.out.println( content );
			   System.out.println( "------ End File ----------" );

			} finally {
			   channel.disconnect();
			}

		 } finally {
			session.disconnect();
		 }

	  }
   }

}
