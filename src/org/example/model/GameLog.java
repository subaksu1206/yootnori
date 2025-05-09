package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 게임 진행 로그를 관리하는 클래스
 */
public class GameLog {
    private List<LogEntry> entries;

    /**
     * 생성자
     */
    public GameLog() {
        this.entries = new ArrayList<>();
    }

    /**
     * 윷 던지기 로그 추가
     * @param player 윷을 던진 플레이어
     * @param result 윷 결과
     */
    public void addYutThrowLog(Player player, Yut.YutResult result) {
        String message = String.format("%s님이 '%s'를 던졌습니다.",
                player.getName(), result.getName());
        addEntry(message);
    }

    /**
     * 말 이동 로그 추가
     * @param player 말을 이동한 플레이어
     * @param piece 이동한 말
     * @param from 출발 위치
     * @param to 도착 위치
     */
    public void addMoveLog(Player player, Piece piece, Place from, Place to) {
        String fromName = from != null ? from.getName() : "시작점";
        String toName = to != null ? to.getName() : "도착점";

        String message = String.format("%s님의 말이 %s에서 %s(으)로 이동했습니다.",
                player.getName(), fromName, toName);
        addEntry(message);
    }

    /**
     * 말 업기 로그 추가
     * @param player 말을 업은 플레이어
     * @param basePiece 기본 말
     * @param stackedPiece 업힌 말
     */
    public void addStackLog(Player player, Piece basePiece, Piece stackedPiece) {
        String message = String.format("%s님의 말이 같은 위치의 다른 말을 업었습니다.",
                player.getName());
        addEntry(message);
    }

    /**
     * 말 잡기 로그 추가
     * @param player 말을 잡은 플레이어
     * @param capturedPiece 잡힌 말
     */
    public void addCaptureLog(Player player, Piece capturedPiece) {
        String message = String.format("%s님이 %s님의 말을 잡았습니다. 추가 턴이 부여됩니다.",
                player.getName(), capturedPiece.getPlayer().getName());
        addEntry(message);
    }

    /**
     * 추가 턴 로그 추가
     * @param player 추가 턴을 얻은 플레이어
     * @param reason 추가 턴의 이유
     */
    public void addExtraTurnLog(Player player, String reason) {
        String message = String.format("%s님이 %s(으)로 추가 턴을 얻었습니다.",
                player.getName(), reason);
        addEntry(message);
    }

    /**
     * 게임 종료 로그 추가
     * @param winner 승리한 플레이어
     */
    public void addGameOverLog(Player winner) {
        String message = String.format("게임이 종료되었습니다. %s님의 승리!",
                winner.getName());
        addEntry(message);
    }

    /**
     * 일반 로그 추가
     * @param message 로그 메시지
     */
    public void addEntry(String message) {
        LogEntry entry = new LogEntry(message);
        entries.add(entry);
    }

    /**
     * 모든 로그 항목 반환
     * @return 로그 항목 목록
     */
    public List<LogEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * 최근 로그 항목 반환
     * @param count 가져올 항목 수
     * @return 최근 로그 항목 목록
     */
    public List<LogEntry> getRecentEntries(int count) {
        int size = entries.size();
        if (size <= count) {
            return new ArrayList<>(entries);
        }
        return new ArrayList<>(entries.subList(size - count, size));
    }

    /**
     * 로그 항목 클래스
     */
    public static class LogEntry {
        private String timestamp;
        private String message;

        public LogEntry(String message) {
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            this.message = message;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s", timestamp, message);
        }
    }
}