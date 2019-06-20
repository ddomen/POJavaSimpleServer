package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import Dto.*;



public class ServerThread implements Runnable{
    protected List<DtoDataSet> dataset;
    protected DtoPackage dtoPackage;
    protected ServerSocket socket;
    public ServerThread(int port) throws IOException { this.socket = new ServerSocket(port); }

    protected ServerThread SetData(List<DtoDataSet> dataset){ this.dataset = dataset; return this; }
    protected ServerThread SetPackage(DtoPackage dtoPackage){ this.dtoPackage = dtoPackage; return this; }

    public void run() {
        while(true){
            Connection connect = null;
            try{ connect = new Connection(this.socket.accept(), this.dtoPackage, this.dataset); }
            catch (Exception ex){ System.err.println("Connessione rifiutata!"); }

            if(connect != null){
                Thread response = new Thread(connect);
                response.start();
            }
        }
    }
}