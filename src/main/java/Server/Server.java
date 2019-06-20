package Server;

import Dto.DtoDataSet;
import Dto.DtoPackage;

import java.util.List;

public class Server {
    protected final int port;
    protected ServerThread runner;
    protected Thread thread;

    public Server(){
        this.port = 80;
        this.Start();
    }
    public Server(List<DtoDataSet> dataset, int port) {
        this.port = port;
        this.Start();
    }

    protected Server Start(){
        try{
            this.runner = new ServerThread(this.port);
            this.thread = new Thread(this.runner);
            thread.start();
        }
        catch (Exception ex){ System.err.println("Impossibile avviare il server!"); }
        return this;
    }

    public Server Stop(){
        if(this.thread != null && this.thread.isAlive()) { this.thread.stop(); }
        return this;
    }

    public Server SetData(List<DtoDataSet> dataset){ this.runner.SetData(dataset); return this; }
    public Server SetPackage(DtoPackage dtoPackage){ this.runner.SetPackage(dtoPackage); return this; }
}
