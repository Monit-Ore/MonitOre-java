// Trata erros relacionados à criação do arquivo de log
import java.io.IOException;

// Permite utilizar data e hora
import java.time.LocalDateTime;

// Permite definir o formato da data e hora
import java.time.format.DateTimeFormatter;

// Permite utilizar a formatação brasileira nos números
import java.util.Locale;

// Gera valores aleatórios para simular as métricas
import java.util.Random;

// Permite mostrar os logs no terminal
import java.util.logging.ConsoleHandler;

// Permite salvar os logs em um arquivo
import java.util.logging.FileHandler;

// Permite criar um formato personalizado para os logs
import java.util.logging.Formatter;

// Representa os destinos dos logs, como terminal e arquivo
import java.util.logging.Handler;

// Representa os níveis de gravidade dos logs
import java.util.logging.Level;

// Contém as informações de um registro de log
import java.util.logging.LogRecord;

// Classe principal utilizada para gerar logs
import java.util.logging.Logger;


public class AgenteMonitorOre {

    // Objeto responsável por registrar os logs
    private static final Logger logger =
            Logger.getLogger(AgenteMonitorOre.class.getName());

    // Objeto usado para gerar métricas simuladas
    private static final Random random = new Random();

    // Configuração usada para mostrar números no formato brasileiro
    private static final Locale LOCAL_BR =
            Locale.forLanguageTag("pt-BR");

    // Formato usado para mostrar a data e a hora
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Identificadores simulados da torre e do servidor
    private static final String TORRE = "TORRE-001";
    private static final String SERVIDOR = "SERVIDOR-001";


    // CPU entre 70% e 79,99% entra no nível de atenção
    private static final double ATENCAO_CPU = 70.0;

    // CPU igual ou acima de 80% entra no nível crítico
    private static final double CRITICO_CPU = 80.0;


    // Memória entre 70% e 79,99% entra no nível de atenção
    private static final double ATENCAO_MEMORIA = 70.0;

    // Memória igual ou acima de 80% entra no nível crítico
    private static final double CRITICO_MEMORIA = 80.0;


    // Disco entre 80% e 89,99% entra no nível de atenção
    private static final double ATENCAO_DISCO = 80.0;

    // Disco igual ou acima de 90% entra no nível crítico
    private static final double CRITICO_DISCO = 90.0;


    // Temperatura entre 70°C e 79,99°C entra no nível de atenção
    private static final double ATENCAO_TEMPERATURA = 70.0;

    // Temperatura igual ou acima de 80°C entra no nível crítico
    private static final double CRITICO_TEMPERATURA = 80.0;


    /*
     Armazena o nível anterior de cada métrica.

     Permite saber se uma métrica entrou no nível crítico,
     permaneceu crítica ou voltou ao normal.
     */
    private static Nivel nivelAnteriorCpu;
    private static Nivel nivelAnteriorMemoria;
    private static Nivel nivelAnteriorDisco;
    private static Nivel nivelAnteriorTemperatura;


