package bottelegram;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Levelito extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        return "512586030:AAEaf0i6s_KglDeyYuASxAeoqfptgMhjOko";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            //Recojo el mensaje de texto y sus caracteristicas
            String mensajeRecibido = update.getMessage().getText();//texto
            String usuario = update.getMessage().getFrom().getUserName();//user
            long chatID = update.getMessage().getChatId();//id del chat

            //Creo un mensaje para enviar
            SendMessage mensaje = new SendMessage();
            mensaje.setChatId(chatID);//le aseigno el char

            if (update.getMessage().isCommand()) {//Control de comandos

                String comando = update.getMessage().getText();
                switch (comando) {
                    case "/ayudaquedadas":
                        mensaje.setText("Crear quedada: en SITIO, para DIA a las");
                        break;
                    case "/ayudaencuestas":
                        mensaje.setText("Crear encuesta: DIA1, DIA2, DIA3...|| Crear encuesta HORA1, HORA2, HORA3...");
                        break;
                    case "/outofcontext":
                        Random rand= new Random();
                        int opcion=rand.nextInt(4);
                        switch (opcion){
                            case 0: 
                                mensaje.setText("Y @Hirobyte dijo \"Toma anda, cómete esto\"");
                                break;
                            case 1:
                                mensaje.setText("Y @Hirobyte dijo \"[..] tienes todo gigante. Tienes plátanos gigantes[...]\"");
                                break;
                            case 2:
                                mensaje.setText("Y @Senixirabix dijo \"Tú come Bedy, tú come...\"");
                                break;
                            case 3:
                                mensaje.setText("Y @Bedelin dijo \"Vamos a hacerlo, Jakub\"");
                                break;
                            case 4:
                                mensaje.setText("Y @Senixirabix dijo \"Lo mejor es al natural\"");
                                break;
                        }
                }
                //CREACION DE QUEDADAS
            } else if (mensajeRecibido.indexOf("Crear quedada") == 0) {//Creación de quedadas
                String datos = mensajeRecibido.split(": en")[1];
                String[] datosSeparados = datos.split(", para");
                mensaje.setText("¡Gente! Quedamos en el " + datosSeparados[0] + " el día" + datosSeparados[1]);

                //Hago las opciones del mensaje
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
                List<List<InlineKeyboardButton>> filas = new ArrayList<>();
                List<InlineKeyboardButton> linea = new ArrayList<>();

                linea.add(new InlineKeyboardButton().setText("Allé voy").setCallbackData("ir @" + usuario));

                filas.add(linea);
                markup.setKeyboard(filas);

                mensaje.setReplyMarkup(markup);

                //CREACION DE ENCUESTAS
            } else if (mensajeRecibido.indexOf("Crear encuesta") == 0) {
                String datos = mensajeRecibido.split(": ")[1];
                String[] opciones = datos.split(", ");

                //Hago las opciones del mensaje
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
                List<List<InlineKeyboardButton>> filas = new ArrayList<>();
                List<InlineKeyboardButton> linea = new ArrayList<>();

                String textoMensaje = "¡Encuesta va! Elegid sabiamente. Ya han votado:";
                for (String s : opciones) {
                    linea.add(new InlineKeyboardButton().setText(s).setCallbackData("voto @" + usuario + "," + s));
                    textoMensaje = textoMensaje + "\r\n-" + s + ": 0";
                }
                mensaje.setText(textoMensaje);
                filas.add(linea);
                markup.setKeyboard(filas);
                mensaje.setReplyMarkup(markup);

            } else if (isGay(mensajeRecibido)) {//Jakub gay
                mensaje.setText("@" + usuario + ", ¿estás seguro? No apostaría por ello.");
            }

            try {
                execute(mensaje);//envia el mensaje
            } catch (TelegramApiException ex) {
                System.out.println(ex);
            }
        } else if (update.hasCallbackQuery()) {

            String callData = update.getCallbackQuery().getData();
            String user = callData.split("@")[1];
            long chatID = update.getCallbackQuery().getMessage().getChatId();
            int mensajeID = update.getCallbackQuery().getMessage().getMessageId();

            EditMessageText mensajeEditado = new EditMessageText();
            mensajeEditado.setChatId(chatID);
            mensajeEditado.setMessageId(mensajeID);

            if (update.getCallbackQuery().getData().contains("ir")) {
                if (update.getCallbackQuery().getMessage().getText().contains(user)) {
                    String mensajeNuevo = update.getCallbackQuery().getMessage().getText();
                    mensajeNuevo = mensajeNuevo.replaceAll("⭕ @" + user, "");
                    mensajeEditado.setText(mensajeNuevo);
                } else {
                    mensajeEditado.setText(update.getCallbackQuery().getMessage().getText() + "\r\n⭕ @" + user);

                }
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
                List<List<InlineKeyboardButton>> filas = new ArrayList<>();
                List<InlineKeyboardButton> linea = new ArrayList<>();

                linea.add(new InlineKeyboardButton().setText("Allé voy").setCallbackData("ir @" + user));

                filas.add(linea);
                markup.setKeyboard(filas);
                mensajeEditado.setReplyMarkup(markup);

            } else if (update.getCallbackQuery().getData().contains("voto")) {
                String votante = user.split(",")[0];
                String texto = update.getCallbackQuery().getMessage().getText().split("-")[0];
                if (!texto.contains(votante)) {
                    texto = texto + "⭕ @" + votante;
                    String[] opciones = update.getCallbackQuery().getMessage().getText().split("-");
                    ArrayList<Integer> votos = new ArrayList<>();

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
                    List<List<InlineKeyboardButton>> filas = new ArrayList<>();
                    List<InlineKeyboardButton> linea = new ArrayList<>();

                    for (int i = 1; i < opciones.length; i++) {
                        String opcionDeTurno = opciones[i].split(": ")[0];
                        Integer votoSacado = Integer.parseInt(String.valueOf(opciones[i].split(": ")[1].charAt(0)));
                        System.out.println(opciones[i]);
                        if (user.split(",")[1].equals(opcionDeTurno)) {
                            votoSacado++;
                        }
                        votos.add(votoSacado);
                        texto = texto + "\r\n" + opcionDeTurno + ": " + votos.get(i - 1);
                        linea.add(new InlineKeyboardButton().setText(opcionDeTurno).setCallbackData("voto @" + user + "," + opcionDeTurno));
                    }

                    mensajeEditado.setText(texto);
                    filas.add(linea);
                    markup.setKeyboard(filas);
                    mensajeEditado.setReplyMarkup(markup);
                }
            }
            try {
                execute(mensajeEditado);
            } catch (TelegramApiException ex) {
                System.out.println(ex);
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "LevelitoBot";
    }

    public boolean isGay(String texto) {
        String textoMin = texto.toLowerCase();
        String gay = "sorry, no gay";
        String gay2 = "sorry no gay";
        String gay3 = "sory no gay";
        String gay4 = "sorry no gai";
        String gay5 = "sory no gai";

        return textoMin.equals(gay) || textoMin.equals(gay2) || textoMin.equals(gay3) || textoMin.equals(gay4) || textoMin.equals(gay5);
    }
}
