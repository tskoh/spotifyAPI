import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

/**
* A sandbox playground class of Spotify's APIs for manipulating playlists
* Demonstrates GET, POST, PUT and Delete
*
* @author Timothy Koh
*/

public class spotifyAPI {

	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String DELETE = "DELETE";

	private static final String playlistURL = "https://api.spotify.com/v1/playlists/0nCvvUSZnsYiFI82uHydXg";
	private static final String dataURI = "{\"uris\":[\"spotify:track:32pVxiyqfsKKoZMEyF9WDn\"]}"; // All Good - Samm Henshaw
	private static final String dataDetails = "{\"name\":\"Workday\",\"description\":\"This is a test\",\"public\":true}";
	private static final String dataDelete = "{\"tracks\":[{\"uri\":\"spotify:track:32pVxiyqfsKKoZMEyF9WDn\",\"positions\":[0]}]}";

	private static String apiToken;

	/**
	* Method to get a spotifly playlist's details. Must have spotify playlist id
	* @return void
	*/
	private static void sendGET() throws IOException {
		URL url = new URL(playlistURL);
		HttpURLConnection conn = createConnection(url, GET);

		// Get input stream and store into bufferedreader object
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		// Append lines into response
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}

	/**
	* Method to add item(s) to a playlist. Must have spotify playlist id and
	* item URIs
	* @return void
	*/
	private static void sendPOST() throws IOException {
		// Need to add /tracks to endpoint in order to fulfill this post request
		URL url = new URL(playlistURL + "/tracks");

		// Helper method to create connection obj
		HttpURLConnection conn = createConnection(url, POST);
		conn.setDoOutput(true);

		// Helper method to add data values to request
		write2Output(conn, dataURI);

		int responseCode = conn.getResponseCode();
		System.out.println("POST Response Code: " + responseCode);
		System.out.println(conn.getResponseMessage());
	}

	/**
	* Method to edit the details of a playlist. Must have spotify playlist id and
	* desired description info for playlist
	* @return void
	*/
	private static void sendPUT() throws IOException {
		URL url = new URL(playlistURL);

		// Helper method to create connection obj
		HttpURLConnection conn = createConnection(url, PUT);
		conn.setDoOutput(true);

		// Helper method to add data values to request
		write2Output(conn, dataDetails);

		int responseCode = conn.getResponseCode();
		System.out.println("PUT Response Code: " + responseCode);
		System.out.println(conn.getResponseMessage());

	}

	/**
	* Method to remove item(s) from a playlist. Must have spotify playlist id and
	* item URI and position of item in playlist.
	* @return void
	*/
	private static void sendDELETE() throws IOException {
		// Need to add /tracks to endpoint in order to fulfill this delete request
		URL url = new URL(playlistURL + "/tracks");

		// Helper method to create connection obj
		HttpURLConnection conn = createConnection(url, DELETE);
		conn.setDoOutput(true);

		// Helper method to add data values to request
		write2Output(conn, dataDelete);

		int responseCode = conn.getResponseCode();
		System.out.println("DELETE Response Code: " + responseCode);
		System.out.println(conn.getResponseMessage());
	}

	/** Helper function to write data to the connection's output stream
	* @param conn This is the connection established
	* @param data This is the data to be written
	* @return void
	*/
	private static void write2Output(HttpURLConnection conn, String data) throws IOException {
		try(OutputStream os = conn.getOutputStream()) {
		    byte[] input = data.getBytes();
		    os.write(input, 0, input.length);
		    os.flush();
		    os.close();
		}
	}

	/** Helper function to add request properties
	* @param url This is the endpoint url
	* @param method This is the request method
	* @return HttpURLConnection Returns connection object with added properties
	*/
	private static HttpURLConnection createConnection(URL url, String method) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Authorization", "Bearer " + apiToken);

		// Need extra properties for data
		if(!method.equals(GET)) {
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
		}

		return conn;
	}

	/** Main driver. Need to get auth token from developer.spotify.com
	* @return void
	*/
	public static void main(String[] args) throws IOException {

		// properties file holds auth token
		InputStream input = new FileInputStream("./config.properties");
		Properties prop = new Properties();
		prop.load(input);
		apiToken = prop.getProperty("api.token");

		// Scan in user input, based on the number it will execute one of the methods
		// Any other input will exit
		int numInput;
		boolean exit = false;
		Scanner scanner = new Scanner(System.in);
		while(!exit) {
			System.out.println("\n1 - GET\n2 - PUT\n3 - POST\n4 - DELETE\n");

			numInput = scanner.nextInt();
			switch(numInput) {
				case 1:
					sendGET();
					break;
				case 2:
					sendPUT();
					break;
				case 3:
					sendPOST();
					break;
				case 4:
					sendDELETE();
					break;
				default:
					System.out.println("Exiting");
					exit = true;
					break;
			}
        	}
	}
}
