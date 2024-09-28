package com.game.lottery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    private Long id;

    @Column
    private Integer firstBall;
    private Integer secondBall;
    private Integer thirdBall;
    private Integer fourthBall;
    private Integer fifthBall;
    private Integer powerBall;

    @ManyToOne
    private User user;

}
