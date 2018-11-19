package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorCliente extends Thread {

	private Socket cliente;
	private String nomecliente;
	private BufferedReader leitor;
	private PrintWriter escritor;
	private static final Map<String, GerenciadorCliente> clientes = new HashMap<String, GerenciadorCliente>();

	public GerenciadorCliente(Socket cliente) {
		this.cliente = cliente;
	}

	@Override
	public void run() {
		try {
			leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));// esta recebendo do cliente
			escritor = new PrintWriter(cliente.getOutputStream(), true);// esta mandando pro cliente, true e o autoflush

			String msg = "";

			
			
			do {
				escritor.println("Bem vindo ao chat. Por favor digite um nome: ");
				msg = leitor.readLine();
				if(!msg.isEmpty()) break;
				
			} while (true);

			if (!clientes.isEmpty()) {
				while (true) {
					if (!(nomecliente == null))
						break;

					for (String c : clientes.keySet()) {
						if (c.trim().equals(msg)) {
							escritor.println(
									"Nome escolhido: " + msg + ", já existe no Chat, escolha outro por favor!");
						} else {
							this.nomecliente = msg;
							escritor.println("Ola " + this.nomecliente + "(Digite bye para sair do chat)");
							clientes.put(this.nomecliente, this);
						}

						if (!(nomecliente == null))
							break;

						escritor.println("Digite o novo nome. ");
						msg = leitor.readLine();
					}

				}

			} else {
				this.nomecliente = msg;
				escritor.println("Ola " + this.nomecliente + "(Digite bye para sair do chat)");
				clientes.put(this.nomecliente, this);
			}

			while (true) {

				msg = leitor.readLine();
				Date date = new Date();
				SimpleDateFormat formatador = new SimpleDateFormat(" HH:mm, dd/MM/yyyy");

				// OPCAO SAIR DO CHAT
				if (msg.equalsIgnoreCase("bye")) {
					this.cliente.close();

					// ENVIAR MENSAGEM PRIVADA
				} else if (msg.toLowerCase().startsWith("send -user")) {

					String[] msgArray = msg.split(" ");
					String nome = msgArray[2];
					String mensagem = "";
					for (int i = 0; i < msgArray.length; i++) {
						if (i > 2) {
							mensagem = mensagem + " " + msgArray[i];
						}
					}

					GerenciadorCliente destinatario = clientes.get(nome);

					if (destinatario == null || mensagem.isEmpty()) {
						if (mensagem.isEmpty()) {
							escritor.println("Escreva uma mensagem.");
						} else {
							escritor.println("Usuario " + nome + " nao existe");
						}
					} else {
						destinatario.getEscritor()
								.println(cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort() + "/~"
										+ this.nomecliente + ": " + mensagem + " " + formatador.format(date));
					}

					// LISTA TODOS OS USUARIOS
				} else if (msg.equals("list")) {
					StringBuilder str = new StringBuilder();
					for (String c : clientes.keySet()) {
						str.append(c);
						str.append(" | ");
					}
					str.delete(str.length() - 1, str.length());
					escritor.println("Nome dos usuarios: " + str.toString());

					// ENVIAR MENSAGEM PARA TODOS
				} else if (msg.toLowerCase().startsWith("send -all ")) {
					String mensagem = msg.substring(10, msg.length());

					for (String c : clientes.keySet()) {

						GerenciadorCliente destinatario = clientes.get(c);
						destinatario.getEscritor()
								.println(cliente.getInetAddress().getHostAddress() + ": " + cliente.getPort() + "/~"
										+ this.nomecliente + ":" + mensagem + " " + formatador.format(date));
					}

					// RENOMEAR NOME DO USUARIO
				} else if (msg.toLowerCase().startsWith("rename")) {

					String novonome = msg.substring(msg.indexOf(" "), msg.length());
					novonome = novonome.trim();
					System.out.println(novonome);
					StringBuilder str = new StringBuilder();
					GerenciadorCliente destinatario = clientes.get(novonome);

					for (String c : clientes.keySet()) {
						str.append(c);
						GerenciadorCliente nomelista = clientes.get(c);
						if (nomelista != destinatario) {
							clientes.remove(this.nomecliente);
							System.out.println("Nome depois de remover: " + novonome);
							clientes.put(novonome, this);
							System.out.println(novonome);
							this.nomecliente = novonome;
							escritor.println("Nome alterado com sucesso");
							break;
						} else if (nomelista == destinatario) {
							escritor.println("Nome " + novonome + " ja existe");
							break;
						}
					}
				} else {
					// COMANDO INVALIDO
					escritor.println("Comando Inexistente");
				}
			}
		} catch (IOException e) {
			System.err.println("O cliente " + this.nomecliente + " saiu do chat");
			// REMOVE O CLIENTE DA LISTA
			clientes.remove(this.nomecliente);
		} catch (NullPointerException e) {
			System.out.println("Conexão encerada inesperadamente pelo cliente: " + nomecliente);
			clientes.remove(this.nomecliente);
		}
	}

	public PrintWriter getEscritor() {
		return escritor;
	}

	public BufferedReader getLeitor() {
		return leitor;
	}

}
