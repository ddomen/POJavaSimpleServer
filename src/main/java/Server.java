import DTO.DataSetDTO;
import DTO.DataSetMetadataDTO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Server {
    protected final int port;
    protected Main runner;
    protected Thread thread;

    public Server(){
        this.port = 80;
        this.Start();
    }
    public Server(List<DataSetDTO> dataset, int port) {
        this.port = port;
        this.Start();
    }

    protected Server Start(){
        try{
            this.runner = new Main(this.port);
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

    public Server SetData(List<DataSetDTO> dataset){ this.runner.SetData(dataset); return this; }
    public Server SetMetadata(DataSetMetadataDTO metadata){ this.runner.SetMetadata(metadata); return this; }

    protected class Main implements Runnable{
        protected List<DataSetDTO> dataset;
        protected DataSetMetadataDTO metadata;
        protected ServerSocket socket;
        public Main(int port) throws IOException{ this.socket = new ServerSocket(port); }

        protected Main SetData(List<DataSetDTO> dataset){ this.dataset = dataset; return this; }
        protected Main SetMetadata(DataSetMetadataDTO metadata){ this.metadata = metadata; return this; }

        public void run() {
            while(true){
                Connection connect = null;
                try{ connect = new Connection(this.socket.accept(), this.dataset); }
                catch (Exception ex){ System.err.println("Connessione rifiutata!"); }

                if(connect != null){
                    Thread response = new Thread(connect);
                    response.start();
                }
            }
        }
    }

    protected class Connection implements Runnable{
        protected Socket connect;
        protected BufferedReader input;
        protected PrintWriter output;
        protected BufferedOutputStream dataOutput;
        protected List<DataSetDTO> dataset;

        public Connection(Socket connect, List<DataSetDTO> dataset){
            this.connect = connect;
            this.dataset = dataset;
        }

        public void run() {
            try {
                this.input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                this.output = new PrintWriter(connect.getOutputStream());
                this.dataOutput = new BufferedOutputStream(connect.getOutputStream());

                String input = this.input.readLine();
                StringTokenizer parse = new StringTokenizer(input);
                String method = parse.nextToken().toUpperCase();
                String url = parse.nextToken().toLowerCase();

                Response("OK", 200);

                System.out.println("URL: " + url);
            }
            catch (Exception ex){ System.err.println("Impossibile stabilire la connessione");  }
            finally { this.CloseBuffers(); }
        }

        protected Connection CloseBuffers(){
            try {
                this.input.close();
                this.output.close();
                this.dataOutput.close();
                this.connect.close();
            }
            catch (Exception e) { System.err.println("Errore nella chiusura degli stream di connessione"); }
            return this;
        }

        protected HashMap<Integer, String> StatusCodes = new HashMap<Integer, String>(){
            {
                put(200, "OK");
            }
        };

        protected Connection Response(String response, int status) throws IOException{
            this.output.println("HTTP/1.1 " + status + " " + StatusCodes.get(status));
            this.output.println("Server: Java HTTP Server");
            this.output.println("Date: " + new Date());
            this.output.println("Content-type: application/json" );
            this.output.println("Content-length: " + response.length());
            this.output.println();
            this.output.flush();

            this.dataOutput.write(response.getBytes(), 0, response.length());
            this.dataOutput.flush();
            return this;
        }
    }
}
