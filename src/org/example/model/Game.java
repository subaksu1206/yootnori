package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 윷놀이 게임의 전체 상태를 관리하는 모델 클래스
 * MVC 아키텍처에서 Model 역할을 담당
 */
public class Game {
    private List<Player> players;
    private int currentTurnIndex;
    private Board board;
    private Yut yut;
    private GameSettings gameSettings;
    private boolean isGameFinished;
    private Player winner;
    private Yut.YutResult lastYutResult;
    private boolean hasExtraTurn;
    private List<String> gameLog;
    private List<Yut.YutResult> pendingYutResults;
    // 상대방 말을 잡아서 얻은 추가 턴이 사용되었는지 여부
    private boolean captureExtraTurnUsed;
    // 각 윷 결과가 기본 턴에서 온 것인지 추가 턴에서 온 것인지 추적
    private boolean isExtraTurnThrow;

    // Game.java 클래스 내에 추가
    /**
     * 디버깅용: 특정 위치의 말 정보 출력
     */
    public void debugPrintPlaceInfo(Place place) {
        if (place == null) {
            addToGameLog("[디버그] 위치 객체가 null입니다.");
            return;
        }

        addToGameLog("=== [디버그] 위치 정보 ===");
        addToGameLog("위치 ID: " + place.getId());
        addToGameLog("위치 이름: " + place.getName());
        addToGameLog("분기점 여부: " + place.isJunction());
        addToGameLog("중앙점 여부: " + place.isCenter());
        addToGameLog("시작점 여부: " + place.isStartingPoint());
        addToGameLog("도착점 여부: " + place.isEndingPoint());

        List<Piece> pieces = place.getPieces();
        addToGameLog("말 개수: " + pieces.size());

        if (!pieces.isEmpty()) {
            addToGameLog("--- 말 목록 ---");
            for (int i = 0; i < pieces.size(); i++) {
                Piece piece = pieces.get(i);
                addToGameLog((i+1) + ". ID: " + piece.getId() +
                        ", 플레이어: " + piece.getPlayer().getName() +
                        ", 업힌 말 수: " + piece.getStackedPieces().size());

                // 업힌 말이 있으면 자세히 출력
                if (!piece.getStackedPieces().isEmpty()) {
                    addToGameLog("   업힌 말 목록:");
                    for (Piece stackedPiece : piece.getStackedPieces()) {
                        addToGameLog("    - " + stackedPiece.getId() +
                                " (소유자: " + stackedPiece.getPlayer().getName() + ")");
                    }
                }
            }
        }
        addToGameLog("===============");
    }

    /**
     * 디버깅용: 플레이어의 모든 말 상태 출력
     */
    public void debugPrintPlayerPieces(Player player) {
        if (player == null) {
            addToGameLog("[디버그] 플레이어 객체가 null입니다.");
            return;
        }

        addToGameLog("=== [디버그] " + player.getName() + "의 말 상태 ===");
        List<Piece> pieces = player.getPieces();
        addToGameLog("총 말 개수: " + pieces.size());

        for (Piece piece : pieces) {
            Place currentPlace = piece.getCurrentPlace();
            String locationInfo = currentPlace != null ?
                    (currentPlace.getId() + " (" + currentPlace.getName() + ")") :
                    "보드에 없음 (업힌 상태 또는 초기 상태)";

            addToGameLog("말 " + piece.getId() + ":");
            addToGameLog("  위치: " + locationInfo);
            addToGameLog("  완주 여부: " + piece.isCompleted());
            addToGameLog("  업힌 말 수: " + piece.getStackedPieces().size());

            // 업힌 말이 있으면 자세히 출력
            if (!piece.getStackedPieces().isEmpty()) {
                addToGameLog("  업힌 말 목록:");
                for (Piece stackedPiece : piece.getStackedPieces()) {
                    addToGameLog("    - " + stackedPiece.getId());
                }
            }
            addToGameLog("-----------");
        }
        addToGameLog("===================");
    }