    public static void main(String[] args) {

        // Configura os logs no terminal e no arquivo
        configurarLogs();

        // Registra as informações iniciais do agente
        logger.info("============================================================");
        logger.info("MONITOR ORE - AGENTE DE MONITORAMENTO");
        logger.info("Torre: " + TORRE + " | Servidor: " + SERVIDOR);
        logger.info("Agente iniciado com sucesso");
        logger.info("============================================================");

        try {

            // Verifica se os identificadores foram preenchidos
            validarConfiguracao();

            // Variável usada para contar os ciclos
            int ciclo = 1;

            /*
             O while(true) cria uma repetição infinita.

             O monitoramento continuará funcionando até que
             o programa seja interrompido pelo usuário.
             */
            while (true) {

                logger.info("");

                // Mostra o número do ciclo atual
                logger.info(
                        String.format(
                                "==================== CICLO %02d ====================",
                                ciclo
                        )
                );

                // Simula a coleta das métricas
                MetricaServidor metrica = coletarMetricas();


                // Classifica o nível atual da CPU
                Nivel nivelCpu = calcularNivel(
                        metrica.getUsoCpu(),
                        ATENCAO_CPU,
                        CRITICO_CPU
                );

                // Classifica o nível atual da memória
                Nivel nivelMemoria = calcularNivel(
                        metrica.getUsoMemoria(),
                        ATENCAO_MEMORIA,
                        CRITICO_MEMORIA
                );

                // Classifica o nível atual do disco
                Nivel nivelDisco = calcularNivel(
                        metrica.getUsoDisco(),
                        ATENCAO_DISCO,
                        CRITICO_DISCO
                );

                // Classifica o nível atual da temperatura
                Nivel nivelTemperatura = calcularNivel(
                        metrica.getTemperatura(),
                        ATENCAO_TEMPERATURA,
                        CRITICO_TEMPERATURA
                );


                // Calcula o estado geral usando a pior métrica
                Nivel estadoGeral = calcularEstadoGeral(
                        nivelCpu,
                        nivelMemoria,
                        nivelDisco,
                        nivelTemperatura
                );


                // Exibe o resumo completo das métricas
                exibirResumo(
                        metrica,
                        nivelCpu,
                        nivelMemoria,
                        nivelDisco,
                        nivelTemperatura,
                        estadoGeral
                );


                // Registra alertas e recuperações
                analisarEventos(
                        metrica,
                        nivelCpu,
                        nivelMemoria,
                        nivelDisco,
                        nivelTemperatura
                );


                // Simula o envio das métricas para a API
                enviarMetricasParaApi(
                        metrica,
                        estadoGeral
                );


                /*
                 Salva os níveis atuais.

                 No próximo ciclo, esses valores serão
                 utilizados como níveis anteriores.
                 */
                nivelAnteriorCpu = nivelCpu;
                nivelAnteriorMemoria = nivelMemoria;
                nivelAnteriorDisco = nivelDisco;
                nivelAnteriorTemperatura = nivelTemperatura;


                // Registra que o ciclo atual terminou
                logger.info(
                        String.format(
                                "Ciclo %02d finalizado",
                                ciclo
                        )
                );

                // Aumenta o número do próximo ciclo
                ciclo++;

                // Espera dez segundos antes da próxima coleta
                Thread.sleep(10000);
            }

        } catch (InterruptedException erro) {

            // Informa ao Java que a execução foi interrompida
            Thread.currentThread().interrupt();

            // Registra a interrupção
            logger.severe(
                    criarIdentificacao()
                            + " | O monitoramento foi interrompido"
            );

        } catch (Exception erro) {

            // Registra outros erros que possam acontecer
            logger.severe(
                    criarIdentificacao()
                            + " | Erro durante o monitoramento: "
                            + erro.getMessage()
            );

        } finally {

            // É executado quando o agente termina
            logger.info("");
            logger.info("Agente Monitor Ore finalizado");
        }
    }


    /*
     Define os três níveis possíveis de uma métrica.
     */
    private enum Nivel {

        NORMAL("NORMAL"),
        ATENCAO("ATENÇÃO"),
        CRITICO("CRÍTICO");

        // Texto mostrado no terminal
        private final String descricao;

        // Construtor usado para definir a descrição
        Nivel(String descricao) {
            this.descricao = descricao;
        }

        // Retorna a descrição do nível
        public String getDescricao() {
            return descricao;
        }
    }


