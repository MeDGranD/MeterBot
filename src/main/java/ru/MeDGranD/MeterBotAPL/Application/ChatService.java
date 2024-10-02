package ru.MeDGranD.MeterBotAPL.Application;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.MeDGranD.MeterBotAPL.Infrastucture.ChatRepository;
import ru.MeDGranD.MeterBotAPL.Model.Chat;

@Service
public class ChatService {

    private final ChatRepository _chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository){
        this._chatRepository = chatRepository;
    }

    public Chat GetChat(long chatId){

        return this._chatRepository.findByChatId(chatId);

    }

    public void UpdateChat(@NotNull Chat chat) {

        if(this._chatRepository.findByChatId(chat.getChatId()) == null){
            return;
        }

        this._chatRepository.save(chat);

    }

    public void DeleteChat(@NotNull Chat chat){

        if(this._chatRepository.findByChatId(chat.getChatId()) == null){
            return;
        }

        this._chatRepository.delete(chat);

    }

    public Chat CreateChat(@NotNull Chat chat){

        if(this._chatRepository.findByChatId(chat.getChatId()) != null){
            return null;
        }

        this._chatRepository.save(chat);

        return chat;

    }

}
