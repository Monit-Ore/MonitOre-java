package school.sptech.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Ambiente {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .load();

    public static String get(String chave) {
        return dotenv.get(chave);
    }
}
