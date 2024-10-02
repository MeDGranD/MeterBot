package ru.MeDGranD.MeterBotAPL.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "chats")
public class Chat {

    @Id
    @Column(name = "chatId")
    private Long chatId;

    @Column(name = "nameOfMetrics")
    private String nameOfMetrics;

    public Chat(){}

}