    /**
     * 기본 생성자
     */
    public Game() {
        this.players = new ArrayList<>();
        this.currentTurnIndex = 0;
        this.yut = new Yut();
        this.isGameFinished = false;
        this.hasExtraTurn = false;
        this.gameLog = new ArrayList<>();
        this.pendingYutResults = new ArrayList<>();
        this.captureExtraTurnUsed = false;
        this.isExtraTurnThrow = false;
    }

    /**
     * 게임 설정을 통한 초기화
     * @param settings 게임 설정
     */
    public void initialize(GameSettings settings) {
        this.gameSettings = settings;
        this.currentTurnIndex = 0;
        this.isGameFinished = false;
        this.winner = null;
        this.hasExtraTurn = false;
        this.gameLog.clear();
        this.pendingYutResults.clear();
        this.captureExtraTurnUsed = false;
        this.isExtraTurnThrow = false;

        // 보드 초기화
        this.board = new Board(settings.getBoardType());

        // 플레이어 초기화
        this.players.clear();
        for (int i = 0; i < settings.getPlayerCount(); i++) {
            Player player = new Player("Player " + (i + 1));

            // 플레이어당 말 생성
            for (int j = 0; j < settings.getPiecePerPlayer(); j++) {
                Piece piece = new Piece("P" + (i + 1) + "-" + (j + 1), player);
                player.addPiece(piece);
            }

            this.players.add(player);
        }

        // 초기 로그 추가
        addToGameLog("게임이 시작되었습니다. 보드 형태: " + settings.getBoardType() +
                ", 플레이어 수: " + settings.getPlayerCount() +
                ", 말 개수: " + settings.getPiecePerPlayer());
    }

