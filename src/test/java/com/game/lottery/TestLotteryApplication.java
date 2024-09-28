package com.game.lottery;

import org.springframework.boot.SpringApplication;

public class TestLotteryApplication {

    public static void main(String[] args) {
        SpringApplication.from(LotteryApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
