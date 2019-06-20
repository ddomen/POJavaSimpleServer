package Server;

import Dto.DtoDataSet;
import Dto.DtoMetadata;
import Utils.UObject;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

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
    public Server SetMetadata(DtoMetadata metadata){ this.runner.SetMetadata(metadata); return this; }
}