    /**
     * 현재 턴의 플레이어 반환
     * @return 현재 턴 플레이어
     */
    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }

    /**
     * 다음 턴으로 전환
     */
    public void nextTurn() {
        if (!hasExtraTurn) {
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
            addToGameLog(getCurrentPlayer().getName() + "의 턴입니다.");
        } else {
            hasExtraTurn = false;
            captureExtraTurnUsed = false; // 다음 턴이 되면 초기화
            addToGameLog(getCurrentPlayer().getName() + "의 추가 턴입니다.");
        }

        // 디버깅: 턴 시작 시 모든 플레이어의 말 상태 출력
        addToGameLog("[디버그] === 턴 시작 시 게임 상태 ===");
        for (Player player : players) {
            debugPrintPlayerPieces(player);
        }
    }

    /**
     * 윷 던지기 (랜덤)
     * @return 윷 결과
     */
    public Yut.YutResult throwYut() {
        // 기존 코드에서는 hasExtraTurn이 true인 경우에도 초기화했지만,
        // 상대방 말을 잡아서 추가 턴을 얻은 경우에는 윷을 던지기 전이므로 초기화하면 안 됨
        if (!hasExtraTurn) {
            pendingYutResults.clear();
            isExtraTurnThrow = false;
        } else {
            // 이미 추가 턴이 있는 상태에서 던지는 경우
            isExtraTurnThrow = true;
        }
        
        // 상대방 말 잡기로 얻은 추가 턴일 경우, 윷을 던지면 사용한 것으로 표시
        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            captureExtraTurnUsed = true;
        }
        
        lastYutResult = yut.throwYut();
        addToGameLog(getCurrentPlayer().getName() + "이(가) 윷을 던져 " +
                lastYutResult.getName() + "(" + lastYutResult.getMoveCount() + "칸)가 나왔습니다.");

        // 윷/모 결과에 대한 추가 턴 처리
        if (lastYutResult == Yut.YutResult.YUT || lastYutResult == Yut.YutResult.MO) {
            hasExtraTurn = true;
            addToGameLog(getCurrentPlayer().getName() + "에게 추가 턴이 부여되었습니다. (윷/모)");
        }

        // 윷 결과 저장
        pendingYutResults.add(lastYutResult);

        return lastYutResult;
    }

    /**
     * 지정된 윷 결과 설정 (테스트용)
     * @param result 지정할 윷 결과
     * @return 지정된 윷 결과
     */
    public Yut.YutResult setSpecificYutResult(Yut.YutResult result) {
        // 기존 코드에서는 hasExtraTurn이 true인 경우에도 초기화했지만,
        // 상대방 말을 잡아서 추가 턴을 얻은 경우에는 윷을 던지기 전이므로 초기화하면 안 됨
        if (!hasExtraTurn) {
            pendingYutResults.clear();
            isExtraTurnThrow = false;
        } else {
            // 이미 추가 턴이 있는 상태에서 던지는 경우
            isExtraTurnThrow = true;
        }
        
        // 상대방 말 잡기로 얻은 추가 턴일 경우, 윷을 던지면 사용한 것으로 표시
        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            captureExtraTurnUsed = true;
        }
        
        lastYutResult = result;
        addToGameLog(getCurrentPlayer().getName() + "이(가) " +
                result.getName() + "(" + result.getMoveCount() + "칸)로 지정했습니다.");

        // 윷/모 결과에 대한 추가 턴 처리
        if (result == Yut.YutResult.YUT || result == Yut.YutResult.MO) {
            hasExtraTurn = true;
            addToGameLog(getCurrentPlayer().getName() + "에게 추가 턴이 부여되었습니다. (윷/모)");
        }

        // 윷 결과 저장
        pendingYutResults.add(result);

        return result;
    }

    /**
     * 현재 플레이어의 이동 가능한 말 목록 반환
     * @param player 플레이어
     * @param result 윷 결과
     * @return 이동 가능한 말 목록
     */
    public List<Piece> getMovablePieces(Player player, Yut.YutResult result) {
        List<Piece> movablePieces = new ArrayList<>();

        // 완주하지 않고 업혀있지 않은 말만 포함
        for (Piece piece : player.getPieces()) {
            if (!piece.isCompleted() && !piece.isCarried()) {
                movablePieces.add(piece);
            }
        }

        return movablePieces;
    }
    /**
     * 말 이동 실행
     * @param piece 이동할 말
     * @param result 윷 결과
     * @return 이동 후 위치
     */
    public Place movePiece(Piece piece, Yut.YutResult result) {
        // 요청한 결과가 pendingYutResults에 있는지 확인
        if (!pendingYutResults.contains(result)) {
            addToGameLog("[오류] 이동에 사용할 수 없는 윷 결과입니다.");
            return null;
        }

        // 현재 위치 (시작점이거나 이미 보드에 있는 경우)
        Place currentPlace = piece.getCurrentPlace();
        if (currentPlace == null) {
            // 시작점에서 출발
            currentPlace = board.getStartingPlace();
        }

        // 목적지 계산
        Place destination = board.calculateDestination(currentPlace, result);

        // 이동 실행
        piece.moveTo(destination);

        // 이동 로그 추가
        addToGameLog(getCurrentPlayer().getName() + "의 말 " + piece.getId() +
                "이(가) " + (currentPlace.getName() != null ? currentPlace.getName() : "시작점") +
                "에서 " + (destination.getName() != null ? destination.getName() : "도착점") +
                "으로 " + result.getName() + "(" + result.getMoveCount() + "칸) 만큼 이동했습니다.");

        // 사용한 윷 결과 제거
        pendingYutResults.remove(result);

        // 중심점 특별 처리
        if (destination.isCenter()) {
            // 중심점 잡기 확인
            boolean captured = checkCenterCapture(piece);
            if (!captured) {
                // 잡기가 발생하지 않았으면 먼저 중심점 업기 확인
                boolean stacked = checkCenterStacking(piece);
                
                // 중심점 업기 이후에도 같은 위치의 업기 확인 (C_1과 C_2의 말 업기를 위해)
                checkAndApplyGrouping(destination, piece);
            }
        }
        // 일반적인 잡기 확인
        else if (isCapture(destination)) {
            applyCapture(piece);
        }
        // 업기 확인
        else {
            checkAndApplyGrouping(destination, piece);
        }

        return destination;
    }
    /**
     * 중심점 간의 업기를 처리하는 메서드
     * @param piece 현재 이동한 말
     * @return 업기가 발생했으면 true, 아니면 false
     */
    public boolean checkCenterStacking(Piece piece) {
        Player player = piece.getPlayer();
        Place currentPlace = piece.getCurrentPlace();

        // 현재 위치가 중심점이 아니면 처리하지 않음
        if (currentPlace == null || !currentPlace.isCenter()) {
            return false;
        }

        // 모든 중심점 가져오기
        Map<String, Place> centerPlaces = board.getCenterPlaces();

        // 다른 중심점에 있는 같은 플레이어의 말 찾기
        List<Piece> samePiecesToStack = new ArrayList<>();

        for (Place centerPlace : centerPlaces.values()) {
            // 현재 위치가 아닌 다른 중심점
            if (!centerPlace.equals(currentPlace)) {
                // 해당 중심점에 있는 모든 말 확인
                for (Piece otherPiece : centerPlace.getPieces()) {
                    // 같은 플레이어의 말인 경우
                    if (otherPiece.getPlayer().equals(player) && !otherPiece.equals(piece)) {
                        samePiecesToStack.add(otherPiece);
                    }
                }
            }
        }

        // 같은 플레이어의 말이 없으면 처리하지 않음
        if (samePiecesToStack.isEmpty()) {
            return false;
        }

        // 같은 플레이어의 말이 있으면 업기 실행
        boolean stackingOccurred = false;

        for (Piece otherPiece : samePiecesToStack) {
            // 말 업기 실행
            boolean result = piece.stackPiece(otherPiece);
            if (result) {
                stackingOccurred = true;
                addToGameLog(player.getName() + "의 말 " + piece.getId() +
                        "이(가) 다른 중심점에 있던 " + otherPiece.getId() + "을(를) 업었습니다.");
            }
        }

        return stackingOccurred;
    }

    /**
     * 말이 다른 플레이어의 말을 잡을 수 있는지 확인
     * @param place 위치
     * @return 잡기 가능 여부
     */
    public boolean isCapture(Place place) {
        Player currentPlayer = getCurrentPlayer();

        // 해당 위치에 다른 플레이어의 말이 있는지 확인
        List<Piece> opponentPieces = place.getOpponentPieces(currentPlayer);
        return !opponentPieces.isEmpty();
    }

    /**
     * 말이 다른 플레이어의 말을 잡음
     * @param capturingPiece 잡는 말
     * @return 잡기 성공 여부
     */
    public boolean applyCapture(Piece capturingPiece) {
        Place currentPlace = capturingPiece.getCurrentPlace();
        if (currentPlace == null) {
            addToGameLog("[디버그] 잡기 실패: 현재 말의 위치가 null입니다.");
            return false;
        }

        // 중요한 변경: 현재 턴이 아닌 말의 소유자를 기준으로 함
        Player capturingPlayer = capturingPiece.getPlayer();

        // 해당 위치에서 잡는 말의 소유자가 아닌 다른 플레이어의 말들을 가져옴
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece piece : currentPlace.getPieces()) {
            if (!piece.getPlayer().equals(capturingPlayer) && !piece.equals(capturingPiece)) {
                opponentPieces.add(piece);
            }
        }

        if (opponentPieces.isEmpty()) {
            addToGameLog("[디버그] 잡기 실패: 상대방 말이 없습니다.");
            return false;
        }

        // 잡기 실행
        for (Piece opponentPiece : opponentPieces) {
            Player opponentPlayer = opponentPiece.getPlayer();

            // 디버깅: 잡히는 말 정보 출력
            addToGameLog("[디버그] 잡히는 말 정보:");
            addToGameLog("말 ID: " + opponentPiece.getId());
            addToGameLog("소유자: " + opponentPlayer.getName());
            addToGameLog("업힌 말 개수: " + opponentPiece.getStackedPieces().size());

            // 업힌 말 목록을 복사 (ConcurrentModificationException 방지)
            List<Piece> stackedPieces = new ArrayList<>(opponentPiece.getStackedPieces());

            // 로그에 잡기 기록
            addToGameLog(capturingPlayer.getName() + "의 말 " + capturingPiece.getId() +
                    "이(가) " + opponentPlayer.getName() + "의 말 " +
                    opponentPiece.getId() + "을(를) 잡았습니다.");

            // 업힌 말들 먼저 처리
            if (!stackedPieces.isEmpty()) {
                addToGameLog("업힌 말 " + stackedPieces.size() + "개도 함께 시작점으로 돌아갑니다.");

                // 각 업힌 말을 직접 처리
                for (Piece stackedPiece : stackedPieces) {
                    addToGameLog("업힌 말 " + stackedPiece.getId() + "이(가) 시작점으로 돌아갑니다.");

                    // 중요: carriedBy 관계 해제
                    stackedPiece.setCarriedBy(null);

                    // 먼저 업힌 말을 스택에서 제거
                    opponentPiece.getStackedPieces().remove(stackedPiece);

                    // 업힌 말을 시작점으로 이동
                    stackedPiece.moveTo(board.getStartingPlace());
                }
            }

            // 현재 위치에서 말 제거 확인
            boolean removed = currentPlace.removePiece(opponentPiece);
            addToGameLog("[디버그] 말이 현재 위치에서 제거되었는지: " + removed);

            // 메인 말 시작점으로 이동 (실제 보드의 시작 위치로)
            opponentPiece.moveTo(board.getStartingPlace());
            opponentPiece.clearStackedPieces();
        }

        // 디버깅: 잡기 후 상태 출력
        addToGameLog("[디버그] === 잡기 후 상태 ===");
        debugPrintPlaceInfo(currentPlace);
        debugPrintPlaceInfo(board.getStartingPlace());

        // 잡기 후 추가 턴 부여 - 현재 턴 플레이어에게만 추가 턴 부여
        Player currentPlayer = getCurrentPlayer();
        if (capturingPlayer.equals(currentPlayer)) {
            hasExtraTurn = true;
            captureExtraTurnUsed = false; // 새로운 잡기 추가 턴
            // 자동으로 DO 결과를 추가하지 않음 (사용자가 다시 윷을 던져야 함)
            addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (잡기)");
        } else {
            addToGameLog("[디버그] 현재 턴 플레이어(" + currentPlayer.getName() +
                    ")가 아닌 " + capturingPlayer.getName() +
                    "의 말이 상대를 잡았지만, 추가 턴은 부여되지 않습니다.");
        }

        return true;
    }
    /**
     * 같은 플레이어의 말 업기
     * @param piece1 기준 말
     * @param piece2 업힐 말
     * @return 업기 성공 여부
     */
    public boolean applyGrouping(Piece piece1, Piece piece2) {
        // 디버깅: 업기 전 상태 출력
        addToGameLog("[디버그] === 업기 전 상태 ===");
        addToGameLog("업는 말: " + piece1.getId() + ", 업히는 말: " + piece2.getId());

        if (piece1.getCurrentPlace() != null) {
            debugPrintPlaceInfo(piece1.getCurrentPlace());
        } else {
            addToGameLog("[디버그] 업는 말의 현재 위치가 null입니다.");
        }

        // 같은 플레이어의 말인지 확인
        if (!piece1.getPlayer().equals(piece2.getPlayer())) {
            addToGameLog("업기 실패: 서로 다른 플레이어의 말입니다.");
            return false;
        }

        // 같은 위치에 있는지 확인
        Place place1 = piece1.getCurrentPlace();
        Place place2 = piece2.getCurrentPlace();

        if (place1 == null) {
            addToGameLog("[디버그] 업기 실패: 업는 말이 보드 위에 없습니다.");
            return false;
        }

        if (place2 == null) {
            addToGameLog("[디버그] 업기 실패: 업히는 말이 보드 위에 없습니다. (이미 업힌 상태일 수 있음)");
            return false;
        }
        
        // C_1과 C_2는 같은 위치로 취급
        boolean sameLocation = place1.equals(place2);
        // 한 말은 C_1에 있고 다른 말은 C_2에 있는 경우
        if (!sameLocation && 
            ((place1.getId().equals("C_1") && place2.getId().equals("C_2")) || 
             (place1.getId().equals("C_2") && place2.getId().equals("C_1")))) {
            sameLocation = true;
        }

        if (!sameLocation) {
            addToGameLog("[디버그] 업기 실패: 두 말이 서로 다른 위치에 있습니다. place1: " +
                    place1.getId() + ", place2: " + place2.getId());
            return false;
        }

        // 업기 전 말 목록 출력
        addToGameLog("[디버그] 업기 전 위치에 있는 말 목록:");
        for (Piece p : place1.getPieces()) {
            addToGameLog("- " + p.getId() + " (소유자: " + p.getPlayer().getName() + ")");
        }

        // 업기 실행
        boolean stackResult = piece1.stackPiece(piece2);

        if (stackResult) {
            addToGameLog(piece1.getPlayer().getName() + "의 말 " + piece1.getId() +
                    "이(가) " + piece2.getId() + "을(를) 업었습니다.");
        } else {
            addToGameLog("업기 실패: stackPiece 메서드가 false를 반환했습니다.");
            return false;
        }

        // 업기 후 위치에 있는 말 목록 출력
        addToGameLog("[디버그] 업기 후 위치에 있는 말 목록:");
        for (Piece p : place1.getPieces()) {
            addToGameLog("- " + p.getId() + " (소유자: " + p.getPlayer().getName() + ")");
        }

        // 디버깅: 업기 후 상태 출력
        addToGameLog("[디버그] === 업기 후 상태 ===");
        if (piece1.getCurrentPlace() != null) {
            debugPrintPlaceInfo(piece1.getCurrentPlace());
        }
        addToGameLog("[디버그] 업는 말 " + piece1.getId() + "에 업힌 말 개수: " + piece1.getStackedPieces().size());

        return true;
    }

    /**
     * 현재 위치에서 같은 플레이어의 모든 말 업기 확인 및 처리
     * @param place 현재 위치
     * @param currentPiece 현재 말
     */
    public void checkAndApplyGrouping(Place place, Piece currentPiece) {
        if (place == null || currentPiece == null) {
            return;
        }

        Player currentPlayer = currentPiece.getPlayer();
        List<Piece> piecesAtPlace = new ArrayList<>(place.getPieces());
        
        // 현재 위치가 C_1 또는 C_2인 경우, 다른 중심점에 있는 말도 고려
        if (place.isCenter() && (place.getId().equals("C_1") || place.getId().equals("C_2"))) {
            String otherCenterId = place.getId().equals("C_1") ? "C_2" : "C_1";
            Place otherCenter = board.getPlaceById(otherCenterId);
            
            if (otherCenter != null) {
                // 다른 중심점에 있는 말도 추가
                piecesAtPlace.addAll(otherCenter.getPieces());
            }
        }

        // 현재 위치에 같은 플레이어의 말이 있는지 확인
        List<Piece> samePlayerPieces = new ArrayList<>();
        for (Piece p : piecesAtPlace) {
            if (p.getPlayer().equals(currentPlayer) && !p.equals(currentPiece)) {
                samePlayerPieces.add(p);
            }
        }

        // 같은 위치에 같은 플레이어의 말이 있으면 처리
        if (!samePlayerPieces.isEmpty()) {
            // 이동한 말이 다른 말들을 업음 (테스트 의도)
            for (Piece otherPiece : samePlayerPieces) {
                boolean result = applyGrouping(currentPiece, otherPiece);
                if (result) {
                    addToGameLog(currentPlayer.getName() + "의 말 " + currentPiece.getId() +
                            "이(가) " + otherPiece.getId() + "을(를) 업었습니다.");
                }
            }
        }
    }
    /**
     * 중심점 간의 잡기를 처리하는 메서드
     * @param piece 현재 이동한 말
     * @return 잡기가 발생했으면 true, 아니면 false
     */
    public boolean checkCenterCapture(Piece piece) {
        Player currentPlayer = piece.getPlayer();
        Place currentPlace = piece.getCurrentPlace();

        // 현재 위치가 중심점이 아니면 처리하지 않음
        if (currentPlace == null || !currentPlace.isCenter()) {
            return false;
        }

        // 모든 중심점 가져오기
        Map<String, Place> centerPlaces = board.getCenterPlaces();

        // 다른 중심점에 있는 상대방 말 찾기
        List<Piece> opponentPiecesToCapture = new ArrayList<>();

        for (Place centerPlace : centerPlaces.values()) {
            // 현재 위치가 아닌 다른 중심점
            if (!centerPlace.equals(currentPlace)) {
                // 해당 중심점에 있는 모든 말 확인
                for (Piece otherPiece : centerPlace.getPieces()) {
                    // 상대방 말인 경우 (자신의 말이 아닌 경우)
                    if (!otherPiece.getPlayer().equals(currentPlayer)) {
                        opponentPiecesToCapture.add(otherPiece);
                    }
                }
            }
        }

        // 상대방 말이 없으면 처리하지 않음
        if (opponentPiecesToCapture.isEmpty()) {
            return false;
        }

        // 상대방 말이 있으면 잡기 실행
        for (Piece opponentPiece : opponentPiecesToCapture) {
            // 로그 기록
            addToGameLog(currentPlayer.getName() + "의 말 " + piece.getId() +
                    "이(가) 중심점에서 " + opponentPiece.getPlayer().getName() +
                    "의 말 " + opponentPiece.getId() + "을(를) 잡았습니다.");

            // 업힌 말 처리
            if (!opponentPiece.getStackedPieces().isEmpty()) {
                addToGameLog("업힌 말 " + opponentPiece.getStackedPieces().size() +
                        "개도 함께 시작점으로 돌아갑니다.");
            }

            // 말을 현재 위치에서 제거
            Place opponentPlace = opponentPiece.getCurrentPlace();
            if (opponentPlace != null) {
                opponentPlace.removePiece(opponentPiece);
            }

            // 업힌 말 해제
            opponentPiece.unstackAllPieces();

            // 시작점으로 이동
            opponentPiece.moveTo(board.getStartingPlace());
        }

        // 현재 턴 플레이어에게 추가 턴 부여
        hasExtraTurn = true;
        captureExtraTurnUsed = false; // 새로운 잡기 추가 턴
        // 자동으로 DO 결과를 추가하지 않음 (사용자가 다시 윷을 던져야 함)
        addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (중심점 잡기)");

        return true;
    }
    /**
     * 플레이어의 모든 말이 완주했는지 확인 (승리 조건)
     * @param player 확인할 플레이어
     * @return 승리 여부
     */
    public boolean isPlayerWinner(Player player) {
        return player.isAllPiecesCompleted();
    }

    /**
     * 게임 종료 조건 확인
     * @return 게임 종료 여부
     */
    public boolean checkGameEnd() {
        for (Player player : players) {
            if (isPlayerWinner(player)) {
                isGameFinished = true;
                winner = player;
                addToGameLog(player.getName() + "이(가) 게임에서 승리했습니다!");
                return true;
            }
        }
        return false;
    }

    /**
     * 턴 종료 시 호출되는 메소드
     * 추가 턴이 없거나, 추가 턴이 있지만 모든 윷 결과를 사용한 경우 다음 플레이어로 턴 전환
     */
    public void endTurnIfNoExtraTurn() {
        // 추가 턴이 있고, 윷 결과가 비어있는 경우 (상대방 말을 잡아서 추가 턴을 얻은 경우)
        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            // 추가 턴에서 던진 윷 결과를 모두 사용했거나, 상대방 말 잡기로 인한 추가 턴이 이미 사용된 경우
            if (isExtraTurnThrow || captureExtraTurnUsed) {
                // 추가 턴 사용 완료 - 다음 플레이어로 턴 전환
                hasExtraTurn = false;
                isExtraTurnThrow = false;
                captureExtraTurnUsed = false;
                nextTurn();
                return;
            }
            
            // 아직 추가 턴을 사용하지 않은 경우 (윷/모 결과로 얻은 추가 턴)
            addToGameLog(getCurrentPlayer().getName() + "의 추가 턴이 부여되었습니다. 윷을 던지세요.");
            return;
        }
        
        // 추가 턴이 없는 경우, 다음 플레이어로 턴 전환
        if (!hasExtraTurn) {
            nextTurn();
            pendingYutResults.clear();
        } 
        // 추가 턴이 있지만, 모든 윷 결과를 사용한 경우 (윷/모가 나온 후 모든 결과를 사용한 경우)
        else if (pendingYutResults.isEmpty()) {
            // 이전 수정: 추가 턴을 정상적으로 사용한 경우에도 hasExtraTurn이 true로 유지됨
            // 이 부분은 이제 위의 if 조건문에서 처리되므로 여기까지 코드가 오지 않을 것임
            // 예상치 못한 상황을 대비하여 남겨둠
            hasExtraTurn = false;
            isExtraTurnThrow = false;
            nextTurn();
            addToGameLog(getCurrentPlayer().getName() + "의 턴이 시작되었습니다.");
        } 
        // 추가 턴이 있고, 윷 결과가 남아있는 경우
        else {
            addToGameLog(getCurrentPlayer().getName() + "의 추가 턴이 남아있습니다. 윷 결과: " + 
                    formatPendingResults());
        }
    }
    
    /**
     * 저장된 윷 결과 목록을 문자열로 변환
     * @return 윷 결과 문자열
     */
    private String formatPendingResults() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pendingYutResults.size(); i++) {
            Yut.YutResult result = pendingYutResults.get(i);
            sb.append(result.getName()).append("(").append(result.getMoveCount()).append("칸)");
            if (i < pendingYutResults.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * 게임 재시작
     * @param settings 새 게임 설정 (null이면 기존 설정 유지)
     */
    public void restartGame(GameSettings settings) {
        if (settings != null) {
            initialize(settings);
        } else if (gameSettings != null) {
            initialize(gameSettings);
        }
        addToGameLog("게임이 재시작되었습니다.");
    }



    /**
     * 게임 로그에 메시지 추가
     * @param message 로그 메시지
     */
    private void addToGameLog(String message) {
        gameLog.add(message);
    }

    // Getter 메소드들

    /**
     * 현재 플레이어 목록 반환
     * @return 플레이어 목록
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * 현재 턴 인덱스 반환
     * @return 현재 턴 인덱스
     */
    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    /**
     * 게임 보드 반환
     * @return 게임 보드
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 게임 설정 반환
     * @return 게임 설정
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * 게임 종료 여부 반환
     * @return 게임 종료 여부
     */
    public boolean isGameFinished() {
        return isGameFinished;
    }

    /**
     * 승리자 반환
     * @return 승리 플레이어 (없으면 null)
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * 마지막 윷 결과 반환
     * @return 마지막 윷 결과
     */
    public Yut.YutResult getLastYutResult() {
        return lastYutResult;
    }

    /**
     * 마지막 윷 결과 설정 (테스트용)
     * @param result 윷 결과
     */
    void setLastYutResult(Yut.YutResult result) {
        this.lastYutResult = result;
    }

    /**
     * 추가 턴 여부 반환
     * @return 추가 턴 여부
     */
    public boolean hasExtraTurn() {
        return hasExtraTurn;
    }

    /**
     * 게임 로그 반환
     * @return 게임 로그 목록
     */
    public List<String> getGameLog() {
        return new ArrayList<>(gameLog);
    }

    /**
     * 윷/모 결과를 저장하는 리스트 반환
     * @return 윷/모 결과를 저장하는 리스트
     */
    public List<Yut.YutResult> getPendingYutResults() {
        return new ArrayList<>(pendingYutResults);
    }
}