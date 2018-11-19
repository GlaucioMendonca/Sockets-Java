package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) {
        ServerSocket servidor = null;
        
        try {
            System.out.println("Iniciando o servidor ...");
            servidor = new ServerSocket(6500);
            System.out.println("Servidor Iniciado");

            while (true) {
                Socket cliente = servidor.accept();
                new GerenciadorCliente(cliente).start();
                System.out.println("host conectado");
            }
            
        } catch (Throwable e) {
            try {
                if (servidor != null) {
                    servidor.close();
                }
            } catch (IOException el) {
                
            }
            System.err.println("Erro interno do servidor");
            e.printStackTrace();
        }
    }
}
