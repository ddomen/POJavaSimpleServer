package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

import Dto.*;



public class ServerThread implements Runnable{
    protected List<DtoData> dataset;
    protected DtoPackage dtoPackage;
    protected ServerSocket socket;
    protected boolean verbose;

    public ServerThread(int port) throws IOException { this.socket = new ServerSocket(port); }

    public ServerThread SetVerbose(boolean verbose){ this.verbose = verbose; return this; }
    public ServerThread SetData(List<DtoData> dataset){ this.dataset = dataset; return this; }
    public ServerThread SetPackage(DtoPackage dtoPackage){ this.dtoPackage = dtoPackage; return this; }

    public void run() {
        while(true){
            Connection connect = null;
            try{
                if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONNECTION]: CREAZIONE"); }
                connect = new Connection(this.socket.accept(), this.dtoPackage, this.dataset).SetVerbose(this.verbose);
            }
            catch (Exception ex){
                System.err.println("[" + new Date() + "][SERVER][CONNECTION]: ERRORE CREAZIONE");
                if(this.verbose){ ex.printStackTrace(); }
            }

            if(connect != null){
                Thread response = new Thread(connect);
                response.start();
            }
        }
    }
}