package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 윷놀이 게임 플레이어 클래스
 * 플레이어 정보와 소유한 말들을 관리합니다.
 */
public class Player {
    private String id;           // 플레이어 고유 ID
    private String name;         // 플레이어 이름
    private List<Piece> pieces;  // 플레이어가 소유한 말 목록

    // 말 개수 제한 상수
    private static final int MIN_PIECES = 2;
    private static final int MAX_PIECES = 5;

    /**
     * 기본 생성자
     */
    public Player() {
        this.pieces = new ArrayList<>();
    }

    /**
     * 이름으로 플레이어 생성
     * @param name 플레이어 이름
     */
    public Player(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("플레이어 이름은 비어있을 수 없습니다.");
        }

        this.name = name;
        // 이름에서 공백을 _로 대체하고 소문자화하여 ID 생성
        this.id = name.replaceAll("\\s+", "_").toLowerCase();
        this.pieces = new ArrayList<>();
    }

    /**
     * 말 추가
     * @param piece 추가할 말
     * @throws IllegalStateException 최대 말 개수를 초과할 경우
     */
    public void addPiece(Piece piece) {
        if (piece == null) {
            throw new IllegalArgumentException("말(Piece)이 null일 수 없습니다.");
        }

        if (pieces.size() >= MAX_PIECES) {
            throw new IllegalStateException("플레이어당 최대 " + MAX_PIECES + "개의 말만 가질 수 있습니다.");
        }

        if (!pieces.contains(piece)) {
            pieces.add(piece);
        }
    }

    /**
     * 플레이어가 소유한 말 개수가 최소 요구사항을 충족하는지 확인
     * @return 최소 요구사항 충족 여부
     */
    public boolean hasMinimumRequiredPieces() {
        return pieces.size() >= MIN_PIECES;
    }

    /**
     * 플레이어의 모든 말 반환 (방어적 복사)
     * @return 말 목록의 복사본
     */
    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    /**
     * 이동 가능한 말 목록 반환 (완주하지 않은 말만)
     * @return 이동 가능한 말 목록
     */
    public List<Piece> getMovablePieces() {
        List<Piece> movablePieces = new ArrayList<>();
        for (Piece piece : pieces) {
            if (!piece.isCompleted()) {
                movablePieces.add(piece);
            }
        }
        return movablePieces;
    }

    /**
     * ID로 말 찾기
     * @param pieceId 찾을 말의 ID
     * @return 해당 ID의 말 객체, 없으면 null
     */
    public Piece getPieceById(String pieceId) {
        if (pieceId == null) {
            return null;
        }

        for (Piece piece : pieces) {
            if (pieceId.equals(piece.getId())) {
                return piece;
            }
        }
        return null;
    }

    /**
     * 모든 말이 완주했는지 확인
     * @return 모든 말이 완주했으면 true, 아니면 false
     */
    public boolean isAllPiecesCompleted() {
        if (pieces.isEmpty()) {
            return false; // 말이 없으면 완주할 수 없음
        }

        for (Piece piece : pieces) {
            if (!piece.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 완주한 말의 개수 반환
     * @return 완주한 말 개수
     */
    public int getCompletedPieceCount() {
        int count = 0;
        for (Piece piece : pieces) {
            if (piece.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 플레이어 ID 가져오기
     * @return 플레이어 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 플레이어 이름 가져오기
     * @return 플레이어 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 플레이어 이름 설정
     * @param name 새 이름
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("플레이어 이름은 비어있을 수 없습니다.");
        }
        this.name = name;
    }

    /**
     * 두 플레이어가 같은지 비교 (ID 기반)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    /**
     * 해시코드 계산 (ID 기반)
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 플레이어 정보를 문자열로 표현
     */
    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pieces=" + pieces.size() +
                ", completedPieces=" + getCompletedPieceCount() +
                '}';
    }
}