package org.example.model;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 윷놀이 게임 보드 클래스
 * 사각형, 오각형, 육각형 보드 형태를 지원합니다.
 */
public class Board {
    private BoardType boardType; // 사각형, 오각형, 육각형
    private Map<String, Place> places; // ID로 접근 가능한 모든 위치
    private Place startingPlace; // 시작 위치
    private Place endingPlace; // 도착 위치
    private Map<String,Place> centerPlaces; // 중앙 위치
    private Place beyondEndPlace; // E를 넘어가는 히든 위치

    /**
     * 보드 타입 열거형
     */
    public enum BoardType {
        SQUARE, PENTAGON, HEXAGON
    }

    /**
     * 생성자
     * @param boardType 보드의 형태(사각형, 오각형, 육각형)
     */
    public Board(BoardType boardType) {
        this.boardType = boardType;
        this.places = new HashMap<>();
        this.centerPlaces = new HashMap<>();

        // 보드 초기화
        initializeBoard();
    }

    /**
     * 보드 타입에 따라 보드 초기화
     */
    private void initializeBoard() {
        switch (boardType) {
            case SQUARE:
                initializeSquareBoard();
                break;
            case PENTAGON:
                initializePentagonBoard();
                break;
            case HEXAGON:
                initializeHexagonBoard();
                break;
        }
    }
    /**
     * 사각형 보드 초기화 메서드
     * 일반적인 윷놀이판 구조를 생성합니다.
     */

