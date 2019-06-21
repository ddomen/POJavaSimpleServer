package Server;

import Dto.DtoData;
import Dto.DtoPackage;

import java.util.List;

public class Server {
    protected final int port;
    protected ServerThread runner;
    protected Thread thread;
    protected boolean verbose;

    public Server(){
        this.port = 80;
        this.Start();
    }
    public Server(List<DtoData> dataset, int port) {
        this.port = port;
        this.Start();
    }

    public Server SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    protected Server Start(){
        try{
            this.runner = new ServerThread(this.port).SetVerbose(this.verbose);
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

    public Server SetData(List<DtoData> dataset){ this.runner.SetData(dataset); return this; }
    public Server SetPackage(DtoPackage dtoPackage){ this.runner.SetPackage(dtoPackage); return this; }
}
