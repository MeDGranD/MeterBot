package ru.MeDGranD.MeterBotAPL.Presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Locality;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.MeDGranD.MeterBotAPL.Application.Contacts.IncreaseUsersMetrics;
import ru.MeDGranD.MeterBotAPL.Application.UserService;

import java.util.Map;
import java.util.Optional;

@Component
public class MeterBot extends AbilityBot {

    private final String _token;
    private final String _botName;
    private final long _creatorId;
    private final UserService _userService;


    @Autowired
    public MeterBot(@Value("${telegramToken}") String token,
                    @Value("${botName}") String botName,
                    @Value("${creatorId}") long creatorId,
                    UserService userService){

        super(new OkHttpTelegramClient(token), botName);
        super.onRegister();

        _token = token;
        _creatorId = creatorId;
        _botName = botName;
        _userService = userService;

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

    public Ability measure(){

        return Ability
                .builder()
                .name("measure")
                .info("command for measuring anything!")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .setStatsEnabled(true)
                .action(ctx -> {


                    String userName = ctx.user().getUserName();

                    IncreaseUsersMetrics request = new IncreaseUsersMetrics();
                    request.username = userName;
                    request.id = ctx.user().getId();

                    Optional<Integer> serviceAnswer = _userService.IncreaseMetrics(request);

                    if(serviceAnswer.isEmpty()){
                        silent.send("Wait for another day man!", ctx.chatId());
                    }
                    else{
                        silent.send(String.format("Your metrics was increased by %d!", serviceAnswer.get()), ctx.chatId());
                    }


                })
                .build();

    }

    public Ability statistics(){

        return Ability
                .builder()
                .name("statistics")
                .info("Metrics statistics for everyone!")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .setStatsEnabled(true)
                .action(ctx -> {

                    String response = "Stats on this time:\n";

                    for(Map.Entry<String, Long> entry : _userService.GetStatistics().entrySet()){

                        response = response.concat(String.format("%s has %d metrics!\n", entry.getKey(), entry.getValue()));

                    }

                    silent.send(response, ctx.chatId());

                })
                .build();

    }


}