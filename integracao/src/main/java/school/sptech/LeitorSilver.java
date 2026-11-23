package school.sptech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LeitorSilver {

    private static final Logger log = LoggerFactory.getLogger(LeitorSilver.class);

    public List<Leitura> importarLeituras(S3Client s3Client, String bucket) {
        ListObjectsV2Request requestLista = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(requestLista);

        // A data chega como String, é preciso converter para DateTime
        DateTimeFormatter formatadorArq = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        DateTimeFormatter formatadorCSV = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        // Variável que ficará guardado o "nome" do CSV mais recente com base na data e no horário
        String keyMaisRecente = null;

        List<Leitura> leituras = new ArrayList<>();

        LocalDateTime dataMaisRecente = null;

        for (S3Object content : response.contents()) {
            String nomeArquivo = content.key();
            nomeArquivo = nomeArquivo.substring(nomeArquivo.lastIndexOf("/") + 1);

            if (!nomeArquivo.endsWith("_HISTORICO_HARDWARE.csv")) {
                continue;
            }
            String arquivoData = nomeArquivo.substring(0, nomeArquivo.indexOf("_HISTORICO_HARDWARE.csv"));

            LocalDateTime dtHrArquivo = LocalDateTime.parse(arquivoData, formatadorArq);

            if (dataMaisRecente == null || dtHrArquivo.isAfter(dataMaisRecente)) {
                dataMaisRecente = dtHrArquivo;
                keyMaisRecente = content.key();
                System.out.println("CSV mais recente: " + content.key());
            }
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(keyMaisRecente)
                .build();

        try (
                InputStream inputStream = s3Client.getObject(request, ResponseTransformer.toInputStream());
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader)
        ) {
            String linha;
            reader.readLine(); // pular cabeçalho
            while ((linha = reader.readLine()) != null) {
                String[] colunas = linha.split(";");
                if (linha.isBlank() || colunas.length != 14) {
                    continue;
                }
                String stringDtHr = colunas[2] + " " + colunas[3];
                LocalDateTime dtHrConversao = LocalDateTime.parse(stringDtHr, formatadorCSV);
                Leitura leitura = new Leitura(
                        colunas[1],
                        Double.valueOf(colunas[4]),
                        Double.valueOf(colunas[5]),
                        Double.valueOf(colunas[7]),
                        dtHrConversao
                        );
                leituras.add(leitura);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo do Silver");
        }
        return leituras;
    }
}
