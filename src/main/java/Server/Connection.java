package Server;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import Dto.*;


public class Connection implements Runnable{
    protected Socket connect;
    protected BufferedReader input;
    protected PrintWriter output;
    protected BufferedOutputStream dataOutput;
    protected List<DtoDataSet> dataset;
    protected DtoMetadata metadata;

    public Connection(Socket connect, DtoMetadata metadata, List<DtoDataSet> dataset){
        this.connect = connect;
        this.metadata = metadata;
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
            String url = parse.nextToken().toLowerCase().substring(1);

            Controller cnt = new Controller(method, url);

            Response(cnt.Execute(this.metadata, this.dataset));
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
            put(404, "NOT FOUND");
            put(500, "INTERNAL SERVER ERROR");
            put(503, "SERVICE UNAVAILABLE");
        }
    };

    protected Connection Response(ActionResponse response) throws IOException {
        this.output.println("HTTP/1.1 " + response.status + " " + StatusCodes.get(response.status));
        this.output.println("Server: Java HTTP Server");
        this.output.println("Date: " + new Date());
        this.output.println("Content-type: application/json" );
        this.output.println("Content-length: " + response.length);
        this.output.println();
        this.output.flush();

        this.dataOutput.write(response.result.getBytes(), 0, response.length);
        this.dataOutput.flush();
        return this;
    }
}
