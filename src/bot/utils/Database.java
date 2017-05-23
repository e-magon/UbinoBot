package bot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

public class Database {
    public static void aggiornaDB(Update update, String fileDatabase) {
        try {
            //Legge informazioni del mittente
            User mittente = update.getMessage().getFrom();
            
            String chatid = "" + update.getMessage().getChatId();
            String userid = "" + mittente.getId();
            String nome = mittente.getFirstName();
            String cognome = mittente.getLastName();
            String username = mittente.getUserName();

            JSONParser parser = new JSONParser();
            //Inizializza a stringa json vuota
            String stringaJSON = "[]";
            //Legge il database esistente
            try {
                stringaJSON = readFile(fileDatabase);
            } catch (Exception e) {}
            
            //Fa il parsing per renderla una lista json
            JSONArray lista = (JSONArray) parser.parse(stringaJSON);
                        
            //Controlla che la entry con chatid non esista già
            boolean duplicato = false;
            for(int k=0; k<lista.size(); k++) {
                    JSONObject temp = (JSONObject) lista.get(k);
                    if (chatid.equals((String) temp.get("chatid"))) {
                        duplicato = true;
                    }
            }
            
            //Se non esiste, lo salva
            if(! duplicato) {
                JSONObject tmp = new JSONObject();

                tmp.put("username", username);
                tmp.put("userid", userid);
                tmp.put("chatid", chatid);
                tmp.put("nome", nome);
                tmp.put("cognome", cognome);

                lista.add(tmp);
                
                saveFile(fileDatabase, lista.toJSONString());
            }
        } catch (Exception e) {
            System.out.println("Errore aggiornamento db: " + e);
        }
    }
    
    public static String[] getDestinatari(String fileDatabase) {
        //Usato per ricavare l'array distinto di chatid conosciute
        String destinatari[] = null;
        //Inizializza a stringa json vuota
        String stringaJSON = "[]";
        JSONParser parser = new JSONParser();
        
        //Aggiunge ogni chatid salvato all'array inviato al chiamante
        try {
            //Cerca di leggere il file database
            stringaJSON = readFile(fileDatabase);
            JSONArray listaJSON = (JSONArray) parser.parse(stringaJSON);
            
            //Dato che le chatid non sono ripetute nel database, la grandezza è
            //sicuramente la stessa nell'elenco distinto di chatid da inviare
            destinatari = new String[listaJSON.size()];
            
            for(int k=0; k<listaJSON.size(); k++) {
                //Per ogni elemento del db, fa la copia della chatid nell'array da inviare
                JSONObject temp = (JSONObject) listaJSON.get(k);
                destinatari[k] = (String) temp.get("chatid");
            }
        } catch (Exception e) {
            System.out.println("Errore get destinatari:" + e);
        }
        
        return destinatari;
    }
    
    public static String salvaMemoria(String nome, String descrizione, String fileMemorie) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        //Inizializza a stringa json vuota
        String stringaJSON = "[]";
        //Legge il database esistente
        try {
            stringaJSON = readFile(fileMemorie);
        } catch (Exception e) {}

        //Fa il parsing per renderla una lista json
        JSONArray lista = (JSONArray) parser.parse(stringaJSON);
        
        //Controlla se esiste già una entry con quel nome. In quel caso, lo
        //sostituisce ma ritorna la descrizione originale.
        String originale = "";
        for(int k=0; k<lista.size(); k++) {
            JSONObject temp = (JSONObject) lista.get(k);
            if (nome.equals((String) temp.get("nome"))) {
                originale = (String) temp.get("descrizione");
                lista.remove(k);
            }
        }
        
        //Aggiunge l'entry
        JSONObject tmp = new JSONObject();
        tmp.put("nome", nome);
        tmp.put("descrizione", descrizione);
        lista.add(tmp);

        //Salva il file
        saveFile(fileMemorie, lista.toJSONString());
        //Ritorna il valore originale della entry
        return originale;
    }
    
    public static String getElencoMemorie(String fileMemorie) throws ParseException {
        String memorie = "";
        JSONParser parser = new JSONParser();
        
        //Inizializza a stringa json vuota
        String stringaJSON = "[]";
        //Legge il database esistente
        try {
            stringaJSON = readFile(fileMemorie);
        } catch (Exception e) {}
        
        //Fa il parsing per renderla una lista json
        JSONArray lista = (JSONArray) parser.parse(stringaJSON);
        
        //Prende tutte le memorie
        for(int k=0; k<lista.size(); k++) {
            JSONObject temp = (JSONObject) lista.get(k);
            memorie += temp.get("nome") +"\n";
        }
        
        return memorie;
    }
    
    public static String getMemoria(String fileMemorie, String nome) throws ParseException {
        String memoria = "";
        JSONParser parser = new JSONParser();
        
        //Inizializza a stringa json vuota
        String stringaJSON = "[]";
        //Legge il database esistente
        try {
            stringaJSON = readFile(fileMemorie);
        } catch (Exception e) {
            System.out.println("Errore nella lettura file memorie " + e);
        }
        
        //Fa il parsing per renderla una lista json
        JSONArray lista = (JSONArray) parser.parse(stringaJSON);
        
        //Cerca la memoria richiesta
        for(int k=0; k<lista.size(); k++) {
            JSONObject temp = (JSONObject) lista.get(k);
            if(temp.get("nome").equals(nome))
                memoria = "" + temp.get("descrizione");
        }
        
        return memoria;
    }
    
    public static String delMemoria(String fileMemorie, String nome) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        //Inizializza a stringa json vuota
        String stringaJSON = "[]";
        //Legge il database esistente
        try {
            stringaJSON = readFile(fileMemorie);
        } catch (Exception e) {}

        //Fa il parsing per renderla una lista json
        JSONArray lista = (JSONArray) parser.parse(stringaJSON);
        
        //Controlla se esiste una entry con quel nome. In quel caso, lo
        //elimina e ritorna la descrizione originale.
        String originale = "";
        for(int k=0; k<lista.size(); k++) {
            JSONObject temp = (JSONObject) lista.get(k);
            if (nome.equals((String) temp.get("nome"))) {
                originale = (String) temp.get("descrizione");
                lista.remove(k);
            }
        }
        
        //Salva il file
        saveFile(fileMemorie, lista.toJSONString());
        return originale;
    }
    
    private static String readFile(String file) throws FileNotFoundException, IOException {
        //Legge il file di testo con il nome passato. Mantiene gli a capo e tabulazioni
        BufferedReader reader;
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        reader = new BufferedReader(new FileReader (file));
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }
    
    private static void saveFile(String file, String stringaJSON) throws IOException {
        //Scrive il file specificato con la stringa specificata
        //createNewFile utilizzato nel caso in cui esistesse già per ripulirlo, dato che non lo fa write
        new File(file).createNewFile();
        PrintWriter writer = new PrintWriter(file);
        writer.print(stringaJSON);
        writer.close();
    }
}