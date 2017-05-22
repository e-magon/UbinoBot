package bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MainClass {
    public static void main(String[] args) {
        // Inizializza il context delle API (richiesto)
        ApiContextInitializer.init();

        // Instanzia le API dei bot di Telegram (richiesto)
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Avvia il bot
        try {
            UbinoBot bot = new UbinoBot();
            botsApi.registerBot(bot);
            System.out.println("Bot avviato! @" + bot.getBotUsername());
        } catch (TelegramApiException e) {
            System.out.println("Errore avvio bot: " + e);
        }
    }
}