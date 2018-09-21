package org.pap.wiki.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

public class Server {

    private ServerSocket servidor; // Servidor localhost:9999
    
    private ClientManager client;
    
    public final ArrayList<ClientManager> clientes; //Lista que guarda os clientes

    public Server(int port) throws IOException {
        clientes = new ArrayList<ClientManager>();
        listen(port);
    }

    private void listen(int port) throws IOException {

        //Cria o socket servidor
        servidor = new ServerSocket(port);
        System.out.println("Servidor iniciado em " + servidor);

        //Laço que recebe as ligações dos clientes
        while (true) {

            Socket cliente = servidor.accept();
            System.out.println("Ligação aceita de " + cliente);
            
            client = new ClientManager(cliente, this);
            
            //criar um novo handler para este cliente
            clientes.add(client);
        }
    }

    public void replicarMensagem(String mensagem) {
        String[] result = mensagem.split(":");
        
        synchronized (clientes) {
            for (ClientManager cl : clientes) {
                cl.enviarMensagem(result[0] + ":" + result[2]);                
            }
        }
    }

    public void removerCliente(ClientManager cliente) {
        synchronized (clientes) {
            System.out.println("A remover a ligação de " + cliente);
            clientes.remove(cliente);
            System.out.println("Ligações restantes: " + clientes.size());
            try {
                cliente.fechar();
            } catch (IOException ex) {
                System.out.println("Erro ao desligar o contacto com " + cliente);
                System.out.println(ex.getMessage());
            }
        }
    }

    public void removeConnection(Socket cliente) {
    }
    
    public void listaUsuario(){
        synchronized (clientes) {
            for (ClientManager cliente: clientes) {
                cliente.enviarLista(clientes);                
            }
        }
    }

    public static void main(String args[]) {
        try {
            new Server(9999);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    void mensagemDestino(String[] result) {
        for(ClientManager cliente: clientes){
            if(cliente.clientName.equals(result[2])){
                String mensagem = result[1] + ":" + result[3];
                cliente.enviarMensagem(mensagem);
            }
        }
    }
}
