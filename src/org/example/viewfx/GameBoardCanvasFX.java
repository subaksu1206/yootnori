package org.example.viewfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.controller.GameController;
import javafx.scene.text.Text;
import org.example.model.Board.BoardType;
import java.util.Arrays;
import org.example.model.Piece;
import org.example.model.Place;

import java.util.List;

public class GameBoardCanvasFX extends Canvas {
    private GameController controller;

    public GameBoardCanvasFX(GameController controller) {
        super(700, 700); // 기본 크기
        this.controller = controller;
        setFocusTraversable(true);
        setOnMouseClicked(e -> draw()); // 클릭 시 다시 그리기 (디버깅용)
    }

    public void updateBoard() {
        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        BoardType boardType = controller.getGame().getGameSettings().getBoardType();

        switch (boardType) {
            case SQUARE:
                drawSquareBoard(gc);
                break;
            case PENTAGON:
                drawPentagonBoard(gc);
                break;
            case HEXAGON:
                drawHexagonBoard(gc);
                break;
            default:
                drawSquareBoard(gc);
        }
    }





    private void drawSquareBoard(GraphicsContext gc) {
        double padding = 50;
        double cellSize = 70; // 크기 적당히 크게 설정
        double radius = 25; // 원의 반지름 (크기 조정 가능)

        gc.clearRect(0, 0, getWidth(), getHeight());

        // 총 외곽 24개 점 + 중앙 1개 + 대각선 위 추가 점 8개 = 33개
        double[][] positions = new double[33][2];

// 모서리 위치 (4개)
        positions[0] = new double[]{padding, padding};                             // 좌상단
        positions[6] = new double[]{padding + 5 * cellSize, padding};              // 우상단
        positions[12] = new double[]{padding + 5 * cellSize, padding + 5 * cellSize}; // 우하단
        positions[18] = new double[]{padding, padding + 5 * cellSize};             // 좌하단

// 상단 라인 (1~5)
        for (int i = 1; i <= 5; i++)
            positions[i] = new double[]{padding + i * cellSize, padding};

// 우측 라인 (7~11)
        for (int i = 1; i <= 5; i++)
            positions[6 + i] = new double[]{padding + 5 * cellSize, padding + i * cellSize};

// 하단 라인 (13~17)
        for (int i = 1; i <= 5; i++)
            positions[12 + i] = new double[]{padding + (5 - i) * cellSize, padding + 5 * cellSize};

// 좌측 라인 (19~23)
        for (int i = 1; i <= 5; i++)
            positions[18 + i] = new double[]{padding, padding + (5 - i) * cellSize};

// 중앙점
        positions[24] = new double[]{padding + 2.5 * cellSize, padding + 2.5 * cellSize}; // X 중앙

// 대각선 상의 추가 점 (총 8개 - 중앙 제외하고 각 대각선당 3개씩 추가)
// 좌상 ↔ 우하 대각선 (점 3개 추가)
        positions[25] = new double[]{padding + 1 * cellSize, padding + 1 * cellSize};
        positions[26] = new double[]{padding + 1.75 * cellSize, padding + 1.75 * cellSize};
        positions[27] = new double[]{padding + 3.25 * cellSize, padding + 3.25 * cellSize};
        positions[28] = new double[]{padding + 4 * cellSize, padding + 4 * cellSize};

// 우상 ↔ 좌하 대각선 (점 3개 추가)
        positions[29] = new double[]{padding + 4 * cellSize, padding + 1 * cellSize};
        positions[30] = new double[]{padding + 3.25 * cellSize, padding + 1.75 * cellSize};
        positions[31] = new double[]{padding + 1.75 * cellSize, padding + 3.25 * cellSize};
        positions[32] = new double[]{padding + 1 * cellSize, padding + 4 * cellSize};



        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // 외곽 라인 연결
        for (int i = 0; i < 24; i++) {
            double[] start = positions[i];
            double[] end = positions[(i + 1) % 24];
            gc.strokeLine(start[0], start[1], end[0], end[1]);
        }

        // 중앙 십자 대각선 연결
        gc.strokeLine(positions[0][0], positions[0][1], positions[12][0], positions[12][1]);
        gc.strokeLine(positions[6][0], positions[6][1], positions[18][0], positions[18][1]);

        // 내부 대각선 (C1~C3, C2~C4)
        gc.strokeLine(positions[25][0], positions[25][1], positions[27][0], positions[27][1]);
        gc.strokeLine(positions[26][0], positions[26][1], positions[28][0], positions[28][1]);

        // 위치 원 그리기
        gc.setFill(Color.WHITE);
        for (int i = 0; i < positions.length; i++) {
            double x = positions[i][0] - radius / 2;
            double y = positions[i][1] - radius / 2;
            gc.fillOval(x, y, radius, radius);
            gc.strokeOval(x, y, radius, radius);

            Place place = controller.getGame().getBoard().getPlaceById(String.valueOf(i));
            String text = (place != null && place.getName() != null) ? place.getName() : "?";

            // 위치 텍스트
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(12));

            Place places = controller.getGame().getBoard().getPlaceById(i < 24 ? String.valueOf(i)
                    : (i == 24 ? "X"
                    : "C" + (i - 24)));


            gc.fillText(text, positions[i][0] - radius / 3, positions[i][1] + radius / 3);

            gc.setFill(Color.WHITE);
        }

