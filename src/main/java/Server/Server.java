package Server;

import java.util.*;
import java.io.IOException;

import Dto.*;

/**
 * Classe per la generazione di un Server
 */
public class Server {
    /**
     * Porta su cui ascoltare
     */
    protected final int port;
    /**
     * ServerThread per la generazione e la gestione di connessioni
     */
    protected ServerThread runner;
    /**
     * Thread disaccoppiamento del server
     */
    protected Thread thread;
    /**
     * Modalità verbose
     */
    protected boolean verbose;

    /**
     * Crea un Server che ascolta sulla porta 80
     */
    public Server(){ this.port = 80; }

    /**
     * Crea un Server che ascolta sulla porta specificata
     * @param port porta di ascolto
     */
    public Server(int port) { this.port = port; }

    /**
     * Setta la modalità verbose del client
     * @param verbose on/off modalità verbose
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public Server SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    /**
     * Avvia il server ed i relativi Thread
     * @return oggetto this per la concatenzaione (method chaining)
     */
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

    /**
     * Ferma il server ed i relativi Thread
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public Server Stop(){
        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER]: STOPPING"); }
        if(this.thread != null && this.thread.isAlive()) { this.thread.stop(); }
        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER]: STOPPED"); }
        return this;
    }

    /**
     * Imposta il dataset utilizzato dal server
     * @param dataset dataset da utilizzare
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public Server SetData(List<DtoData> dataset){ this.runner.SetData(dataset); return this; }

    /**
     * Imposta le informazioni del dataset utilizzato dal server
     * @param dtoPackage informazioni di dataset da utilizzare
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public Server SetPackage(DtoPackage dtoPackage){ this.runner.SetPackage(dtoPackage); return this; }
}
