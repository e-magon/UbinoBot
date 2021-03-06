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
            System.out.println("Caricamento del bot @" + bot.getBotUsername() + "...");
            botsApi.registerBot(bot);
            System.out.println("Bot avviato! @" + bot.getBotUsername());
            //Notifica me che è up
            bot.onStart();
        } catch (TelegramApiException e) {
            System.out.println("Errore avvio bot: " + e);
        }
    }
}