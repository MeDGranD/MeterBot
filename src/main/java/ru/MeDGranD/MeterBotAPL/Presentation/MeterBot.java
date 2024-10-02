package ru.MeDGranD.MeterBotAPL.Presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Flag;
import org.telegram.telegrambots.abilitybots.api.objects.Locality;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.MeDGranD.MeterBotAPL.Application.ChatService;
import ru.MeDGranD.MeterBotAPL.Application.Contacts.IncreaseUsersMetrics;
import ru.MeDGranD.MeterBotAPL.Application.UserService;
import ru.MeDGranD.MeterBotAPL.Model.Chat;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class MeterBot extends AbilityBot {

    private final String _token;
    private final String _botName;
    private final long _creatorId;
    private final UserService _userService;
    private final ChatService _chatService;


    @Autowired
    public MeterBot(@Value("${telegramToken}") String token,
                    @Value("${botName}") String botName,
                    @Value("${creatorId}") long creatorId,
                    UserService userService,
                    ChatService chatService){

        super(new OkHttpTelegramClient(token), botName);
        super.onRegister();

        _token = token;
        _creatorId = creatorId;
        _botName = botName;
        _userService = userService;
        _chatService = chatService;

        try{

            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(token, this);

        } catch (TelegramApiException exp){
            exp.printStackTrace();
        }

    }

    @Override
    public long creatorId(){
        return _creatorId;
    }

    public Ability nameMetrics(){

        return Ability
                .builder()
                .name("nameMetrics")
                .info("Команда для назначения названия метрики")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .setStatsEnabled(true)
                .action(ctx ->{

                    silent.forceReply("Введите название вашей метрики:", ctx.chatId());


                })
                .reply(((baseAbilityBot, update) -> {

                    String reply = update.getMessage().getText();
                    if(reply.length() > 255){
                        silent.send("Название слишком большое, попробуйте уменьшить его до 255 символов.", update.getMessage().getChatId());
                        silent.forceReply("Введите название вашей метрики:", update.getMessage().getChatId());
                        return;
                    }

                    Chat newChat = _chatService.GetChat(update.getMessage().getChatId());

                    if(newChat == null) {
                        newChat = new Chat();
                        newChat.setChatId(update.getMessage().getChatId());
                        newChat.setNameOfMetrics(reply);
                        _chatService.CreateChat(newChat);
                    }
                    else{
                        newChat.setNameOfMetrics(reply);
                        _chatService.UpdateChat(newChat);
                    }


                }),
                        Flag.MESSAGE,
                        Flag.REPLY,
                        isReplyToBot(),
                        isReplyToMessage("Введите название вашей метрики:"))
                .build();

    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase("testBootPisa_bot");
    }

    public Ability measure(){

        return Ability
                .builder()
                .name("measure")
                .info("Команда для измерения метрики")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .setStatsEnabled(true)
                .action(ctx -> {


                    String userName = ctx.user().getUserName();
                    long chatId = ctx.chatId();

                    Chat chat = _chatService.GetChat(chatId);
                    if(chat == null){
                        this.nameMetrics().action().accept(ctx);
                        return;
                    }

                    IncreaseUsersMetrics request = new IncreaseUsersMetrics();
                    request.username = userName;
                    request.id = ctx.user().getId();

                    Optional<Integer> serviceAnswer = _userService.IncreaseMetrics(request);

                    if(serviceAnswer.isEmpty()){
                        silent.send("Надо ждать следующего дня!", ctx.chatId());
                    }
                    else{
                        silent.send(String.format("Твой/я/ё %s был(а/о) увеличено на %d!", chat.getNameOfMetrics(), serviceAnswer.get()), ctx.chatId());
                    }


                })
                .build();

    }

    public Ability statistics(){

        return Ability
                .builder()
                .name("statistics")
                .info("Статистика метрик для всех!")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .setStatsEnabled(true)
                .action(ctx -> {

                    Chat chat = _chatService.GetChat(ctx.chatId());
                    if(chat == null){
                        this.nameMetrics().action().accept(ctx);
                        return;
                    }

                    String response = "Статистика на данный момент:\n";

                    for(Map.Entry<String, Long> entry : _userService.GetStatistics().entrySet()){

                        response = response.concat(String.format("%s имеет %d %s!\n", entry.getKey(), entry.getValue(), chat.getNameOfMetrics()));

                    }

                    silent.send(response, ctx.chatId());

                })
                .build();

    }


}