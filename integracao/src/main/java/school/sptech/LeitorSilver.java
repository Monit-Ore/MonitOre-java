package school.sptech;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

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

    public List<Leitura> importarLeituras(S3Client s3Client, String bucket, String key) {
        List<Leitura> leituras = new ArrayList<>();

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try (
                InputStream inputStream = s3Client.getObject(request, ResponseTransformer.toInputStream());
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader)
        ) {
            String linha;
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            reader.readLine(); // pular cabeçalho
            while ((linha = reader.readLine()) != null) {
                String[] colunas = linha.split(";");
                if (linha.isBlank() || colunas.length != 14) {
                    continue;
                }
                String stringDtHr = colunas[2] + " " + colunas[3];
                LocalDateTime dtHrConversao = LocalDateTime.parse(stringDtHr, formatador);
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
