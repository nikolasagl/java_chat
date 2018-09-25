package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientManager extends Thread {

    private final Server servidor;
    private final Socket socket;
    private final PrintStream escritor;
    public String clientName;
    
    //Contrutor recebe o socket q ele vai administrar
    //Recebe o servidor com quem vai se comunicar
    public ClientManager(Socket socket, Server servidor) throws IOException {
        this.socket = socket;
        this.servidor = servidor;
        this.escritor = new PrintStream(socket.getOutputStream());
        
        //Thread Start pq sou uma thread
        start();
    }
    
    //Ouve tudo o que eh enviado pela view atraves da variavel leitor //LISTENER
    //Trata a mensagem e, de acordo com o que foi enviado pela view define qual funcao chamar no servidor
    @Override
    public void run() {
        try {
            Scanner leitor = new Scanner(socket.getInputStream());
            while (leitor.hasNextLine()) {
                String mensagem = leitor.nextLine();
                String[] result = mensagem.split(":");

                String metodo = result[0];
                String destinatario = (result.length > 1) ? destinatario = result[1] : null;
                String msg = (result.length > 2) ? msg = result[2] : null;

                System.err.println("Mensagem recebida pelo servidor: " + mensagem);
                
                if(mensagem.equals("sair")){
                    System.err.println("Entrei no Sair");
                    servidor.removerCliente(this); 
                    
                }else if(metodo.equals("login")){
                    System.err.println("Entrei no login");                    
                                       
                    String resp = servidor.tentaLogar(destinatario.toLowerCase());
                    if(resp.equals("login:true")){
                        this.clientName = destinatario;
                        servidor.listaUsuario();
                        escritor.println("login:true");
                        
                    }else{
                        escritor.println("login:false");
                    }
                    
                }else if(destinatario.equals("*")){
                    System.err.println("Entrei no Replicar Mensagem");
                    servidor.replicarMensagem(mensagem, clientName);

                }else if((metodo.equals("mensagem")) && !(destinatario.equals("*"))){
                    String[] teste = mensagem.split(":");
                    String[] destinos = teste[1].split(";");
                    System.err.println("Entrei no Mensagem com Destinatario");
                    servidor.mensagemDestino(mensagem, destinos, clientName);
                    
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
        escritor.println(mensagem);
    }
}
