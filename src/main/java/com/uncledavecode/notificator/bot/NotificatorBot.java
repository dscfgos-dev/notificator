package com.uncledavecode.notificator.bot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.uncledavecode.notificator.config.BotConfiguration;
import com.uncledavecode.notificator.controllers.UserController;
import com.uncledavecode.notificator.model.AccessRequest;
import com.uncledavecode.notificator.services.AccessRequestService;
import com.uncledavecode.notificator.utils.BotUtils;
import com.vdurmont.emoji.EmojiParser;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NotificatorBot extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;
    private final AccessRequestService accessRequestService;
    private final UserController userController;

    public NotificatorBot(BotConfiguration botConfiguration, AccessRequestService accessRequestService,
            UserController userController) {
        this.botConfiguration = botConfiguration;
        this.accessRequestService = accessRequestService;
        this.userController = userController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.getText() != null) {
                    if (message.getText().equalsIgnoreCase("/start")) {
                        BotUtils.sendMessage(this, "Welcome back!!!", update.getMessage().getChatId());
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
                            BotUtils.sendMessage(this,
                                    "To continue, you must register\\.\nRun the \\/register command to continue\\.",
                                    message.getChatId(), ParseMode.MARKDOWNV2);
                        }
                    }
                }
            } else if (update.hasCallbackQuery()) {
                this.processCallBack(update);
            }
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

    private void processCallBack(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {

            Long chat_id = update.getCallbackQuery().getMessage().getChatId();

            AccessRequest request = accessRequestService.getByChatId(chat_id);
            if (request != null) {
                long message_id = update.getCallbackQuery().getMessage().getMessageId();
                String response = update.getCallbackQuery().getData();
                boolean accepted = response.equalsIgnoreCase("yes");
                String answer = accepted
                        ? "Process Completed\\!\nUser account is pending for approval"
                        : "Registration canceled\\!";
                EditMessageText new_message = new EditMessageText();
                new_message.setChatId(chat_id.toString());
                new_message.setMessageId(Math.toIntExact(message_id));
                new_message.setText(answer);
                new_message.setParseMode(ParseMode.MARKDOWNV2);

                if (accepted) {
                    this.userController.addUserAccount(request);
                } else {
                    this.accessRequestService.deleteByChatId(request.getChatId());
                }

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
                boolean isValid = true;
                String textValue = update.getMessage().getText();
                switch (request.getStep()) {
                    case 1:
                        isValid = EmailValidator.getInstance().isValid(textValue);
                        if (isValid) {
                            request.setEmail(textValue);
                        } else {
                            BotUtils.sendMessage(this, "Email not valid", request.getChatId());
                        }
                        break;
                    case 2:
                        request.setName(textValue);
                        break;
                    case 3:
                        request.setLastname(textValue);
                        break;

                }
                if (isValid) {
                    request.setStep(request.getStep() + 1);
                    request = this.accessRequestService.updateAccessRequest(request);
                }
            }

            this.showMessageFromStep(request);
        }
    }

    private void continueUserRegister(AccessRequest request) {
        BotUtils.sendMessage(this, "We continue the user registration.!!!", request.getChatId());

        this.showMessageFromStep(request);
    }

    private void startUserRegister(AccessRequest request) {
        BotUtils.sendMessage(this, "Starting user registration.!!!", request.getChatId());
        request.setStep(1);
        request.setRequestDate(LocalDateTime.now());
        this.accessRequestService.updateAccessRequest(request);

        this.showMessageFromStep(request);
    }

    private void showMessageFromStep(AccessRequest request) {
        switch (request.getStep()) {
            case 1:
                BotUtils.sendMessage(this, "Step 1\nEnter an Email\\.", request.getChatId(), ParseMode.MARKDOWNV2);
                break;
            case 2:
                BotUtils.sendMessage(this, "Step 2\nEnter Name\\.", request.getChatId(), ParseMode.MARKDOWNV2);
                break;
            case 3:
                BotUtils.sendMessage(this, "Step 2\nEnter Last Name\\.", request.getChatId(), ParseMode.MARKDOWNV2);
                break;
            case 4:
                BotUtils.sendMessage(this, this.getConfirmMessage(request));
                break;
        }
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

    private SendMessage getConfirmMessage(AccessRequest request) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Final Step\nCreate User Account?");
        sendMessage.setChatId(request.getChatId().toString());

        InlineKeyboardButton btnYes = new InlineKeyboardButton();
        btnYes.setText(EmojiParser.parseToUnicode(":heavy_check_mark:") + " Yes");
        btnYes.setCallbackData("yes");

        InlineKeyboardButton btnNo = new InlineKeyboardButton();
        btnNo.setText(EmojiParser.parseToUnicode(":x:") + " No");
        btnNo.setCallbackData("no");

        rowInline.add(btnYes);
        rowInline.add(btnNo);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        return sendMessage;
    }

}
