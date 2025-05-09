package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.viewfx.GameSetupSceneFX;

public class YutGameAppFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // JavaFX 초기 설정
        primaryStage.setTitle("윷놀이 게임 설정");

        // GameSetupSceneFX는 JavaFX 설정 화면 클래스
        GameSetupSceneFX.setup(primaryStage);  // 정적 메서드로 씬 구성 및 표시

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  // JavaFX 앱 실행
    }
}
