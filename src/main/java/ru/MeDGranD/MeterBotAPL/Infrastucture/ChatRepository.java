package ru.MeDGranD.MeterBotAPL.Infrastucture;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.MeDGranD.MeterBotAPL.Model.Chat;

@Repository
public interface ChatRepository extends CrudRepository<Chat, String> {

    public Chat findByChatId(long chatId);

}
