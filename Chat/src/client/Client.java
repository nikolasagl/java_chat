package client;

import de.javasoft.plaf.synthetica.SyntheticaBlueLightLookAndFeel;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Client extends javax.swing.JFrame {
    
    private Socket client;
    private Scanner leitor;
    private PrintStream escritor;
    private String clientName;
    public static final ArrayList<String> lista = new ArrayList();
    
    
    //Construtor inicia os componentes do JFrame
    public Client() {
        initComponents();
    }
    
    public void playSound() throws UnsupportedAudioFileException, LineUnavailableException{
        String soundName = "sound.wav";    
        AudioInputStream audioInputStream;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void btnAtualizar(){
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < lista.size(); i++)
        {
            listModel.addElement(lista.get(i));
        }
        listaOnline.setModel(listModel);   
    }

    //Funcao chamada com o click do botao conectar
    //Cria a conexao do socket com o servidor
    //Define um nome ao socket "clientName" e loga no servidor com esse nome
    public void ligar() {
        try {
            final String aux_nome = txtNomeUsuario.getText().trim();
            
            txtAreaConversa.append("Conectando...\n");
            
            String host = txtServerIp.getText().trim();            
            int port = Integer.parseInt(txtServerPorta.getText().trim());
            client = new Socket(host, port);

            leitor = new Scanner(client.getInputStream());
            escritor = new PrintStream(client.getOutputStream());
            
            escritor.println("login:" + aux_nome);
            
            //Thread que ouve as respostas do servidor que chegam atraves do clientManager //LISTENER
            //Trata a mensagem recebida e, de acordo com o que foi enviado pelo clientManager define quais acoes realizar
            new Thread(){
                @Override
                public void run() {
                    try {
                        while (leitor.hasNextLine()) {
                            String msg = leitor.nextLine();
                            String[] result = msg.split(":");
                            System.err.println("Mensagem recebida do servidor: " + msg);
                            
                            if(msg.toLowerCase().startsWith("transmitir:")){
                                String mensagem = msg.substring(11, msg.length());   
                                if (result[2].equals("*")){ // result[1] para implementacao antiga
//                                    String aux = msg.substring(13, msg.length());
//                                    txtAreaConversa.append("Todos:" + aux + "\n"); Implementacao antiga
                                    txtAreaConversa.append("De: " + result[1] + " | Para:*:" + result[3] + "\n");
                                    txtAreaConversa.append("\n");
                                    try {
                                        playSound();
                                    } catch (UnsupportedAudioFileException | LineUnavailableException ex) {
                                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }else{
                                    String[] aux = mensagem.split(":");
                                    String aux2 = aux[1].substring(0, (aux[1].length())-1);
                                    txtAreaConversa.append("De: " + aux[0] + " | Para: " + aux2 + ": " + aux[2] + "\n");   
                                    txtAreaConversa.append("\n");
                                    try {
                                        playSound();
                                    } catch (UnsupportedAudioFileException | LineUnavailableException ex) {
                                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }   
                                
                            }else if(msg.toLowerCase().startsWith("lista_usuarios:")){
                                String[] aux = msg.split(":");
                                
                                String[] nomes = aux[1].split(";");
                                for(String nome: nomes){
                                    if(nome != null){
                                        addLista(nome);                                             
                                    }
                                } 
                                //Atualiza a lista de clientes
                                btnAtualizar();
                                lista.clear();
                                
                            }else if(msg.toLowerCase().equals("login:false")){
                               System.err.println("Nome de usuario ja existe... " + msg);                               
                               txtAreaConversa.append("Nome de usuario ja existe... Escolha outro!" + "\n"); 
                               txtAreaConversa.append("\n");
                               client.close();
                               
                            }else if(msg.toLowerCase().equals("login:true")){
                               clientName = aux_nome;
                               txtAreaConversa.append("Usuario conectado...\n");
                               txtAreaConversa.append("\n");
                               System.err.println("Usuario conectado...");
                               txtNomeUsuario.setEditable(false);
                               txtServerIp.setEditable(false);
                               txtServerPorta.setEditable(false);
                               btnConectar.setEnabled(false);
                               
                            }else{
                               System.err.println("Mensagem Invalida do Servidor");
                            }                            
                        }
                    } catch (IOException ex) {
                        txtAreaConversa.append("<-cliente->:" + ex.getMessage() + "\n");
                    }
                }

                private void addLista(String nome) {
                    if(nome != null){
                        ListModel model = (ListModel) listaOnline.getModel();
                        lista.add(nome);
                    }
                }                
            }.start(); //Starta a Thread    
            
        } catch (IOException ex) {
            txtAreaConversa.append("<-cliente->:" + ex.getMessage() + "\n");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpLigacao = new javax.swing.JPanel();
        jlblEndereco = new javax.swing.JLabel();
        txtServerIp = new javax.swing.JTextField();
        jlblPorto = new javax.swing.JLabel();
        txtServerPorta = new javax.swing.JFormattedTextField();
        btnConectar = new javax.swing.JButton();
        jlblNick = new javax.swing.JLabel();
        txtNomeUsuario = new javax.swing.JTextField();
        btnSair = new javax.swing.JButton();
        jpMensagens = new javax.swing.JPanel();
        jscpScrollMensagens = new javax.swing.JScrollPane();
        txtAreaConversa = new javax.swing.JTextArea();
        btnLimpar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaOnline = new javax.swing.JList<>();
        btnLimparSelecao = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtAreaEnviar = new javax.swing.JTextField();
        btnEnviar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jpLigacao.setBorder(javax.swing.BorderFactory.createTitledBorder("Conexão"));

        jlblEndereco.setText("IP:");

        jlblPorto.setText("Porta:");

        txtServerPorta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        btnConectar.setText("Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        jlblNick.setText("Nome:");

        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpLigacaoLayout = new org.jdesktop.layout.GroupLayout(jpLigacao);
        jpLigacao.setLayout(jpLigacaoLayout);
        jpLigacaoLayout.setHorizontalGroup(
            jpLigacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpLigacaoLayout.createSequentialGroup()
                .addContainerGap()
                .add(jlblNick)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtNomeUsuario, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jlblEndereco)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtServerIp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jlblPorto)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtServerPorta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnConectar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(btnSair, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpLigacaoLayout.setVerticalGroup(
            jpLigacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpLigacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(btnConectar)
                .add(txtServerPorta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblPorto)
                .add(txtServerIp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblEndereco)
                .add(jlblNick)
                .add(txtNomeUsuario, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(btnSair))
        );

        jpMensagens.setBorder(javax.swing.BorderFactory.createTitledBorder("Conversa"));

        txtAreaConversa.setColumns(20);
        txtAreaConversa.setRows(5);
        jscpScrollMensagens.setViewportView(txtAreaConversa);

        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpMensagensLayout = new org.jdesktop.layout.GroupLayout(jpMensagens);
        jpMensagens.setLayout(jpMensagensLayout);
        jpMensagensLayout.setHorizontalGroup(
            jpMensagensLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpMensagensLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpMensagensLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jscpScrollMensagens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMensagensLayout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(btnLimpar)))
                .addContainerGap())
        );
        jpMensagensLayout.setVerticalGroup(
            jpMensagensLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMensagensLayout.createSequentialGroup()
                .add(btnLimpar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jscpScrollMensagens, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 303, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Online"));

        jScrollPane1.setViewportView(listaOnline);

        btnLimparSelecao.setText("Limpar Selecao");
        btnLimparSelecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparSelecaoActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(btnLimparSelecao)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(btnLimparSelecao)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Mensagens"));

        txtAreaEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAreaEnviarActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtAreaEnviar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtAreaEnviar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpLigacao, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jpMensagens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(btnEnviar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jpLigacao, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jpMensagens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(btnEnviar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtAreaEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAreaEnviarActionPerformed
        btnEnviar.doClick();
    }//GEN-LAST:event_txtAreaEnviarActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        if (txtNomeUsuario.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome de usuario não pode ser vazio.", "Nome de usuario vazio...", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtServerIp.getText().trim().isEmpty()) {
            txtServerIp.setText("127.0.0.1");
        }
        
        if (txtServerPorta.getText().trim().isEmpty()) {
            txtServerPorta.setText("6666");
        }
        
        ligar();
    }//GEN-LAST:event_btnConectarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (client != null) {
            try {
                client.close(); 
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        if((txtAreaEnviar.getText().trim() != null) && (!txtAreaEnviar.getText().trim().isEmpty())){        
            String texto_mensagem = txtAreaEnviar.getText().trim();        
            String destino = null;
            int[] destinos = this.listaOnline.getSelectedIndices();        
        
            //Mensagem para varios destinatarios
            if(destinos.length > 1){    
                String mensagem = "mensagem:";
                for (int index: destinos){
                    destino = (String) this.listaOnline.getModel().getElementAt(index);
                    if(!(destino.equals(clientName))){
                        mensagem += destino + ";";
                    }else{
                        txtAreaConversa.append("Voce nao pode enviar mensagens para voce mesmo!!\n");
                        txtAreaConversa.append("\n");
                    }                   
                }
                mensagem += ": " + texto_mensagem;
                escritor.println(mensagem);    
                String[] result = mensagem.split(":");
                txtAreaConversa.append("Para: " + result[1].substring(0, (result[1].length())-1) + ": " + result[2] + "\n");
                txtAreaConversa.append("\n");

            //Mensagem para um destinatario apenas
            }else if (this.listaOnline.getSelectedIndex() > -1) {
                destino = (String) this.listaOnline.getSelectedValue();
                if(!(destino.equals(clientName))){
                    escritor.println("mensagem:" + destino + ":" + texto_mensagem);
                    txtAreaConversa.append("Para: " + destino + ": " + texto_mensagem + "\n");
                    txtAreaConversa.append("\n");
                }else{
                    txtAreaConversa.append("Voce nao pode enviar mensagens para voce mesmo!!\n");
                    txtAreaConversa.append("\n");
                }

            //Mensagem para todos
            }else{
                escritor.println("mensagem:" + "*: " + texto_mensagem);
                txtAreaConversa.append("Para:*: " + texto_mensagem + "\n");
                txtAreaConversa.append("\n");
            }
            txtAreaEnviar.setText("");        
        }        
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        txtAreaConversa.setText("");   
        listaOnline.clearSelection();
    }//GEN-LAST:event_btnLimparActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        escritor.println("sair");
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnLimparSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparSelecaoActionPerformed
        listaOnline.clearSelection();
    }//GEN-LAST:event_btnLimparSelecaoActionPerformed

    
    /**
     * @param args the command line arguments
     * @throws java.text.ParseException
     */
    public static void main(String args[]) throws ParseException {   
        
        UIManager.put("Synthetica.window.decoration", Boolean.TRUE);
        SyntheticaBlueLightLookAndFeel BlueLight = new SyntheticaBlueLightLookAndFeel();           
       
        try{
            UIManager.setLookAndFeel(BlueLight);
        }
        catch (UnsupportedLookAndFeelException e){}
                
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                new Client().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConectar;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnLimparSelecao;
    private javax.swing.JButton btnSair;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlblEndereco;
    private javax.swing.JLabel jlblNick;
    private javax.swing.JLabel jlblPorto;
    private javax.swing.JPanel jpLigacao;
    private javax.swing.JPanel jpMensagens;
    private javax.swing.JScrollPane jscpScrollMensagens;
    private javax.swing.JList<String> listaOnline;
    private javax.swing.JTextArea txtAreaConversa;
    private javax.swing.JTextField txtAreaEnviar;
    private javax.swing.JTextField txtNomeUsuario;
    private javax.swing.JTextField txtServerIp;
    private javax.swing.JFormattedTextField txtServerPorta;
    // End of variables declaration//GEN-END:variables
}
