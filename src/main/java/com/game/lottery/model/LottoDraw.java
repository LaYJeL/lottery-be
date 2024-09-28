package com.game.lottery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lotto_draw")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LottoDraw {

    @Id
    private Long id;

    @OneToMany
    private List<User> users = new ArrayList<>();

    private LocalDateTime dateTime;
}
