package org.example.model;

/**
 * 게임 설정을 관리하는 클래스
 * 플레이어 수, 말 개수, 보드 형태 등의 게임 설정을 저장
 */
public class GameSettings {
    // 상수 정의
    private static final int DEFAULT_PLAYER_COUNT = 2;
    private static final int DEFAULT_PIECE_PER_PLAYER = 4;
    private static final Board.BoardType DEFAULT_BOARD_TYPE = Board.BoardType.SQUARE;

    private static final int MIN_PLAYER_COUNT = 2;
    private static final int MAX_PLAYER_COUNT = 4;
    private static final int MIN_PIECE_PER_PLAYER = 2;
    private static final int MAX_PIECE_PER_PLAYER = 5;

    // 설정 속성
    private int playerCount;
    private int piecePerPlayer;
    private Board.BoardType boardType;

    /**
     * 기본 생성자
     * 기본값으로 설정
     */
    public GameSettings() {
        this.playerCount = DEFAULT_PLAYER_COUNT;
        this.piecePerPlayer = DEFAULT_PIECE_PER_PLAYER;
        this.boardType = DEFAULT_BOARD_TYPE;
    }

    /**
     * 모든 설정 값을 지정하는 생성자
     * @param playerCount 플레이어 수
     * @param piecePerPlayer 플레이어당 말 개수
     * @param boardType 보드 형태
     * @throws IllegalArgumentException 유효하지 않은 설정값 입력 시
     */
    public GameSettings(int playerCount, int piecePerPlayer, Board.BoardType boardType) {
        setPlayerCount(playerCount);
        setPiecePerPlayer(piecePerPlayer);
        setBoardType(boardType);
    }

    /**
     * 플레이어 수 설정
     * @param playerCount 플레이어 수 (2-4)
     * @throws IllegalArgumentException 범위를 벗어난 경우
     */
    public void setPlayerCount(int playerCount) {
        if (playerCount < MIN_PLAYER_COUNT || playerCount > MAX_PLAYER_COUNT) {
            throw new IllegalArgumentException("플레이어 수는 " + MIN_PLAYER_COUNT + "명에서 "
                    + MAX_PLAYER_COUNT + "명 사이여야 합니다");
        }
        this.playerCount = playerCount;
    }

    /**
     * 플레이어당 말 개수 설정
     * @param piecePerPlayer 말 개수 (2-5)
     * @throws IllegalArgumentException 범위를 벗어난 경우
     */
    public void setPiecePerPlayer(int piecePerPlayer) {
        if (piecePerPlayer < MIN_PIECE_PER_PLAYER || piecePerPlayer > MAX_PIECE_PER_PLAYER) {
            throw new IllegalArgumentException("플레이어당 말 개수는 " + MIN_PIECE_PER_PLAYER + "개에서 "
                    + MAX_PIECE_PER_PLAYER + "개 사이여야 합니다");
        }
        this.piecePerPlayer = piecePerPlayer;
    }

    /**
     * 보드 형태 설정
     * @param boardType 보드 형태 (사각형, 오각형, 육각형)
     * @throws NullPointerException null 입력 시
     */
    public void setBoardType(Board.BoardType boardType) {
        if (boardType == null) {
            throw new NullPointerException("보드 타입은 null일 수 없습니다");
        }
        this.boardType = boardType;
    }

    /**
     * 플레이어 수 반환
     * @return 플레이어 수
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * 플레이어당 말 개수 반환
     * @return 말 개수
     */
    public int getPiecePerPlayer() {
        return piecePerPlayer;
    }

    /**
     * 보드 형태 반환
     * @return 보드 형태
     */
    public Board.BoardType getBoardType() {
        return boardType;
    }

    /**
     * 설정값이 모두 유효한지 검증
     * @return 유효성 여부
     */
    public boolean isValid() {
        return playerCount >= MIN_PLAYER_COUNT && playerCount <= MAX_PLAYER_COUNT
                && piecePerPlayer >= MIN_PIECE_PER_PLAYER && piecePerPlayer <= MAX_PIECE_PER_PLAYER
                && boardType != null;
    }

    /**
     * 정적 상수에 대한 접근 메서드
     */
    public static int getMinPlayerCount() {
        return MIN_PLAYER_COUNT;
    }

    public static int getMaxPlayerCount() {
        return MAX_PLAYER_COUNT;
    }

    public static int getMinPiecePerPlayer() {
        return MIN_PIECE_PER_PLAYER;
    }

    public static int getMaxPiecePerPlayer() {
        return MAX_PIECE_PER_PLAYER;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "playerCount=" + playerCount +
                ", piecePerPlayer=" + piecePerPlayer +
                ", boardType=" + boardType +
                '}';
    }
}