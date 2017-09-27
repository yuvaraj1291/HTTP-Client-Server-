import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static void main(String[] arg) throws Exception{

		String hostName=arg[0];
		String port=arg[1];
		String command=arg[2];
		String fileName=arg[3];
		//String url="http://"+hostName+":"+port;

		//fileName = "C://Users/ysundarr/Downloads/Project/"+fileName;



		Socket socket=new Socket(hostName,Integer.valueOf(port));

		//PrintWriter request=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),true));
		PrintWriter request = new PrintWriter(socket.getOutputStream(),
				true);

		BufferedReader response = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		if(command.equalsIgnoreCase("PUT")){
			StringBuffer sb=new StringBuffer();


			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line.trim());
					sb.append("\n");
				}
			}
			String body=sb.toString(); 

			request.print(command.toUpperCase()+" /" + fileName + "/ HTTP/1.1\r\n"); // "+path+"
			request.print("Host: " + hostName+"\r\n");
			request.write("Content-Length: " + body.length() + "\r\n");
			request.print("Accept-Language: en-us\r\n");
			request.print("Connection: Keep-Alive\r\n");
			request.print("Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n");
			request.print("Content-type: text/html\r\n");
			request.print("\r\n");


			request.println(body);
			request.flush();
			System.out.println("PUT request sent from the Server...");
			System.out.println("Body of the file is supposed to be: "+body);
		}

		else if(command.equalsIgnoreCase("GET")){
			request.print(command.toUpperCase()+" /"+fileName+"/ HTTP/1.1\r\n");
			request.print("Accept: text/plain, text/html, text/*\r\n");
			request.print("Content-Type: application/x-www-form-urlencoded\r\n");
			request.print("\r\n");
			request.flush();
			System.out.println("GET request sent from the client..");

		}

		String responseLine;



		while ((responseLine = response.readLine()) != null) {
			System.out.println(responseLine);
		}
		System.out.println("Stopped the Server");

		request.close();
		response.close();
		socket.close();

	}
}
