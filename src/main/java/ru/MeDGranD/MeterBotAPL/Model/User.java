package ru.MeDGranD.MeterBotAPL.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(name = "lastUpdate")
    private LocalDateTime lastUpdate;

    @Column(name = "firstUpdate")
    private LocalDateTime firstUpdate;

    @Column(name = "metrics")
    private Long metrics;

    @Column(name = "telegramId")
    private Long telegramId;

    @Column(name = "userName")
    private String userName;

    public User(){}

    private User(UserBuilder builder){

        this.Id = builder.Id;
        this.userName = builder.userName;
        this.metrics = builder.metrics;
        this.telegramId = builder.telegramId;
        this.lastUpdate = builder.lastUpdate;
        this.firstUpdate = builder.firstUpdate;

    }

    public static class UserBuilder{
        private Long Id;
        private LocalDateTime lastUpdate;
        private LocalDateTime firstUpdate;
        private Long metrics;
        private Long telegramId;
        private String userName;

        public UserBuilder(){}

        public UserBuilder lastUpdate(LocalDateTime lastUpdate){
            this.lastUpdate = lastUpdate;
            return this;
        }

        public UserBuilder firstUpdate(LocalDateTime firstUpdate){
            this.firstUpdate = firstUpdate;
            return this;
        }

        public UserBuilder metrics(Long metrics){
            this.metrics = metrics;
            return this;
        }

        public UserBuilder username(String userName){
            this.userName = userName;
            return this;
        }

        public UserBuilder telegramId(Long telegramId){
            this.telegramId = telegramId;
            return this;
        }

        public UserBuilder id(long id){
            this.Id = id;
            return this;
        }

        public User build(){
            return new User(this);
        }

    }

}
