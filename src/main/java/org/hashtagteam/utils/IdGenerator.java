package org.hashtagteam.utils;

import java.time.Instant;
import java.util.Random;

public class IdGenerator {
    private static final Random random = new Random();

    public static String generateId(String str) {
        //TODO 임시 수정 다시 수정 필요 
        long timestamp = Instant.now().toEpochMilli();
        int randomPart = random.nextInt(10000);
        return str + "-" + timestamp;
    }
}