    /*
     Configura o formato dos logs apresentados no terminal
     e salvos no arquivo monitor-ore.log.
     */
    private static void configurarLogs() {

        try {

            /*
             Desativa o formato padrão do Java.

             Isso remove informações repetitivas, como
             o nome da classe e o nome do método.
             */
            logger.setUseParentHandlers(false);

            // Remove configurações anteriores do Logger
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }


            /*
             Cria o formato personalizado dos logs.

             Exemplo:
             [05/09/2026 22:30:00] [INFO] Mensagem
             */
            Formatter formatoPersonalizado = new Formatter() {

                @Override
                public String format(LogRecord registro) {

                    // Obtém a data e a hora atuais
                    String dataHora = LocalDateTime.now()
                            .format(FORMATO_DATA);

                    // Guarda o nível que será exibido
                    String nivel;

                    // Transforma SEVERE em CRÍTICO
                    if (registro.getLevel() == Level.SEVERE) {
                        nivel = "CRÍTICO";

                        // Transforma WARNING em ATENÇÃO
                    } else if (registro.getLevel() == Level.WARNING) {
                        nivel = "ATENÇÃO";

                        // Os demais registros aparecem como INFO
                    } else {
                        nivel = "INFO";
                    }

                    // Retorna o registro no formato personalizado
                    return String.format(
                            "[%s] [%-7s] %s%n",
                            dataHora,
                            nivel,
                            registro.getMessage()
                    );
                }
            };


            // Cria a configuração dos logs do terminal
            ConsoleHandler console = new ConsoleHandler();

            // Aplica o formato personalizado ao terminal
            console.setFormatter(formatoPersonalizado);

            // Permite mostrar todos os níveis no terminal
            console.setLevel(Level.ALL);


            /*
             Cria ou abre o arquivo monitor-ore.log.

             O true adiciona os novos registros sem
             apagar os registros anteriores.
             */
            FileHandler arquivoLog = new FileHandler(
                    "monitor-ore.log",
                    true
            );

            // Aplica o formato personalizado ao arquivo
            arquivoLog.setFormatter(formatoPersonalizado);

            // Permite salvar todos os níveis no arquivo
            arquivoLog.setLevel(Level.ALL);


            // Adiciona a saída do terminal ao Logger
            logger.addHandler(console);

            // Adiciona a saída do arquivo ao Logger
            logger.addHandler(arquivoLog);

            // Permite que o Logger trabalhe com todos os níveis
            logger.setLevel(Level.ALL);

        } catch (IOException erro) {

            /*
             System.err é usado porque o Logger pode não
             ter conseguido criar o arquivo de log.
             */
            System.err.println(
                    "Não foi possível criar o arquivo de log: "
                            + erro.getMessage()
            );
        }
    }


    /*
     Verifica se os identificadores obrigatórios foram preenchidos.
     */
    private static void validarConfiguracao() {

        // Verifica se o identificador da torre está vazio
        if (TORRE == null || TORRE.isBlank()) {

            throw new IllegalArgumentException(
                    "O identificador da torre não foi informado"
            );
        }

        // Verifica se o identificador do servidor está vazio
        if (SERVIDOR == null || SERVIDOR.isBlank()) {

            throw new IllegalArgumentException(
                    "O identificador do servidor não foi informado"
            );
        }
    }


    /*
     Gera valores aleatórios para simular a coleta das métricas.
     */
    private static MetricaServidor coletarMetricas() {

        /*
         nextDouble() gera um número entre 0 e 1.

         Multiplicando por 100, o número passa
         a ficar entre 0 e 100.
         */
        double usoCpu = random.nextDouble() * 100;
        double usoMemoria = random.nextDouble() * 100;
        double usoDisco = random.nextDouble() * 100;

        // Gera uma temperatura aleatória entre 30°C e 100°C
        double temperatura = 30 + random.nextDouble() * 70;


        /*
         Cria e retorna um objeto contendo os valores
         coletados e o horário da coleta.
         */
        return new MetricaServidor(
                usoCpu,
                usoMemoria,
                usoDisco,
                temperatura,
                LocalDateTime.now()
        );
    }


    /*
     Classifica uma métrica como NORMAL, ATENÇÃO ou CRÍTICO.
     */
    private static Nivel calcularNivel(
            double valor,
            double limiteAtencao,
            double limiteCritico
    ) {

        // Verifica primeiro se o valor está no nível crítico
        if (valor >= limiteCritico) {
            return Nivel.CRITICO;
        }

        // Verifica se o valor está no nível de atenção
        if (valor >= limiteAtencao) {
            return Nivel.ATENCAO;
        }

        // Se não atingiu nenhum limite, está normal
        return Nivel.NORMAL;
    }


    /*
     Define o estado geral do servidor utilizando
     a pior situação encontrada.
     */
    private static Nivel calcularEstadoGeral(
            Nivel nivelCpu,
            Nivel nivelMemoria,
            Nivel nivelDisco,
            Nivel nivelTemperatura
    ) {

        /*
         Se pelo menos uma métrica estiver crítica,
         o estado geral será CRÍTICO.
         */
        if (nivelCpu == Nivel.CRITICO
                || nivelMemoria == Nivel.CRITICO
                || nivelDisco == Nivel.CRITICO
                || nivelTemperatura == Nivel.CRITICO) {

            return Nivel.CRITICO;
        }

        /*
         Se nenhuma métrica estiver crítica, mas pelo menos
         uma estiver em atenção, o estado será ATENÇÃO.
         */
        if (nivelCpu == Nivel.ATENCAO
                || nivelMemoria == Nivel.ATENCAO
                || nivelDisco == Nivel.ATENCAO
                || nivelTemperatura == Nivel.ATENCAO) {

            return Nivel.ATENCAO;
        }

        // Se todas as métricas estiverem normais
        return Nivel.NORMAL;
    }


    /*
     Exibe todas as métricas coletadas de maneira organizada.
     */
    private static void exibirResumo(
            MetricaServidor metrica,
            Nivel nivelCpu,
            Nivel nivelMemoria,
            Nivel nivelDisco,
            Nivel nivelTemperatura,
            Nivel estadoGeral
    ) {

        /*
         String.format() permite inserir valores
         dentro de um texto formatado.

         %s representa um texto.
         %.2f representa um número com duas casas decimais.
         %% representa o símbolo de porcentagem.
         %n representa uma quebra de linha.
         */
        String resumo = String.format(
                LOCAL_BR,
                "%n"
                        + "Torre: %s | Servidor: %s%n"
                        + "Horário da coleta: %s%n"
                        + "CPU:         %6.2f%%  [%s]%n"
                        + "Memória:     %6.2f%%  [%s]%n"
                        + "Disco:       %6.2f%%  [%s]%n"
                        + "Temperatura: %6.2f°C  [%s]%n"
                        + "Estado geral: %s",
                TORRE,
                SERVIDOR,
                metrica.getDataHora().format(FORMATO_DATA),
                metrica.getUsoCpu(),
                nivelCpu.getDescricao(),
                metrica.getUsoMemoria(),
                nivelMemoria.getDescricao(),
                metrica.getUsoDisco(),
                nivelDisco.getDescricao(),
                metrica.getTemperatura(),
                nivelTemperatura.getDescricao(),
                estadoGeral.getDescricao()
        );

        // Registra o resumo no terminal e no arquivo
        logger.info(resumo);
    }


    /*
     Analisa as métricas para registrar
     os alertas e as recuperações.
     */
    private static void analisarEventos(
            MetricaServidor metrica,
            Nivel nivelCpu,
            Nivel nivelMemoria,
            Nivel nivelDisco,
            Nivel nivelTemperatura
    ) {

        // Analisa a situação da CPU
        registrarEvento(
                "CPU",
                metrica.getUsoCpu(),
                "%",
                CRITICO_CPU,
                nivelCpu,
                nivelAnteriorCpu
        );

        // Analisa a situação da memória
        registrarEvento(
                "Memória",
                metrica.getUsoMemoria(),
                "%",
                CRITICO_MEMORIA,
                nivelMemoria,
                nivelAnteriorMemoria
        );

        // Analisa a situação do disco
        registrarEvento(
                "Disco",
                metrica.getUsoDisco(),
                "%",
                CRITICO_DISCO,
                nivelDisco,
                nivelAnteriorDisco
        );

        // Analisa a situação da temperatura
        registrarEvento(
                "Temperatura",
                metrica.getTemperatura(),
                "°C",
                CRITICO_TEMPERATURA,
                nivelTemperatura,
                nivelAnteriorTemperatura
        );
    }


    /*
     Registra o alerta ou a recuperação
     de uma única métrica.
     */
    private static void registrarEvento(
            String componente,
            double valor,
            String unidade,
            double limiteCritico,
            Nivel nivelAtual,
            Nivel nivelAnterior
    ) {

        // Monta as informações principais do evento
        String dados = String.format(
                LOCAL_BR,
                "%s | Componente=%s | Valor=%.2f%s",
                criarIdentificacao(),
                componente,
                valor,
                unidade
        );


        // Verifica se a métrica está no nível crítico
        if (nivelAtual == Nivel.CRITICO) {

            // Verifica se ela já estava crítica no ciclo anterior
            if (nivelAnterior == Nivel.CRITICO) {

                logger.severe(
                        dados
                                + " | Permanece acima do limite crítico"
                                + formatarLimite(
                                limiteCritico,
                                unidade
                        )
                );

            } else {

                // Informa que a métrica acabou de ficar crítica
                logger.severe(
                        dados
                                + " | Entrou no nível crítico"
                                + formatarLimite(
                                limiteCritico,
                                unidade
                        )
                );
            }


            // Verifica se a métrica está no nível de atenção
        } else if (nivelAtual == Nivel.ATENCAO) {

            /*
             Se estava crítica e agora está em atenção,
             significa que melhorou, mas ainda não está normal.
             */
            if (nivelAnterior == Nivel.CRITICO) {

                logger.warning(
                        dados
                                + " | Saiu do nível crítico, "
                                + "mas ainda requer atenção"
                );

            } else {

                // Informa que a métrica está próxima do limite crítico
                logger.warning(
                        dados
                                + " | Próximo do limite crítico"
                                + formatarLimite(
                                limiteCritico,
                                unidade
                        )
                );
            }


        /*
         Se o valor atual está normal, mas anteriormente
         estava em atenção ou crítico, houve recuperação.
         */
        } else if (nivelAnterior == Nivel.CRITICO
                || nivelAnterior == Nivel.ATENCAO) {

            logger.info(
                    "RECUPERADO | "
                            + dados
                            + " | Métrica voltou ao nível normal"
            );
        }
    }


    /*
     Formata o valor limite mostrado nos alertas.
     */
    private static String formatarLimite(
            double limite,
            String unidade
    ) {

        return String.format(
                LOCAL_BR,
                " | Limite=%.2f%s",
                limite,
                unidade
        );
    }


    /*
     Cria a identificação da torre e do servidor.
     */
    private static String criarIdentificacao() {

        return "Torre=" + TORRE
                + " | Servidor=" + SERVIDOR;
    }


    /*
     Simula o envio das métricas para a API do Monitor Ore.
     */
    private static void enviarMetricasParaApi(
            MetricaServidor metrica,
            Nivel estadoGeral
    ) {

        // Por enquanto, considera que o envio sempre funciona
        boolean envioRealizado = true;

        if (envioRealizado) {

            // Registra o sucesso do envio
            logger.info(
                    "ENVIO | "
                            + criarIdentificacao()
                            + " | Estado=" + estadoGeral.getDescricao()
                            + " | Coleta="
                            + metrica.getDataHora().format(FORMATO_DATA)
                            + " | Métricas enviadas com sucesso"
            );

        } else {

            // Registra uma falha no envio
            logger.severe(
                    "ENVIO | "
                            + criarIdentificacao()
                            + " | Falha ao enviar métricas para a API"
            );
        }
    }


    /*
     Classe interna usada para armazenar
     os valores de uma coleta.
     */
    private static class MetricaServidor {

        // Percentual de utilização da CPU
        private final double usoCpu;

        // Percentual de utilização da memória
        private final double usoMemoria;

        // Percentual de utilização do disco
        private final double usoDisco;

        // Temperatura do equipamento em graus Celsius
        private final double temperatura;

        // Data e hora em que a coleta foi realizada
        private final LocalDateTime dataHora;


        // Construtor usado para criar uma nova coleta
        public MetricaServidor(
                double usoCpu,
                double usoMemoria,
                double usoDisco,
                double temperatura,
                LocalDateTime dataHora
        ) {
            this.usoCpu = usoCpu;
            this.usoMemoria = usoMemoria;
            this.usoDisco = usoDisco;
            this.temperatura = temperatura;
            this.dataHora = dataHora;
        }


        // Retorna o valor da CPU
        public double getUsoCpu() {
            return usoCpu;
        }

        // Retorna o valor da memória
        public double getUsoMemoria() {
            return usoMemoria;
        }

        // Retorna o valor do disco
        public double getUsoDisco() {
            return usoDisco;
        }

        // Retorna o valor da temperatura
        public double getTemperatura() {
            return temperatura;
        }

        // Retorna a data e a hora da coleta
        public LocalDateTime getDataHora() {
            return dataHora;
        }
    }
}