        // 중요 지점 강조
        highlightCircle(gc, positions[0], radius, Color.GREEN); // 시작점 S
        highlightCircle(gc, positions[6], radius, Color.GOLD);  // 분기점 (상단 우측)
        highlightCircle(gc, positions[12], radius, Color.GOLD); // 분기점 (하단 우측)
        highlightCircle(gc, positions[18], radius, Color.GOLD); // 분기점 (하단 좌측)
        highlightCircle(gc, positions[24], radius, Color.GOLDENROD); // 중앙 X
    }

    // 위치 강조 메서드
    private void highlightCircle(GraphicsContext gc, double[] position, double radius, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        double x = position[0] - radius / 2;
        double y = position[1] - radius / 2;
        gc.strokeOval(x, y, radius, radius);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
    }

    private void drawPentagonBoard(GraphicsContext gc) {
        double padding = 50;
        double size = 500;
        double radius = 25;
        double centerX = padding + size / 2;
        double centerY = padding + size / 2;
        double outerRadius = size / 2;

        // 외곽 꼭짓점 5개 위치 계산
        double[][] outerPoints = new double[5][2];
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2;
            outerPoints[i][0] = centerX + outerRadius * Math.cos(angle);
            outerPoints[i][1] = centerY + outerRadius * Math.sin(angle);
        }

        // 외곽 점 25개 계산
        double[][] positions = new double[36][2]; // 0~24 외곽, 25 = 중앙, 26~35 = C1~C10
        int idx = 0;
        for (int i = 0; i < 5; i++) {
            int next = (i + 1) % 5;
            for (int j = 0; j < 5; j++) {
                double x = outerPoints[i][0] + (outerPoints[next][0] - outerPoints[i][0]) * j / 5.0;
                double y = outerPoints[i][1] + (outerPoints[next][1] - outerPoints[i][1]) * j / 5.0;
                positions[idx++] = new double[]{x, y};
            }
        }

        // 중앙점
        positions[25] = new double[]{centerX, centerY};

        // 대각선 점 (2개씩 × 5방향 = 10개)
        for (int i = 0; i < 5; i++) {
            double[] outer = outerPoints[i];
            // 1/3 지점
            positions[26 + i * 2] = new double[]{
                    outer[0] * 2 / 3 + centerX / 3,
                    outer[1] * 2 / 3 + centerY / 3
            };
            // 2/3 지점
            positions[27 + i * 2] = new double[]{
                    outer[0] / 3 + centerX * 2 / 3,
                    outer[1] / 3 + centerY * 2 / 3
            };
        }

        // 연결선 (외곽)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        for (int i = 0; i < 25; i++) {
            int next = (i + 1) % 25;
            gc.strokeLine(positions[i][0], positions[i][1], positions[next][0], positions[next][1]);
        }

        // 연결선 (대각선)
        for (int i = 0; i < 5; i++) {
            double[] outer = outerPoints[i];
            double[] c1 = positions[26 + i * 2];
            double[] c2 = positions[27 + i * 2];
            gc.strokeLine(outer[0], outer[1], c1[0], c1[1]);
            gc.strokeLine(c1[0], c1[1], c2[0], c2[1]);
            gc.strokeLine(c2[0], c2[1], centerX, centerY);
        }

        // 텍스트 라벨
        String[] labels = {
                "0", "1", "2", "3", "4",    // 꼭짓점 → 다음 꼭짓점
                "5", "6", "7", "8", "9",
                "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24",  // 총 25개 외곽
                "X",  // 중앙
                "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10"
        };

        gc.setFont(new Font(12));

        for (int i = 0; i < positions.length; i++) {
            double x = positions[i][0] - radius / 2;
            double y = positions[i][1] - radius / 2;

            // 원 그리기
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, radius, radius);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x, y, radius, radius);

            // 텍스트
            String text = (i < labels.length) ? labels[i] : "?";
            Text textNode = new Text(text);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            double textHeight = textNode.getLayoutBounds().getHeight();
            gc.setFill(Color.BLACK);
            gc.fillText(text, positions[i][0] - textWidth / 2, positions[i][1] + textHeight / 4);
        }

        // 강조 (시작점 0, 꼭짓점 5, 10, 15, 20, 중앙)
        highlightCircle(gc, positions[0], radius, Color.GREEN);       // 시작점
        highlightCircle(gc, positions[5], radius, Color.GOLD);
        highlightCircle(gc, positions[10], radius, Color.GOLD);
        highlightCircle(gc, positions[15], radius, Color.GOLD);
        highlightCircle(gc, positions[20], radius, Color.GOLD);
        highlightCircle(gc, positions[25], radius, Color.GOLDENROD);  // 중앙점
    }

    private void drawHexagonBoard(GraphicsContext gc) {
        double padding = 50;
        double radius = 25;
        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;
        double outerRadius = 250;

        // 총 43개 위치: 0~29 (외곽), 30 (중앙 X), 31~42 (C1~C12)
        double[][] positions = new double[43][2];

        // 육각형 꼭짓점 6개 정의
        double[][] corners = new double[6][2];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            corners[i][0] = centerX + outerRadius * Math.cos(angle);
            corners[i][1] = centerY + outerRadius * Math.sin(angle);
        }

        // 외곽 점 30개 (각 변마다 5점 = 시작점 + 중간 4개)
        int idx = 0;
        for (int i = 0; i < 6; i++) {
            double[] start = corners[i];
            double[] end = corners[(i + 1) % 6];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                double x = start[0] + (end[0] - start[0]) * t;
                double y = start[1] + (end[1] - start[1]) * t;
                positions[idx++] = new double[]{x, y};
            }
        }

        // 중앙점
        positions[30] = new double[]{centerX, centerY};

        // 내부 대각선 점 C1~C12 (6방향 * 2점씩)
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            double x1 = centerX + outerRadius * Math.cos(angle) * 1 / 3;
            double y1 = centerY + outerRadius * Math.sin(angle) * 1 / 3;
            double x2 = centerX + outerRadius * Math.cos(angle) * 2 / 3;
            double y2 = centerY + outerRadius * Math.sin(angle) * 2 / 3;

            positions[31 + i * 2] = new double[]{x1, y1};
            positions[31 + i * 2 + 1] = new double[]{x2, y2};
        }

        // 연결선 (외곽)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        for (int i = 0; i < 30; i++) {
            int next = (i + 1) % 30;
            gc.strokeLine(positions[i][0], positions[i][1], positions[next][0], positions[next][1]);
        }

        // 내부 대각선 연결 (C1~C12 → X)
        for (int i = 31; i <= 42; i++) {
            gc.strokeLine(positions[i][0], positions[i][1], positions[30][0], positions[30][1]);
        }

        // 중심점 X ↔ 6개 꼭짓점 직접 연결
        int[] cornerIndices = {0, 5, 10, 15, 20, 25};
        for (int i : cornerIndices) {
            gc.strokeLine(positions[30][0], positions[30][1], positions[i][0], positions[i][1]);
        }


        // 라벨 설정
        String[] labels = new String[43];
        for (int i = 0; i < 30; i++) labels[i] = String.valueOf(i);
        labels[30] = "X";
        for (int i = 31; i <= 42; i++) labels[i] = "C" + (i - 30);

        // 위치 원과 텍스트 출력
        gc.setFont(new Font(12));
        for (int i = 0; i < positions.length; i++) {
            double x = positions[i][0] - radius / 2;
            double y = positions[i][1] - radius / 2;

            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, radius, radius);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x, y, radius, radius);

            String text = labels[i];
            Text textNode = new Text(text);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            double textHeight = textNode.getLayoutBounds().getHeight();
            gc.setFill(Color.BLACK);
            gc.fillText(text, positions[i][0] - textWidth / 2, positions[i][1] + textHeight / 4);
        }

        // 강조 원 (시작점 0, 꼭짓점 5, 10, 15, 20, 25, 중앙 X)
        highlightCircle(gc, positions[0], radius, Color.GREEN);
        highlightCircle(gc, positions[5], radius, Color.GOLD);
        highlightCircle(gc, positions[10], radius, Color.GOLD);
        highlightCircle(gc, positions[15], radius, Color.GOLD);
        highlightCircle(gc, positions[20], radius, Color.GOLD);
        highlightCircle(gc, positions[25], radius, Color.GOLD);
        highlightCircle(gc, positions[30], radius, Color.GOLDENROD);
    }


}