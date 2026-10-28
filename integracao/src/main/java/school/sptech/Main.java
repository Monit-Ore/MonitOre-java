package school.sptech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.client.S3Provider;
import school.sptech.config.Ambiente;
import school.sptech.config.Jira;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static Boolean acimaConstante(List<Leitura> leituras, String componente, Double limite) {
        // Função para verificar a constância dos valores de captura, se essa constância for quebrada (valor < limite),
        // a função retorna False, pois os valores não estão todos acima do limite.
        for (Leitura leitura : leituras) {
            Double valor = leitura.getValor(componente);
            if (valor < limite) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // Dados de acesso ao Jira Cloud
        // ATENÇÃO: USE VARIÁVEIS DE AMBIENTE!


        Jira jira = new Jira(Ambiente.get("JIRA_URL"),
                        Ambiente.get("JIRA_EMAIL"),
                        Ambiente.get("JIRA_TOKEN"));


        // Banco de Dados
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();
//
//        List<ParametroEtl> parametros = connection.query("""
//        SELECT p.hostname, c.nome AS componente, l.atencao, l.critico
//        FROM limite_alerta l
//        JOIN PC_Industrial p ON p.id_pc_industrial = l.fk_pc_industrial
//        JOIN componente c ON c.id_componente = l.fk_componente
//        """, new BeanPropertyRowMapper<>(ParametroEtl.class));

        String sql = """
        SELECT p.hostname, c.nome AS componente, l.atencao, l.critico, t.nome AS torre
        FROM limite_alerta l
        JOIN PC_Industrial p ON p.id_pc_industrial = l.fk_pc_industrial
        JOIN componente c ON c.id_componente = l.fk_componente
        JOIN torre t ON t.id_torre = p.fk_torre
        WHERE p.hostname = ?
        """;

        System.out.println("Iniciando conexão com S3");
        // Instanciando o cliente S3 via S3Provider
        S3Client s3Client = new S3Provider().getS3Client();
        LeitorSilver leitor = new LeitorSilver();

        List<Leitura> leituras = leitor.importarLeituras(
                s3Client, "S3-BUCKET", "S3_KEY");

        int quantidade = 6;

        List<String> iPcs = new ArrayList<>();
        for (Leitura leitura : leituras) {
            if (!iPcs.contains(leitura.getHostname())) {
                iPcs.add(leitura.getHostname());
            }
        }

        System.out.println("Iniciando laço de repetição");

        for (String pc : iPcs) {
            List<Leitura> ultimas = new ArrayList<>();
            for (int i = leituras.size() - 1; i >= 0 && ultimas.size() < quantidade; i--) {
                if (leituras.get(i).getHostname().equals(pc)) {
                    ultimas.add(leituras.get(i));
                }
            }
            List<ParametroEtl> limitesPc = connection.query(sql,
                    new BeanPropertyRowMapper<>(ParametroEtl.class), "PC-VALE-01");
//            leitura.getHostname()
            System.out.println("Laço para limitesPc");
            for (ParametroEtl limite : limitesPc) {
                System.out.println("Laço para limite de cada componente");
                String nivel = null;

                if (acimaConstante(leituras, limite.getComponente(), limite.getCritico())) {
                    nivel = "CRÍTICO";
                } else if (acimaConstante(leituras, limite.getComponente(), 40.0)) {
                    nivel = "ATENÇÃO";
                }

                if (nivel == null) {
                    continue;
                }

                System.out.println("Abrindo chamado");
                String summary = "ALERTA: [" + nivel + "]" +
                        " *Horário:* " + leituras.getLast().getDtHrFormatada() +
                        " - *Componente:* " + limite.getComponente().toUpperCase() +
                        " (" + leituras.getFirst().getValor(limite.getComponente()) + "%)" +
                        " - *PC Industrial:* " + leituras.getFirst().getHostname() +
                        " - *Torre:* " + limite.getTorre();

                try {
                    // Cria uma issue no backlog do projeto informado
                    String response = jira.createIssue(
                            "KAN", // Key do projeto, presente na URL do seu site
                            summary, // Nome da issue
                            "Incident" // Tipo da issue: "Task", "Bug", "Story", etc.
                    );

                    // Exibe o JSON de resposta com os dados da issue criada
                    System.out.println(response);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    }
                }
        }
    
    }
}
