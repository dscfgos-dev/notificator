package com.uncledavecode.notificator.bot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.uncledavecode.notificator.config.BotConfiguration;
import com.uncledavecode.notificator.model.AccessRequest;
import com.uncledavecode.notificator.services.AccessRequestService;
import com.uncledavecode.notificator.services.UserAccountService;

import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NotificatorBot extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;
    private final AccessRequestService accessRequestService;
    private final UserAccountService userAccountService;

    public NotificatorBot(ApplicationContext ctx) {
        this.botConfiguration = ctx.getBean("botConfiguration", BotConfiguration.class);
        this.accessRequestService = ctx.getBean("accessRequestService", AccessRequestService.class);
        this.userAccountService = ctx.getBean("userAccountService", UserAccountService.class);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.getText() != null) {
                    if (message.getText().equalsIgnoreCase("/start")) {
                        this.sendMessage("Welcome back!!!", update.getMessage().getChatId());
                    } else if (message.getText().equalsIgnoreCase("/register")) {
                        AccessRequest request = this.getAccessRequest(message);
                        if (request.getStep() == 0) {
                            this.startUserRegister(request);
                        } else {
                            this.continueUserRegister(request);
                        }
                    } else {
                        AccessRequest request = this.accessRequestService.getByChatId(message.getChatId());
                        if (request != null) {
                            this.continueProcessingUser(request, update);
                        } else {
                            // TODO Show message for start register
                        }
                    }
                }
            } else if (update.hasCallbackQuery()) {
                this.processCallBack(update);
            }
        }

    }

    private void processCallBack(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {

            Long chat_id = update.getCallbackQuery().getMessage().getChatId();

            AccessRequest request = accessRequestService.getByChatId(chat_id);
            if (request != null) {
                long message_id = update.getCallbackQuery().getMessage().getMessageId();
                String response = update.getCallbackQuery().getData();
                String answer = response.equalsIgnoreCase("yes")
                        ? "Process Completed\\!\nUser account is pending for approval"
                        : "Registration canceled\\!";
                EditMessageText new_message = new EditMessageText();
                new_message.setChatId(chat_id.toString());
                new_message.setMessageId(Math.toIntExact(message_id));
                new_message.setText(answer);
                new_message.setParseMode(ParseMode.MARKDOWNV2);

                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void continueProcessingUser(AccessRequest request, Update update) {
        if (request != null && update != null) {
            if (request.getStep() >= 1 && request.getStep() <= 3) {
                switch (request.getStep()) {
                    case 1:
                        request.setEmail(update.getMessage().getText());
                        break;
                    case 2:
                        request.setName(update.getMessage().getText());
                        break;
                    case 3:
                        request.setLastname(update.getMessage().getText());
                        break;

                }
                request.setStep(request.getStep() + 1);
                request = this.accessRequestService.updateAccessRequest(request);
            }

            this.showMessageFromStep(request);
        }
    }

    private void continueUserRegister(AccessRequest request) {
        sendMessage("We continue the user registration.!!!", request.getChatId());

        this.showMessageFromStep(request);
    }

    private void startUserRegister(AccessRequest request) {
        sendMessage("Starting user registration.!!!", request.getChatId());
        request.setStep(1);
        request.setRequestDate(LocalDateTime.now());
        this.accessRequestService.updateAccessRequest(request);

        this.showMessageFromStep(request);
    }

    private void showMessageFromStep(AccessRequest request) {
        switch (request.getStep()) {
            case 1:
                sendParseMessage("Step 1\nEnter an Email\\.", request.getChatId(), ParseMode.MARKDOWNV2);
                break;
            case 2:
                sendParseMessage("Step 2\nEnter Name\\.", request.getChatId(), ParseMode.MARKDOWNV2);
                break;
            case 3:
                sendParseMessage("Step 2\nEnter Last Name\\.", request.getChatId(), ParseMode.MARKDOWNV2);
                break;
            case 4:
                sendMessage(this.getConfirmMessage(request));
                break;
        }
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getUsername();
    }

    @Override
    public String getBotToken() {
        return this.botConfiguration.getToken();
    }

    private AccessRequest getAccessRequest(Message message) {
        AccessRequest result = this.accessRequestService.getByChatId(message.getChatId());
        if (result == null) {
            result = new AccessRequest();
            result.setStep(0);
            result.setLogid(message.getFrom().getUserName());
            result.setChatId(message.getChatId());
        }

        return result;
    }

    private void sendParseMessage(String text, Long chatId, String parseMode) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setParseMode(parseMode);
            sendMessage.setText(text);
            sendMessage.setChatId(chatId.toString());
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage getConfirmMessage(AccessRequest request) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Final Step\nCreate User Account?");
        sendMessage.setChatId(request.getChatId().toString());

        InlineKeyboardButton btnYes = new InlineKeyboardButton();
        btnYes.setText("Yes");
        btnYes.setCallbackData("yes");

        InlineKeyboardButton btnNo = new InlineKeyboardButton();
        btnNo.setText("No");
        btnNo.setCallbackData("no");

        rowInline.add(btnYes);
        rowInline.add(btnNo);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        return sendMessage;
    }

    public void sendMessage(String text, Long chatId) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(text);
            sendMessage.setChatId(chatId.toString());
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
