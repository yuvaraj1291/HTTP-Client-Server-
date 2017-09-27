import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

class ClientHandler implements Runnable{


	public static ArrayList<Socket> socketList;

	static {
		socketList = new ArrayList<Socket>();
	}

	Socket client;

	ClientHandler(Socket client){
		this.client=client;
		socketList.add(client);
	}


	@Override
	public void run() {

		System.out.println("\n\nRunning THREAD "+Thread.currentThread().getName());

		System.out.println("connected to server");


		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
			String line=null;

			String temp = ".";
			String requestHeader = "";
			int size=0;
			while (!temp.equals("")) {
				temp = reader.readLine();
				if(temp.contains("Content-Length: ")){
					size=Integer.valueOf(temp.split(" ")[1]);
				}
				requestHeader += temp + "\n";
			}
			System.out.println(" requestHeader"+requestHeader);

			File fileName=new File(requestHeader.split(" ")[1].replace("/",""));

			if(requestHeader.startsWith("PUT")){
				String body="";
				while (size>0 && (temp=reader.readLine())!=null){
					body+=temp;
					size-=temp.length()+1;

				}


				BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

				bw.write(body);
				bw.close();

				System.out.println("Saved the file");

				Date today = new Date();
				String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + today;


				PrintStream printStream = new PrintStream(client.getOutputStream());
				printStream.println(httpResponse);
				printStream.close();

			}
			else if(requestHeader.startsWith("GET")){
				size=0;
				System.out.println(fileName+" is the requested fIle name");

				if(fileName.exists())
				{	

					BufferedReader br=new BufferedReader(new FileReader(fileName));
					String currentLine=null;
					StringBuffer sb=new StringBuffer();
					
					while((currentLine=br.readLine())!=null){  
						sb.append(currentLine+"\n");
					}
					br.close();
					String httpResponse = "HTTP/1.1 200 OK\r\n" + sb.toString();
					//client.getOutputStream().write(httpResponse.getBytes("UTF-8"));
					//System.out.println("get response sent from server");
					//String resp = "HTTP/1.1 200 OK\r\nDate: " + new Date() + "\r\n\r\n";
                    System.out.println("Started displaying the contents..: \n" +httpResponse );
					PrintStream printStream = new PrintStream(client.getOutputStream());
					printStream.println(httpResponse);
					printStream.close();
				}

				else
				{
					String httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n"; 
					PrintStream printStream = new PrintStream(client.getOutputStream());
					printStream.println(httpResponse);
					printStream.flush();
					printStream.close();
					System.out.println("get response sent from server and is invalid "+httpResponse);
				}

			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
