import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


class Server{

	public static void main(String args[]) throws Exception{
		String port=args[0];
		ServerSocket serverSocket=new ServerSocket(Integer.parseInt(port));

		//Runtime.getRuntime().addShutdownHook(new ProcessorHook(serverSocket));

		stopResources(serverSocket);
		System.out.println("Started the Server...");
		while(true){
			Socket client=serverSocket.accept();
			Thread thread=new Thread(new ClientHandler(client));
			//Runtime.getRuntime().addShutdownHook(thread);
			thread.start();
		}
	}

	private static void stopResources(ServerSocket serverSocket) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					ArrayList<Socket> arrayList=ClientHandler.socketList;
					System.out.println("Closing the opened threads..");
					for (int i = 0; i < arrayList.size(); i++) {
						Socket socket=arrayList.get(i);
						socket.close();
					}
					System.out.println("Server is stopped..");
					Thread.sleep(1500);
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}));
	}

}