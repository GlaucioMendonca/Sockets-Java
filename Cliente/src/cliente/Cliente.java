package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

    public static void main(String[] args) {
        try {
            final Socket cliente = new Socket("127.0.0.1", 6500);
            // lendo mensagem do servidor
            new Thread() {
                public void run() {
                    try {
                        BufferedReader leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

                        while (true) {
                        	if(cliente.isClosed()){	
                        		break;
                        	}
                            String mensagem = leitor.readLine();
                            System.out.println(mensagem);
                        }

                    } catch (IOException e) {
                        System.out.println("você saiu do chat.");
                    }

                }
            }.start();
            
            // escrevendo mensagem para o servidor
            PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader leitorTerminal = new BufferedReader(new InputStreamReader(System.in));
            
            String mensagemTerminal = "";
            
            while (true) {
                mensagemTerminal = leitorTerminal.readLine();
                if (mensagemTerminal == null || mensagemTerminal.length() == 0) {
                	System.out.println("Digite algo.");
                    
                }else {
                	escritor.println(mensagemTerminal);
                    
                    if (mensagemTerminal.equalsIgnoreCase("bye")) {
                    	escritor.println(mensagemTerminal);
                        cliente.close();
                    	break;
                    }
                }
                
            }

        } catch (UnknownHostException e) {
            System.err.println("O cliente fechou a conexão");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("servidor indisponivel.");
            
        }
    }

}
