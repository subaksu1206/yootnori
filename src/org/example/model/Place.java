package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 윷놀이 판의 각 위치를 나타내는 클래스
 */
public class Place {
    private String id;                  // 위치 고유 식별자
    private String name;                // 위치 이름
    private boolean isJunction;         // 분기점 여부
    private boolean isCenter;           // 중앙점 여부
    private boolean isStartingPoint;    // 시작점 여부
    private boolean isEndingPoint;      // 도착점(골인 지점) 여부

    private Place nextPlace;            // 기본 다음 위치
    private Place specialNextPlace;     // 특별 다음 위치(대각선 등)
    private Place previousPlace;  // 이전 위치 참조 추가

    private List<Piece> pieces;         // 현재 이 위치에 있는 말들

    /**
     * 기본 생성자
     */
    public Place() {
        this.pieces = new ArrayList<>();
    }

    /**
     * ID와 이름으로 위치 생성
     * @param id 위치 식별자
     * @param name 위치 이름
     */
    public Place(String id, String name) {
        this.id = id;
        this.name = name;
        this.isJunction = false;
        this.isCenter = false;
        this.isStartingPoint = false;
        this.isEndingPoint = false;
        this.pieces = new ArrayList<>();
    }

    /**
     * 모든 속성을 지정하여 위치 생성
     * @param id 위치 식별자
     * @param name 위치 이름
     * @param isJunction 분기점 여부
     * @param isCenter 중앙점 여부
     * @param isStartingPoint 시작점 여부
     * @param isEndingPoint 도착점 여부
     */
    public Place(String id, String name, boolean isJunction, boolean isCenter,
                 boolean isStartingPoint, boolean isEndingPoint) {
        this.id = id;
        this.name = name;
        this.isJunction = isJunction;
        this.isCenter = isCenter;
        this.isStartingPoint = isStartingPoint;
        this.isEndingPoint = isEndingPoint;
        this.pieces = new ArrayList<>();
    }

    /**
     * 말을 이 위치에 추가
     * @param piece 추가할 말
     */
    public void addPiece(Piece piece) {
        if (piece != null && !pieces.contains(piece)) {
            pieces.add(piece);
        }
    }


    /**
     * 말을 이 위치에서 제거
     * @param piece 제거할 말
     * @return 제거 성공 여부
     */
    public boolean removePiece(Piece piece) {
        return pieces.remove(piece);
    }

    /**
     * 특정 플레이어의 말이 이 위치에 있는지 확인
     * @param player 확인할 플레이어
     * @return 플레이어의 말이 있으면 true
     */
    public boolean hasPlayerPieces(Player player) {
        for (Piece piece : pieces) {
            if (piece.getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 현재 플레이어 외 다른 플레이어의 말 목록 반환
     * @param currentPlayer 현재 플레이어
     * @return 다른 플레이어의 말 목록
     */
    public List<Piece> getOpponentPieces(Player currentPlayer) {
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece piece : pieces) {
            if (!piece.getPlayer().equals(currentPlayer)) {
                opponentPieces.add(piece);
            }
        }
        return opponentPieces;
    }

    /**
     * 현재 플레이어의 말 반환
     * @param player 현재 플레이어
     * @return 플레이어의 말(없으면 null)
     */
    public Piece getPlayerPiece(Player player) {
        for (Piece piece : pieces) {
            if (piece.getPlayer().equals(player)) {
                return piece;
            }
        }
        return null;
    }

    /**
     * 이 위치에 있는 모든 말 목록 반환
     * @return 말 목록
     */
    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    /**
     * 이 위치에 있는 말의 수 반환
     * @return 말의 수
     */
    public int getPieceCount() {
        return pieces.size();
    }

    /**
     * 이 위치가 비어있는지 확인
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return pieces.isEmpty();
    }

    // 기본 getter/setter 메서드

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 이 위치가 분기점인지 확인
     * @return 분기점이면 true
     */
    public boolean isJunction() {
        return isJunction;
    }

    public void setJunction(boolean junction) {
        isJunction = junction;
    }

    /**
     * 이 위치가 중앙점인지 확인
     * @return 중앙점이면 true
     */
    public boolean isCenter() {
        return isCenter;
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }

    /**
     * 이 위치가 시작점인지 확인
     * @return 시작점이면 true
     */
    public boolean isStartingPoint() {
        return isStartingPoint;
    }

    public void setStartingPoint(boolean startingPoint) {
        isStartingPoint = startingPoint;
    }

    /**
     * 이 위치가 도착점(골인 지점)인지 확인
     * @return 도착점이면 true
     */
    public boolean isEndingPoint() {
        return isEndingPoint;
    }

    public void setEndingPoint(boolean endingPoint) {
        isEndingPoint = endingPoint;
    }

    /**
     * 기본 다음 위치 반환
     * @return 다음 위치
     */
    public Place getNextPlace() {
        return nextPlace;
    }

    public void setNextPlace(Place nextPlace) {
        this.nextPlace = nextPlace;
    }

    /**
     * 특별 다음 위치(대각선 등) 반환
     * @return 특별 다음 위치
     */
    public Place getSpecialNextPlace() {
        return specialNextPlace;
    }

    public void setSpecialNextPlace(Place specialNextPlace) {
        this.specialNextPlace = specialNextPlace;
    }

    /**
     * 특별 다음 위치가 있는지 확인
     * @return 특별 다음 위치가 있으면 true
     */
    public boolean hasSpecialNextPlace() {
        return specialNextPlace != null;
    }

    public Place getPreviousPlace() {
        return previousPlace;
    }

    public void setPreviousPlace(Place previousPlace) {
        this.previousPlace = previousPlace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(id, place.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pieces=" + pieces.size() +
                ", isJunction=" + isJunction +
                ", isCenter=" + isCenter +
                ", isStartingPoint=" + isStartingPoint +
                ", isEndingPoint=" + isEndingPoint +
                '}';
    }

    private List<Place> nextPlaces = new ArrayList<>();

    public void addNext(Place next) {
        nextPlaces.add(next);
    }

    public List<Place> getNextPlaces() {
        return nextPlaces;
    }

}