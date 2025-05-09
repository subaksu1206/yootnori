package org.example.controller;

import org.example.model.*;
import org.example.viewfx.GameBoardCanvasFX;
import org.example.viewfx.GameFrameFX;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Game game;
    private GameFrameFX gameFrameFX;
    private GameBoardCanvasFX boardCanvasFX;

    public GameController(GameSettings settings) {
        game = new Game();
        game.initialize(settings);
    }

    public void setUI(GameFrameFX gameFrameFX, GameBoardCanvasFX boardCanvasFX) {
        this.gameFrameFX = gameFrameFX;
        this.boardCanvasFX = boardCanvasFX;
        updateUI();
    }

    public Yut.YutResult throwYut() {
        Yut.YutResult result = game.throwYut();
        updateUI();
        return result;
    }

    public Yut.YutResult setSpecificYutResult(Yut.YutResult result) {
        Yut.YutResult setResult = game.setSpecificYutResult(result);
        updateUI();
        return setResult;
    }

    public Place movePiece(Piece piece, Yut.YutResult yutResult) {
        if (game.getPendingYutResults().isEmpty()) {
            System.out.println("[알림] 먼저 윷을 던져야 합니다.");
            return null;
        }

        if (yutResult == null) {
            yutResult = game.getPendingYutResults().get(0);
        }

        Place destination = game.movePiece(piece, yutResult);
        if (destination == null) {
            System.out.println("[알림] 이동할 수 없습니다.");
            return null;
        }

        if (game.checkGameEnd()) {
            System.out.println("[게임 종료] " + game.getWinner().getName() + "님이 승리했습니다!");
            System.exit(0);
        } else {
            game.endTurnIfNoExtraTurn();
        }

        updateUI();
        return destination;
    }

    public Place movePiece(Piece piece) {
        return movePiece(piece, null);
    }

    public List<Piece> getMovablePieces() {
        if (game.getPendingYutResults().isEmpty()) return new ArrayList<>();

        Yut.YutResult firstResult = game.getPendingYutResults().get(0);
        List<Piece> allPieces = game.getMovablePieces(game.getCurrentPlayer(), firstResult);
        List<Piece> validMovablePieces = new ArrayList<>();

        for (Piece piece : allPieces) {
            if (!piece.isCarried()) validMovablePieces.add(piece);
        }

        return validMovablePieces;
    }

    public List<Yut.YutResult> getPendingYutResults() {
        return game.getPendingYutResults();
    }

    public void updateUI() {
        if (boardCanvasFX != null) boardCanvasFX.updateBoard();
        if (gameFrameFX != null) gameFrameFX.updateGameInfo();
    }

    public Game getGame() {
        return game;
    }
}
