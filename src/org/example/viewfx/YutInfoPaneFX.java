package org.example.viewfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.model.Yut;

public class YutInfoPaneFX extends Canvas {
    private Yut.YutResult currentResult;

    public YutInfoPaneFX() {
        super(200, 120); // 크기 설정
    }

    public void setYutResult(Yut.YutResult result) {
        this.currentResult = result;
        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (currentResult == null) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", 14));
            gc.fillText("윷을 던져주세요", 40, 60);
            return;
        }

        double stickWidth = 20;
        double stickHeight = 60;
        double spacing = 20;
        double startX = (getWidth() - (stickWidth * 4 + spacing * 3)) / 2;
        double startY = 20;

        switch (currentResult) {
            case BACKDO -> drawSticks(gc, new boolean[]{true, false, false, false}, startX, startY, stickWidth, stickHeight);
            case DO -> drawSticks(gc, new boolean[]{false, true, true, true}, startX, startY, stickWidth, stickHeight);
            case GAE -> drawSticks(gc, new boolean[]{false, false, true, true}, startX, startY, stickWidth, stickHeight);
            case GEOL -> drawSticks(gc, new boolean[]{false, true, true, true}, startX, startY, stickWidth, stickHeight);
            case YUT -> drawSticks(gc, new boolean[]{false, false, false, false}, startX, startY, stickWidth, stickHeight);
            case MO -> drawSticks(gc, new boolean[]{true, true, true, true}, startX, startY, stickWidth, stickHeight);
        }

        // 결과 텍스트
        String resultText = currentResult.getName() + " (" + currentResult.getMoveCount() + "칸)";
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 14));
        gc.fillText(resultText, (getWidth() - gc.getFont().getSize() * resultText.length() / 2.0) / 2, getHeight() - 10);
    }

    private void drawSticks(GraphicsContext gc, boolean[] isBellyArray, double x, double y, double width, double height) {
        for (int i = 0; i < 4; i++) {
            double stickX = x + i * (width + 20);
            drawYutStick(gc, stickX, y, width, height, isBellyArray[i]);
        }
    }

    private void drawYutStick(GraphicsContext gc, double x, double y, double width, double height, boolean isBelly) {
        gc.setFill(Color.BURLYWOOD);
        gc.fillRoundRect(x, y, width, height, 5, 5);
        gc.setStroke(Color.BLACK);
        gc.strokeRoundRect(x, y, width, height, 5, 5);

        if (isBelly) {
            gc.setStroke(Color.BLACK);
            gc.strokeLine(x + width / 2, y + 10, x + width / 2, y + height - 10);
        }
    }
}
