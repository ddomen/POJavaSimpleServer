package Server;

import java.util.*;
import java.io.IOException;

import Dto.*;

public class Server {
    protected final int port;
    protected ServerThread runner;
    protected Thread thread;
    protected boolean verbose;

    public Server(){ this.port = 80; }
    public Server(List<DtoData> dataset, int port) { this.port = port; }

    public Server SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    public Server Start(){
        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER]: STARTING"); }
        try{
            this.runner = new ServerThread(this.port).SetVerbose(this.verbose);
            this.thread = new Thread(this.runner);
            thread.start();
            if(this.verbose){ System.out.println("[" + new Date() + "][SERVER]: STARTED"); }
        }
        catch (IOException ex){
            System.err.println("[" + new Date() + "][SERVER]: IMPOSSIBILE AVVIARE IL SERVER!");
            if(this.verbose){ ex.printStackTrace(); }
        }
        return this;
    }

    public Server Stop(){
        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER]: STOPPING"); }
        if(this.thread != null && this.thread.isAlive()) { this.thread.stop(); }
        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER]: STOPPED"); }
        return this;
    }

    public Server SetData(List<DtoData> dataset){ this.runner.SetData(dataset); return this; }
    public Server SetPackage(DtoPackage dtoPackage){ this.runner.SetPackage(dtoPackage); return this; }
}
