package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

public class ClientManager extends Thread {

    private Server servidor;
    private Socket socket;
    private DataOutputStream escritor;
    public String clientName;
    
    //Contrutor recebe o socket q ele vai administrar
    //Recebe o servidor com quem vai se comunicar
    public ClientManager(Socket socket, Server servidor) throws IOException {
        this.socket = socket;
        this.servidor = servidor;
        this.escritor = new DataOutputStream(socket.getOutputStream());
        
        //Thread Start pq sou uma thread
        start();
    }
    
    //Ouve tudo o que eh enviado pela view atraves da variavel leitor //LISTENER
    //Trata a mensagem e, de acordo com o que foi enviado pela view define qual funcao chamar no servidor
    @Override
    public void run() {
        try {
            DataInputStream leitor = new DataInputStream(socket.getInputStream());
            while (true) {
                String mensagem = leitor.readUTF();
                String[] result = mensagem.split(":");

                String metodo = result[0];
                String remetente = null;
                String msg = null;
                
                if(result.length > 1){
                    remetente = result[1];
                }

                if(result.length >= 3){
                    msg = result[2];
                }

                System.err.println(metodo + ":" + remetente + ":" + msg);

                if(metodo.equals("lista_usuarios")){
                    System.err.println("Entrei no lista_usuario");
                    servidor.listaUsuario();

                }else if((result.length > 1) && (metodo.equals("login"))){
                    System.err.println("Entrei no clientName");
                    this.clientName = result[1];

                }else if((result.length == 3) && (remetente.equals("*"))){
                    System.err.println("Entrei no replicarMensagem");
                    servidor.replicarMensagem(mensagem);

                }else if((result.length > 3) && (metodo.equals("mensagem"))){
                    System.err.println("Entrei no Mensagem com Destinatario");
                    servidor.mensagemDestino(result);

                    for(int i=0; i<result.length; i++){
                        System.err.println(result[i]);
                    }
                    
                }else if(mensagem.equals("sair")){
                    System.err.println("Entrei no Sair");
                    escritor.writeUTF("sair");
                    servidor.removerCliente(this); 
                    
                }else{
                    System.err.println("Nao entrei em porra nenhuma");                                       
                }
            }
        } catch (EOFException ex) {
            //DO NOTHING
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            servidor.removerCliente(this);
        }
    }
    
    //FUNCOES UTILIZADAS PELO SERVIDOR PARA COMUNICACAO COM AS VIEWS
    
    //Envia a mensagem
    public void enviarMensagem(String mensagem) {
        try {
            escritor.writeUTF("mensagem:" + mensagem);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    //Envia a lista de clientes conectados
    public void enviarLista(ArrayList<ClientManager> lista) {
        try {
            synchronized (lista) {
                for(ClientManager cli: lista){
                    escritor.writeUTF("lista:" + cli.clientName);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    //Encerra a conexao entre o socket e o servidor
    public void fechar() throws IOException {
        socket.close();
    }
}
