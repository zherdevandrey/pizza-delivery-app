package com.example.telegram.to.kafka.app.runner.impl;


import com.example.app.config.data.TelegramToKafkaServiceConfigData;
import com.example.telegram.to.kafka.app.listener.TelegramKafkaStatusListener;
import com.example.telegram.to.kafka.app.runner.StreamRunner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@ComponentScan(basePackages = "com.example.app.config")
@ConditionalOnProperty(name = "telegram-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private final TelegramToKafkaServiceConfigData telegramToKafkaServiceConfigData;

    private static final String TELEGRAM_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    private static final Random RANDOM = new Random();

    private static final String[] WORDS = new String[]{
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetuer",
            "adipiscing",
            "elit",
            "Maecenas",
            "porttitor",
            "congue",
            "massa",
            "Fusce",
            "posuere",
            "magna",
            "sed",
            "pulvinar",
            "ultricies",
            "purus",
            "lectus",
            "malesuada",
            "libero"
    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";
    private final TelegramKafkaStatusListener telegramKafkaStatusListener;

    public MockKafkaStreamRunner(TelegramToKafkaServiceConfigData configData,
                                 TelegramKafkaStatusListener statusListener) {
        this.telegramToKafkaServiceConfigData = configData;
        this.telegramKafkaStatusListener = statusListener;
    }

    @Override
    public void start() {
        final String[] keywords = telegramToKafkaServiceConfigData.getTelegramKeywords().toArray(new String[0]);
        final int minTweetLength = telegramToKafkaServiceConfigData.getMockMinTweetLength();
        final int maxTweetLength = telegramToKafkaServiceConfigData.getMockMaxTweetLength();
        long sleepTimeMs = telegramToKafkaServiceConfigData.getMockSleepMs();
        log.info("Starting mock filtering telegram streams for keywords {}", Arrays.toString(keywords));
        simulateTelegramStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
    }

    @SneakyThrows
    private void simulateTelegramStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                telegramKafkaStatusListener.onStatus(status);
                sleep(sleepTimeMs);
            }
        });
    }

    @SneakyThrows
    private void sleep(long sleepTimeMs) {
        Thread.sleep(sleepTimeMs);
    }

    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
        String[] params = new String[]{
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TELEGRAM_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return formatTweetAsJsonWithParams(params);
    }

    private String formatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;

        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        return constructRandomTweet(keywords, tweet, tweetLength);
    }

    private String constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }

}
