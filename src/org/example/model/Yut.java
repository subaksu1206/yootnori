package org.example.model;
import java.util.Random;

public class Yut {
    // 윷놀이 결과를 나타내는 열거형
    public enum YutResult {
        BACKDO(-1, "빽도"),
        DO(1, "도"),
        GAE(2, "개"),
        GEOL(3, "걸"),
        YUT(4, "윷"),
        MO(5, "모");

        private final int moveCount;
        private final String name;

        YutResult(int moveCount, String name) {
            this.moveCount = moveCount;
            this.name = name;
        }

        public int getMoveCount() {
            return moveCount;
        }

        public String getName() {
            return name;
        }
    }

    private final Random random = new Random();

    // 단일 윷 막대기 던지기 (앞면: 0, 뒷면: 1)
    private int throwOneStick() {
        return random.nextInt(2);
    }

    // 지정된 결과 반환 (테스트용)
    public YutResult getSpecificResult(YutResult result) {
        return result;
    }

    // 랜덤 윷 던지기
    public YutResult throwYut() {
        // 4개의 윷 막대기 던지기
        int backCount = 0; // 뒷면(등, 1) 개수

        for (int i = 0; i < 4; i++) {
            backCount += throwOneStick();
        }

        // 빽도 특수 케이스 (약 5% 확률로 발생)
        boolean isBackDo = backCount == 1 && random.nextDouble() < 0.05;
        if (isBackDo) {
            return YutResult.BACKDO;
        }

        // 윷 결과 계산
        switch (backCount) {
            case 0: return YutResult.MO;    // 4개 모두 앞면
            case 1: return YutResult.DO;    // 3개 앞면, 1개 뒷면
            case 2: return YutResult.GAE;   // 2개 앞면, 2개 뒷면
            case 3: return YutResult.GEOL;  // 1개 앞면, 3개 뒷면
            case 4: return YutResult.YUT;   // 4개 모두 뒷면
            default: throw new IllegalStateException("Invalid yut result");
        }
    }
}