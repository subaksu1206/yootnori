package org.example.viewfx;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.controller.GameController;
import org.example.model.Board;
import org.example.model.GameSettings;

public class GameSetupSceneFX {

    public static void setup(Stage primaryStage) {
        // ▼ 콤보박스 설정값 ▼
        ComboBox<String> boardTypeCombo = new ComboBox<>();
        boardTypeCombo.getItems().addAll("사각형", "오각형", "육각형");
        boardTypeCombo.getSelectionModel().selectFirst();

        ComboBox<Integer> playerCountCombo = new ComboBox<>();
        playerCountCombo.getItems().addAll(2, 3, 4);
        playerCountCombo.getSelectionModel().selectFirst();

        ComboBox<Integer> pieceCountCombo = new ComboBox<>();
        pieceCountCombo.getItems().addAll(2, 3, 4, 5);
        pieceCountCombo.getSelectionModel().selectFirst();

        // ▼ "게임 시작" 버튼 ▼
        Button startButton = new Button("게임 시작");
        startButton.setOnAction(e -> {
            // 설정 값 가져오기
            Board.BoardType boardType;
            switch (boardTypeCombo.getSelectionModel().getSelectedIndex()) {
                case 1: boardType = Board.BoardType.PENTAGON; break;
                case 2: boardType = Board.BoardType.HEXAGON; break;
                default: boardType = Board.BoardType.SQUARE;
            }

            int playerCount = playerCountCombo.getValue();
            int pieceCount = pieceCountCombo.getValue();

            GameSettings settings = new GameSettings(playerCount, pieceCount, boardType);
            GameController controller = new GameController(settings);

            // 게임 프레임 띄우기
            GameFrameFX gameFrameFX = new GameFrameFX(controller);
            gameFrameFX.start(primaryStage);  // 기존 Stage에 게임 화면 설정
        });

        // ▼ UI 배치 ▼
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        root.getChildren().addAll(
                createLabeledBox("보드 형태:", boardTypeCombo),
                createLabeledBox("플레이어 수:", playerCountCombo),
                createLabeledBox("말 개수:", pieceCountCombo),
                startButton
        );

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
    }

    // 레이블 + 컴포넌트를 한 줄로 배치하는 유틸 메서드
    private static HBox createLabeledBox(String labelText, Control control) {
        Label label = new Label(labelText);
        label.setPrefWidth(100);
        HBox box = new HBox(10, label, control);
        return box;
    }
}