    public void initializeSquareBoard() {
        places = new HashMap<>();
        centerPlaces = new HashMap<>();

        // === 외곽 점 S(0) ~ 23 ===
        for (int i = 0; i < 24; i++) {
            String id = String.valueOf(i);
            String name = (i == 0) ? "S" : id;
            places.put(id, new Place(id, name));
        }

        // 외곽 연결 (원형)
        for (int i = 0; i < 24; i++) {
            Place current = places.get(String.valueOf(i));
            Place next = places.get(String.valueOf((i + 1) % 24));
            current.addNext(next);
        }

        // === 중심점 X (id: "X") ===
        Place center = new Place("X", "X");
        places.put("X", center);
        centerPlaces.put("X", center);

        // === 대각선 점 C1~C8 ===
        String[] diagIds = {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8"};
        for (String cid : diagIds) {
            places.put(cid, new Place(cid, cid));
        }

        // 대각선 경로 연결
        // 5 → C1 → C2 → X
        places.get("5").addNext(places.get("C1"));
        places.get("C1").addNext(places.get("C2"));
        places.get("C2").addNext(center);

        // 10 → C3 → C4 → X
        places.get("10").addNext(places.get("C3"));
        places.get("C3").addNext(places.get("C4"));
        places.get("C4").addNext(center);

        // X → C5 → C6 → 15
        center.addNext(places.get("C5"));
        places.get("C5").addNext(places.get("C6"));
        places.get("C6").addNext(places.get("15"));

        // X → C7 → C8 → 0
        center.addNext(places.get("C7"));
        places.get("C7").addNext(places.get("C8"));
        places.get("C8").addNext(places.get("0"));

        // 시작 위치
        startingPlace = places.get("0");
    }


    /**
     * 오각형 보드 초기화 메서드
     * 오각형 구조의 윷놀이판을 생성합니다.
     */
    private void initializePentagonBoard() {
        // 시작/도착 지점 생성
        Place start = createPlace("S", "시작점", false, false, true, false);
        Place end = createPlace("E", "종료점", false, false, false, false);
        Place beyondEnd = createPlace("FE", "최종 도착", false, false, false, true);

        startingPlace = start;
        endingPlace = beyondEnd; // 최종 도착
        end.setNextPlace(beyondEnd);
        beyondEnd.setPreviousPlace(end); // 역방향 참조 설정

        // 외곽 경로 생성 (인덱스 1~24 사용)
        Place[] outerPath = new Place[25];
        for (int i = 1; i < 25; i++) {
            String id = String.valueOf(i);
            String name = "외곽" + i;
            outerPath[i] = createPlace(id, name);
        }

        // 외곽 경로 연결 및 역방향 참조 설정
        for (int i = 1; i < 24; i++) {
            outerPath[i].setNextPlace(outerPath[i + 1]);
            outerPath[i + 1].setPreviousPlace(outerPath[i]); // 역방향 참조 설정
        }
        outerPath[24].setNextPlace(end);
        end.setPreviousPlace(outerPath[24]); // 역방향 참조 설정

        start.setNextPlace(outerPath[1]);
        outerPath[1].setPreviousPlace(start); // 역방향 참조 설정

        // 중앙 지점 생성
        Place center1 = createPlace("C_1", "중앙1", true, true, false, false);
        Place center2 = createPlace("C_2", "중앙2", true, true, false, false);
        centerPlaces.put(center1.getId(), center1);
        centerPlaces.put(center2.getId(), center2);

        Place C1 = createPlace("C1", "C1");
        Place C2 = createPlace("C2", "C2");
        Place C3 = createPlace("C3", "C3");
        Place C4 = createPlace("C4", "C4");
        Place C5 = createPlace("C5", "C5");
        Place C6 = createPlace("C6", "C6");
        Place C7 = createPlace("C7", "C7");
        Place C8 = createPlace("C8", "C8");
        Place C9 = createPlace("C9", "C9");
        Place C10 = createPlace("C10", "C10");

        outerPath[5].setJunction(true);
        outerPath[10].setJunction(true);
        outerPath[15].setJunction(true);

        // 특별 경로 연결 및 역방향 참조 설정
        outerPath[5].setSpecialNextPlace(C1);
        C1.setPreviousPlace(outerPath[5]); // 역방향 참조 설정

        outerPath[10].setSpecialNextPlace(C3);
        C3.setPreviousPlace(outerPath[10]); // 역방향 참조 설정

        outerPath[15].setSpecialNextPlace(C5);
        C5.setPreviousPlace(outerPath[15]); // 역방향 참조 설정

        C1.setNextPlace(C2);
        C2.setPreviousPlace(C1); // 역방향 참조 설정

        C2.setNextPlace(center1);
        center1.setPreviousPlace(C2); // 역방향 참조 설정

        center1.setNextPlace(C7);
        C7.setPreviousPlace(center1); // 역방향 참조 설정

        C7.setNextPlace(C8);
        C8.setPreviousPlace(C7); // 역방향 참조 설정

        C8.setNextPlace(outerPath[20]);
        outerPath[20].setPreviousPlace(C8); // 역방향 참조 설정

        C3.setNextPlace(C4);
        C4.setPreviousPlace(C3); // 역방향 참조 설정

        C4.setNextPlace(center1);
        // center1은 이미 역방향 참조가 설정됨

        center1.setSpecialNextPlace(C9);
        C9.setPreviousPlace(center1); // 역방향 참조 설정

        C9.setNextPlace(C10);
        C10.setPreviousPlace(C9); // 역방향 참조 설정

        C10.setNextPlace(end);
        // end는 이미 역방향 참조가 설정됨

        C5.setNextPlace(C6);
        C6.setPreviousPlace(C5); // 역방향 참조 설정

        C6.setNextPlace(center2);
        center2.setPreviousPlace(C6); // 역방향 참조 설정

        center2.setNextPlace(C9);
        // C9은 이미 역방향 참조가 설정됨
    }

    /**
     * 육각형 보드 초기화 메서드
     * 육각형 구조의 윷놀이판을 생성합니다.
     */
    private void initializeHexagonBoard() {
        Place start = createPlace("S", "시작점", false, false, true, false);
        Place end = createPlace("E", "종료점", false, false, false, false);
        Place beyondEnd = createPlace("FE", "최종 도착", false, false, false, true);

        startingPlace = start;
        endingPlace = beyondEnd; // 최종 도착
        end.setNextPlace(beyondEnd);
        beyondEnd.setPreviousPlace(end); // 역방향 참조 설정

        Place center1 = createPlace("C_1", "중앙1", true, true, false, false);
        Place center2 = createPlace("C_2", "중앙2", true, true, false, false);
        centerPlaces.put(center1.getId(), center1);
        centerPlaces.put(center2.getId(), center2);

        // 외곽 경로 생성 (인덱스 1~29 사용)
        Place[] outerPath = new Place[30];
        for (int i = 1; i < 30; i++) {
            String id = String.valueOf(i);
            String name = "외곽" + i;
            outerPath[i] = createPlace(id, name);
        }

        // 외곽 경로 연결 및 역방향 참조 설정
        for (int i = 1; i < 29; i++) {
            outerPath[i].setNextPlace(outerPath[i + 1]);
            outerPath[i + 1].setPreviousPlace(outerPath[i]); // 역방향 참조 설정
        }
        outerPath[29].setNextPlace(end);
        end.setPreviousPlace(outerPath[29]); // 역방향 참조 설정

        start.setNextPlace(outerPath[1]);
        outerPath[1].setPreviousPlace(start); // 역방향 참조 설정

        Place C1 = createPlace("C1", "C1");
        Place C2 = createPlace("C2", "C2");
        Place C3 = createPlace("C3", "C3");
        Place C4 = createPlace("C4", "C4");
        Place C5 = createPlace("C5", "C5");
        Place C6 = createPlace("C6", "C6");
        Place C7 = createPlace("C7", "C7");
        Place C8 = createPlace("C8", "C8");
        Place C9 = createPlace("C9", "C9");
        Place C10 = createPlace("C10", "C10");
        Place C11 = createPlace("C11", "C11");
        Place C12 = createPlace("C12", "C12");

        outerPath[5].setJunction(true);
        outerPath[10].setJunction(true);
        outerPath[15].setJunction(true);
        outerPath[20].setJunction(true);

        // 특별 경로 연결 및 역방향 참조 설정
        outerPath[5].setSpecialNextPlace(C1);
        C1.setPreviousPlace(outerPath[5]); // 역방향 참조 설정

        outerPath[10].setSpecialNextPlace(C3);
        C3.setPreviousPlace(outerPath[10]); // 역방향 참조 설정

        outerPath[15].setSpecialNextPlace(C5);
        C5.setPreviousPlace(outerPath[15]); // 역방향 참조 설정

        outerPath[20].setSpecialNextPlace(C7);
        C7.setPreviousPlace(outerPath[20]); // 역방향 참조 설정

        C1.setNextPlace(C2);
        C2.setPreviousPlace(C1); // 역방향 참조 설정

        C2.setNextPlace(center1);
        center1.setPreviousPlace(C2); // 역방향 참조 설정

        center1.setNextPlace(C9);
        C9.setPreviousPlace(center1); // 역방향 참조 설정

        C9.setNextPlace(C10);
        C10.setPreviousPlace(C9); // 역방향 참조 설정

        C10.setNextPlace(outerPath[25]);
        outerPath[25].setPreviousPlace(C10); // 역방향 참조 설정

        C3.setNextPlace(C4);
        C4.setPreviousPlace(C3); // 역방향 참조 설정

        C4.setNextPlace(center1);
        // center1은 이미 역방향 참조가 설정됨

        C5.setNextPlace(C6);
        C6.setPreviousPlace(C5); // 역방향 참조 설정

        C6.setNextPlace(center1);
        // center1은 이미 역방향 참조가 설정됨

        C7.setNextPlace(C8);
        C8.setPreviousPlace(C7); // 역방향 참조 설정

        C8.setNextPlace(center2);
        center2.setPreviousPlace(C8); // 역방향 참조 설정

        center1.setSpecialNextPlace(C11);
        C11.setPreviousPlace(center1); // 역방향 참조 설정

        C11.setNextPlace(C12);
        C12.setPreviousPlace(C11); // 역방향 참조 설정

        C12.setNextPlace(end);
        // end는 이미 역방향 참조가 설정됨

        center2.setNextPlace(C11);
        // C11은 이미 역방향 참조가 설정됨
    }

    /**
     * 위치 생성 및 맵에 추가하는 유틸리티 메서드
     */
    private Place createPlace(String id, String name, boolean isJunction, boolean isCenter,
                              boolean isStartingPoint, boolean isEndingPoint) {
        Place place = new Place(id, name, isJunction, isCenter, isStartingPoint, isEndingPoint);
        places.put(id, place);
        return place;
    }

    /**
     * 간소화된 위치 생성 유틸리티 메서드
     */
    private Place createPlace(String id, String name) {
        return createPlace(id, name, false, false, false, false);
    }

    /**
     * ID로 위치 찾기
     * @param id 위치 ID
     * @return 해당 ID의 위치 객체
     */
    public Place getPlaceById(String id) {
        return places.get(id);
    }

    /**
     * 시작 위치 반환
     * @return 시작 위치
     */
    public Place getStartingPlace() {
        return startingPlace;
    }

    /**
     * 도착 위치 반환
     * @return 도착 위치
     */
    public Place getEndingPlace() {
        return endingPlace;
    }

    /**
     * 중앙 위치 리스트 반환
     * 차후 UI 표시에서는 하나만 찍힐 수 있게 즉, 해당 위치에 도달하면, 중앙에 찍히게
     * @return 중앙 위치
     */
    public Map<String,Place> getCenterPlaces() {
        return new HashMap<>(centerPlaces);
    }

    /**
     * 현재 보드 타입 반환
     * @return 보드 타입
     */
    public BoardType getBoardType() {
        return boardType;
    }

    /**
     * 모든 위치 정보 반환
     * @return 위치 맵
     */
    public Map<String, Place> getAllPlaces() {
        return new HashMap<>(places);
    }
    /**
     * 현재 위치와 윷 결과를 바탕으로 목적지 계산
     * 특별 경로가 있으면 1칸은 특별 경로로, 나머지는 기본 경로로 이동
     * @param currentPlace 현재 위치
     * @param yutResult 윷 결과
     * @return 이동 후 목적지
     */
    public Place calculateDestination(Place currentPlace, Yut.YutResult yutResult) {
        // 이동 칸수 가져오기
        int moveCount = yutResult.getMoveCount();

        // 빽도 케이스 처리 개선
        if (moveCount < 0) {
            // 시작점이나 최종 도착점(FE)에서는 빽도가 적용되지 않음
            if (currentPlace.isStartingPoint() || currentPlace.isEndingPoint()) {
                return currentPlace;
            }

            // 도착점(E)에서는 보드 타입에 따라 적절한 위치로
            if (currentPlace.getId().equals("E")) {
                // previousPlace 참조를 사용하면 이 분기는 필요 없을 수 있음
                // 호환성을 위해 유지
                switch (boardType) {
                    case SQUARE:
                        return getPlaceById("19");
                    case PENTAGON:
                        return getPlaceById("24");
                    case HEXAGON:
                        return getPlaceById("29");
                    default:
                        return currentPlace; // 기본값
                }
            }

            // 이제 previousPlace를 사용해 이전 위치로 이동
            if (currentPlace.getPreviousPlace() != null) {
                return currentPlace.getPreviousPlace();
            } else {
                // 이전 위치 참조가 없는 경우 ID 기반 대체 (하위 호환성)
                // 이 부분은 모든 위치에 previousPlace가 설정된 경우 필요 없음
                return getPlaceById(String.valueOf(Integer.parseInt(currentPlace.getId()) - 1));
            }
        }

        // 현재 위치가 null이면 시작점에서 시작
        if (currentPlace == null) {
            currentPlace = startingPlace;
        }

        Place currentPos = currentPlace;

        // 1. 출발 지점에 특별 경로가 있는지 확인
        if (currentPos.isJunction() && currentPos.hasSpecialNextPlace()) {
            // 1.1. 분기점에서는 첫 이동을 특별 경로로
            currentPos = currentPos.getSpecialNextPlace();
            moveCount--; // 이동 횟수 감소
        }

        // 1.2. 남은 이동을 기본 경로로 처리
        for (int i = 0; i < moveCount; i++) {
            if (currentPos.getNextPlace() != null) {
                currentPos = currentPos.getNextPlace();
            } else {
                break; // 더 이상 이동할 수 없음
            }
        }

        return currentPos;
    }
    /**
     * 두 위치 간의 최단 이동 경로 계산
     * (이 기능은 UI에서 경로 표시에 활용할 수 있음)
     * @param from 시작 위치
     * @param to 도착 위치
     * @return 경로 상의 위치 목록
     */
    public List<Place> calculatePath(Place from, Place to) {
        List<Place> path = new ArrayList<>();
        path.add(from);

        // 간단한 구현: 다음 위치로 계속 이동하면서 목적지에 도달하는지 확인
        // 실제 구현에서는 BFS나 다익스트라 알고리즘을 사용하여 최단 경로 탐색 필요
        Place current = from;
        while (current != to && path.size() < 50) { // 무한 루프 방지
            if (current.getNextPlace() != null) {
                current = current.getNextPlace();
                path.add(current);
            } else {
                break;
            }
        }

        return path;
    }

}