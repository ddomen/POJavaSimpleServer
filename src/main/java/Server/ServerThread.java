package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import Dto.*;



public class ServerThread implements Runnable{
    protected List<DtoDataSet> dataset;
    protected DtoPackage metadata;
    protected ServerSocket socket;
    public ServerThread(int port) throws IOException { this.socket = new ServerSocket(port); }

    protected ServerThread SetData(List<DtoDataSet> dataset){ this.dataset = dataset; return this; }
    protected ServerThread SetMetadata(DtoPackage metadata){ this.metadata = metadata; return this; }

    public void run() {
        while(true){
            Connection connect = null;
            try{ connect = new Connection(this.socket.accept(), this.metadata, this.dataset); }
            catch (Exception ex){ System.err.println("Connessione rifiutata!"); }

            if(connect != null){
                Thread response = new Thread(connect);
                response.start();
            }
        }
    }
}