package br.com.phdss;

import java.io.*;
import java.util.Properties;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Classe que contem a chave privada e criptograva o arquivo auxiliar ou gera uma senha usando a chave privada ou gera o cacerts de todos os estados.
 *
 * @author Pedro H. Lira
 */
public class AdmPDV {

    /**
     * Construtor padrao.
     */
    private AdmPDV() {
    }

    /**
     * Metodo de acao externa usado para criptografar o arquivo auxiliar, senha e gerar cacerts.
     *
     * [opcao] = arquivo, para criptogravar o arquivo auxiliar.properties. [opcao] = senha, para criptogravar uma senha informada. [opcao] = cacerts, para gerar o arquivo NFeCacerts de todos os
     * estados.
     *
     * @param args um array sendo o primeiro parametro uma das opcoes acima.
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            Console console = System.console();
            if (console == null) {
                System.out.println("Erro ao recuperar o console.");
                System.exit(0);
            } else {
                char pws[] = console.readPassword("Informe a senha do ADM: ");
                if (!validarSenha(new String(pws))) {
                    System.out.println("Senha de ADM informada incorreta.");
                    System.exit(0);
                }
            }

            if (args[0].contains("-a")) {
                File arquivo = new File(args[1]);

                try {
                    // recuperando os valores
                    StringBuilder sb = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
                        while (br.ready()) {
                            sb.append(br.readLine()).append("\n");
                        }
                    }

                    // salva o arquivo
                    if (args[0].contains("-c")) {
                        try (FileWriter outArquivo = new FileWriter(arquivo.getAbsolutePath().replace("properties", "txt"))) {
                            BasicTextEncryptor encryptor = new BasicTextEncryptor();
                            encryptor.setPassword(ChavePrivada.VALOR);
                            String dados = encryptor.encrypt(sb.toString());
                            outArquivo.write(dados);
                            outArquivo.flush();
                            System.out.println("Arquivo criptografado: " + arquivo.getAbsolutePath().replace("properties", "txt"));
                        }
                        System.exit(0);
                    } else if (args[0].contains("-d")) {
                        try (FileWriter outArquivo = new FileWriter(arquivo.getAbsolutePath().replace("txt", "properties"))) {
                            BasicTextEncryptor encryptor = new BasicTextEncryptor();
                            encryptor.setPassword(ChavePrivada.VALOR);
                            String dados = encryptor.decrypt(sb.toString());
                            outArquivo.write(dados);
                            outArquivo.flush();
                            System.out.println("Arquivo descriptografado: " + arquivo.getAbsolutePath().replace("txt", "properties"));
                        }
                        System.exit(0);
                    }
                } catch (Exception ex) {
                    System.out.println("Nao foi possivel ler ou gerar o arquivo.");
                    ex.printStackTrace(System.out);
                }
            } else if (args[0].contains("-t")) {
                char txt[] = args[1].toCharArray();
                if (args[0].contains("-c")) {
                    BasicTextEncryptor seguranca = new BasicTextEncryptor();
                    seguranca.setPassword(ChavePrivada.VALOR);
                    String texto = seguranca.encrypt(new String(txt));
                    System.out.println("Texto criptografado: " + texto);
                    System.exit(0);
                } else if (args[0].contains("-d")) {
                    BasicTextEncryptor seguranca = new BasicTextEncryptor();
                    seguranca.setPassword(ChavePrivada.VALOR);
                    String texto = seguranca.decrypt(new String(txt));
                    System.out.println("Texto descriptografado: " + texto);
                    System.exit(0);
                }
            }
        } else if (args[0].equalsIgnoreCase("-cacerts")) {
            Cacerts.gerar();
            System.exit(0);
        }

        System.out.println("Falta informar a [OPCAO...] [ARQUIVO | TEXTO] de utilizacao.");
        System.out.println("[OPCAO]");
        System.out.println("\t-a = arquivo, realiza a acao em um arquivo.");
        System.out.println("\t-t = texto, realiza a acao em um texto informado.");
        System.out.println("\t-c = criptografar, realiza a criptografia de um arquivo ou texto.");
        System.out.println("\t-d = descriptografar, realiza a descriptografia de um arquivo ou texto.");
        System.out.println("\t-cacerts = cacerts, para gerar o arquivo NFeCacerts de todos os estados.");
        System.out.println("Exemplo:\n\tjava -jar AdmPDV -a-c auxiliar.properties");
    }

    /**
     * Metodo que compara a senha informada com a senha de ADM salva.
     *
     * @param senha a senha digitada no console.
     *
     * @return true se for igual , false se nao for igual.
     */
    private static boolean validarSenha(String senha) {
        ConfigurablePasswordEncryptor sha = new ConfigurablePasswordEncryptor();
        sha.setAlgorithm("SHA-1");
        sha.setPlainDigest(true);
        sha.setStringOutputType("hexadecimal");
        senha = sha.encryptPassword(senha);
        // crie uma senha com SHA1 e coloque aqui para usar quando o utilizar o aplicativo
        return senha.equals("852A0B1988AE0DF88DA242C7277360FBF5639A8E");
    }
}
