package org.example.viewfx;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.controller.GameController;
import org.example.model.Game;
import org.example.model.Piece;
import org.example.model.Yut;

import java.util.List;

public class GameFrameFX {
    private GameController controller;
    private Label currentPlayerLabel;
    private Label yutResultLabel;
    private TextArea logTextArea;
    private ListView<String> pieceListView;
    private ListView<String> pendingYutListView;
    private Label pendingYutLabel;
    private ComboBox<String> yutComboBox;
    private GameBoardCanvasFX boardCanvas;

    public GameFrameFX(GameController controller) {
        this.controller = controller;
    }

    public void start(Stage stage) {
        // 보드판 (중앙)
        boardCanvas = new GameBoardCanvasFX(controller);

        // 윷 던지기 버튼
        Button throwYutButton = new Button("랜덤 윷 던지기");
        throwYutButton.setOnAction(e -> {
            Yut.YutResult result = controller.throwYut();
            updateYutResult(result);
            updatePieceList();
            updatePendingYutList();
        });

        Button specificYutButton = new Button("지정 윷 던지기");
        yutComboBox = new ComboBox<>();
        yutComboBox.getItems().addAll("빽도", "도", "개", "걸", "윷", "모");
        yutComboBox.getSelectionModel().selectFirst();

        specificYutButton.setOnAction(e -> {
            String selected = yutComboBox.getValue();
            Yut.YutResult result = switch (selected) {
                case "빽도" -> Yut.YutResult.BACKDO;
                case "도" -> Yut.YutResult.DO;
                case "개" -> Yut.YutResult.GAE;
                case "걸" -> Yut.YutResult.GEOL;
                case "윷" -> Yut.YutResult.YUT;
                case "모" -> Yut.YutResult.MO;
                default -> Yut.YutResult.DO;
            };
            controller.setSpecificYutResult(result);
            updateYutResult(result);
            updatePieceList();
            updatePendingYutList();
        });

        // 이동 가능한 말 리스트
        pieceListView = new ListView<>();

        // 저장된 윷 결과 리스트
        pendingYutLabel = new Label("이동에 사용할 윷 결과:");
        pendingYutListView = new ListView<>();

        // 말 이동 버튼
        Button moveButton = new Button("말 이동");
        moveButton.setOnAction(e -> {
            int selectedPieceIdx = pieceListView.getSelectionModel().getSelectedIndex();
            List<Piece> movablePieces = controller.getMovablePieces();

            if (selectedPieceIdx < 0 || selectedPieceIdx >= movablePieces.size()) return;

            int selectedYutIdx = pendingYutListView.getSelectionModel().getSelectedIndex();
            Yut.YutResult result = controller.getPendingYutResults().isEmpty() ? null :
                    (selectedYutIdx >= 0 ? controller.getPendingYutResults().get(selectedYutIdx)
                            : controller.getPendingYutResults().get(0));

            controller.movePiece(movablePieces.get(selectedPieceIdx), result);
            updatePendingYutList();
        });

        // 정보 패널
        currentPlayerLabel = new Label("현재 턴: Player 1");
        yutResultLabel = new Label("윷 결과: 없음");

        VBox infoBox = new VBox(10, currentPlayerLabel, yutResultLabel);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

        // 로그 텍스트 영역
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setPrefRowCount(15);

        VBox controlBox = new VBox(10,
                new HBox(10, throwYutButton, specificYutButton, yutComboBox),
                pendingYutLabel,
                pendingYutListView,
                new Label("이동 가능한 말:"),
                pieceListView,
                moveButton,
                infoBox,
                new Label("게임 로그:"),
                logTextArea
        );
        controlBox.setPadding(new Insets(10));
        controlBox.setPrefWidth(300);

        BorderPane root = new BorderPane();
        root.setCenter(boardCanvas);
        root.setRight(controlBox);

        Scene scene = new Scene(root, 1024, 768);
        stage.setScene(scene);
        stage.setTitle("윷놀이 게임");
        stage.show();

        controller.setUI(null, boardCanvas);
        updateGameInfo();
    }

    private void updateYutResult(Yut.YutResult result) {
        if (result != null) {
            yutResultLabel.setText("마지막 윷 결과: " + result.getName() + " (" + result.getMoveCount() + "칸)");
        } else {
            yutResultLabel.setText("윷 결과: 없음");
        }
    }

    private void updatePieceList() {
        pieceListView.getItems().clear();
        List<Piece> pieces = controller.getMovablePieces();
        for (Piece piece : pieces) {
            String location = piece.getCurrentPlace() != null ? piece.getCurrentPlace().getName() : "시작점";
            String stackInfo = piece.getStackedPieces().isEmpty() ? "" : " (업힌 말: " + piece.getStackedPieces().size() + ")";
            String carriedInfo = piece.isCarried() ? " [" + piece.getCarriedBy().getId() + "에 업힘]" : "";
            pieceListView.getItems().add(piece.getId() + " - " + location + stackInfo + carriedInfo);
        }
    }

    private void updatePendingYutList() {
        pendingYutListView.getItems().clear();
        List<Yut.YutResult> results = controller.getPendingYutResults();
        for (Yut.YutResult r : results) {
            pendingYutListView.getItems().add(r.getName() + " (" + r.getMoveCount() + "칸)");
        }
        pendingYutLabel.setText(results.isEmpty() ? "이동에 사용할 윷 결과가 없습니다." :
                "이동에 사용할 윷 결과 선택 (" + results.size() + "개):");
    }

    public void updateGameInfo() {
        Game game = controller.getGame();
        currentPlayerLabel.setText("현재 턴: " + game.getCurrentPlayer().getName());

        logTextArea.clear();
        for (String log : game.getGameLog()) {
            logTextArea.appendText(log + "\n");
        }
        updateYutResult(game.getLastYutResult());
        updatePieceList();
        updatePendingYutList();
    }
}
