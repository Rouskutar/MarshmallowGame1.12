package com.example.marshmallowgame1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.StackPane;


public class MarshmallowGame extends Application {


    // PELIN TILA !!!
    // testing
    //

    private GameState state = GameState.EMPTY;
    private final GameStats gameStats = new GameStats();
    private final SoundManager soundManager = new SoundManager();

    // Vaahtokarkin paahtoon liittyvät tilat
    private boolean vaahtokarkkiTummunut = false;
    private boolean paahtoKaynnistetty = false;
    private boolean valmis = false;

    // Animaatiot
    private double textAlpha = 0.0;
    private int liekkiOffset = 0;
    private Timeline liekkiTimeline;

    // Canvas-piirto
    private Canvas canvas;
    private GraphicsContext gc;

    // Reset-nappi
    private Button resetButton;

    @Override
    public void start(Stage stage) {

        // Canvas, johon peli piirretään
        canvas = new Canvas(400, 250);
        gc = canvas.getGraphicsContext2D();

        // Pelin napit
        Button soundButton = new Button("🔊");
        soundButton.setStyle(
                "-fx-font-size: 30;" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #110c0c;" // ääni iconi musta
        );
        Button b1 = new Button("Make a campfire");
        Button b2 = new Button("Light the fire!");
        Button b3 = new Button("Take a stick");
        Button b4 = new Button("Add a marshmallow on the stick");


        resetButton = new Button("Restart");
        resetButton.setVisible(false);
        resetButton.setStyle("-fx-background-color: orange;"); // restart nappi oranssiksi

        // NAPPIEN TOIMINTA

        // ÄÄNI ON/OFF
        soundButton.setOnAction(e -> {
            soundManager.toggleSound();

            if (soundManager.isSoundOn()
                    && (state == GameState.FIRE_LIT
                    || state == GameState.STICK_TAKEN
                    || state == GameState.COOKING)) {
                soundManager.playFire();
            }

            soundButton.setText(
                    soundManager.isSoundOn() ? "🔊" : "🔇"
            );
        });

        // Rakennetaan nuotio
        b1.setOnAction(e -> {
            state = GameState.FIRE_BUILT;
            draw();
        });

        // Sytytetään tuli
        b2.setOnAction(e -> {
            if (state == GameState.FIRE_BUILT) {
                state = GameState.FIRE_LIT;
                valmis = false;
                kaynnistaLiekit();
                soundManager.playFire();
                draw();
            }
        });

        // Otetaan tikku
        b3.setOnAction(e -> {
            if (state == GameState.FIRE_LIT) {
                state = GameState.STICK_TAKEN;
                draw();
            }
        });

        // Lisätään vaahtokarkki ja aloitetaan paahto
        b4.setOnAction(e -> {
            if (state == GameState.STICK_TAKEN) {
                state = GameState.COOKING;
                draw();
                kaynnistaPaahto();
            }
        });

        // RESETOI PELI
        resetButton.setOnAction(e -> resetGame());

        // Layout
        HBox buttons = new HBox(10, b1, b2, b3, b4, resetButton);
        buttons.setStyle("-fx-padding: 10; -fx-alignment: center;");

        BackgroundManager bg = new BackgroundManager(400, 250);

        StackPane centerPane = new StackPane();
        if (bg.getView() != null) {
            centerPane.getChildren().add(bg.getView());
        }
        centerPane.getChildren().add(canvas);

        StackPane.setAlignment(soundButton, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(soundButton, new javafx.geometry.Insets(5));

        centerPane.getChildren().add(soundButton);
        soundButton.toFront();

        BorderPane root = new BorderPane();
        root.setCenter(centerPane);
        root.setBottom(buttons);

        draw();

        stage.setTitle("Marshmallow Game");
        stage.setScene(new Scene(root));
        stage.show();
    }


    // Käynnistää liekkien animaation.

    private void kaynnistaLiekit() {
        liekkiTimeline = new Timeline(
                new KeyFrame(Duration.millis(200), e -> {
                    if (state == GameState.FIRE_LIT ||
                            state == GameState.STICK_TAKEN ||
                            state == GameState.COOKING) {

                        liekkiOffset = (int)(Math.random() * 4 - 2);
                        draw();
                    }
                })
        );
        liekkiTimeline.setCycleCount(Timeline.INDEFINITE);
        liekkiTimeline.play();
    }


    // Käynnistää vaahtokarkin paahtamisen.

    private void kaynnistaPaahto() {
        if (paahtoKaynnistetty) return;
        paahtoKaynnistetty = true;

        // Vaahtokarkki tummuu hetken kuluttua
        PauseTransition tumma = new PauseTransition(Duration.seconds(2));
        tumma.setOnFinished(e -> {
            vaahtokarkkiTummunut = true;
            draw();
        });
        tumma.play();

        // Peli valmistuu
        PauseTransition loppu = new PauseTransition(Duration.seconds(6));
        loppu.setOnFinished(e -> {
            valmis = true;
            state = GameState.DONE;
            gameStats.incrementMarshmallows();
            textAlpha = 0.0;
            resetButton.setVisible(true);
            kaynnistaFadeIn();
        });
        loppu.play();
    }


    // Fade-in animaatio lopputekstille.

    private void kaynnistaFadeIn() {
        PauseTransition fade = new PauseTransition(Duration.millis(50));
        fade.setOnFinished(e -> {
            if (textAlpha < 1.0) {
                textAlpha = Math.min(1.0, textAlpha + 0.05);
                draw();
                fade.play();
            }
        });
        fade.play();
    }

    // Nollaa pelin alkutilaan.

    private void resetGame() {

        if (liekkiTimeline != null) {
            liekkiTimeline.stop();
            liekkiTimeline = null;
        }

        state = GameState.EMPTY;
        vaahtokarkkiTummunut = false;
        paahtoKaynnistetty = false;
        valmis = false;
        textAlpha = 0.0;
        liekkiOffset = 0;

        resetButton.setVisible(false);
        draw();

        soundManager.stopFire();
    }


     // Piirtää pelin nykyisen tilan canvasille.

    private void draw() {

        // Tausta
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());


        // NUOTIOPUUT
        if (state != GameState.EMPTY) {
            gc.setFill(Color.SADDLEBROWN);
            gc.fillRoundRect(200, 130, 10, 95, 10, 10);
            gc.fillRoundRect(220, 130, 15, 100, 8, 8);
            gc.fillRoundRect(230, 150, 65, 15, 8, 8);
            gc.fillRoundRect(160, 180, 45, 15, 10, 10);
            gc.fillRoundRect(200, 170, 70, 12, 8, 8);
            gc.fillRoundRect(210, 190, 70, 12, 8, 8);
        }

        // TULI
        if (state == GameState.FIRE_LIT ||
                state == GameState.STICK_TAKEN ||
                state == GameState.COOKING ||
                state == GameState.DONE) {

            gc.setFill(Color.web("#20170f"));
            gc.fillRect(170, 179, 90, 15);

            gc.setFill(Color.web("#ffd966"));
            gc.fillRoundRect(170, 178, 90, 10, 18, 18);

            gc.setFill(Color.web("#c0392b"));
            gc.fillRoundRect(180, 100 + liekkiOffset, 30, 90, 32, 32);
            gc.fillRoundRect(210, 105 - liekkiOffset, 40, 80, 14, 14);
            gc.fillRoundRect(215, 80 + liekkiOffset, 28, 90, 30, 30);
            gc.fillRoundRect(245, 98 - liekkiOffset, 24, 86, 24, 14);
            gc.fillRoundRect(270, 142 + liekkiOffset, 20, 30, 12, 12);
            gc.fillRoundRect(170, 142 + liekkiOffset, 20, 30, 12, 12);

            gc.setFill(Color.ORANGE);
            gc.fillRoundRect(185, 115 + liekkiOffset, 20, 66, 14, 14);
            gc.fillRoundRect(220, 90 - liekkiOffset, 20, 88, 36, 16);
            gc.fillRoundRect(255, 128 + liekkiOffset, 24, 42, 16, 16);
            gc.fillRoundRect(250, 109 - liekkiOffset, 18, 34, 14, 14);

            gc.setFill(Color.YELLOW);
            gc.fillRoundRect(200, 155 + liekkiOffset, 14, 14, 14, 14);
            gc.fillRoundRect(220, 158 - liekkiOffset, 16, 16, 16, 16);
            gc.fillRoundRect(240, 155 + liekkiOffset, 14, 14, 14, 14);
            gc.fillRoundRect(260, 158 - liekkiOffset, 12, 12, 12, 12);

            gc.setFill(Color.web("#ffffff"));
            gc.fillRoundRect(220, 130 - liekkiOffset, 24, 20, 18, 18);
        }

        // TIKKU
        if (state == GameState.STICK_TAKEN ||
                state == GameState.COOKING ||
                state == GameState.DONE) {

            gc.setStroke(Color.BROWN);
            gc.setLineWidth(4);
            gc.strokeLine(81, 200, 181, 85);

            gc.setFill(Color.web("#e0c3a0"));
            gc.fillRoundRect(0, 170, 100, 25, 8, 8);

            gc.setStroke(Color.web("#c2a887"));
            gc.strokeRoundRect(0, 170, 100, 25, 8, 8);
        }

        // VAAHTOKARKKI
        if (state == GameState.COOKING || state == GameState.DONE) {
            if (!vaahtokarkkiTummunut) {
                gc.setFill(Color.WHITE);
                gc.fillRect(181, 55, 30, 30);
            } else {
                gc.setFill(Color.web("#f1e3d5"));
                gc.fillRoundRect(181, 55, 30, 30, 12, 12);

                gc.setFill(Color.web("#684624"));
                gc.fillRoundRect(181, 55, 30, 13, 10, 10);

                gc.setFill(Color.web("#3a220b"));
                gc.fillRoundRect(181, 55, 30, 4, 8, 8);
            }
        }

        // LOPPUTEKSTI
        if (state == GameState.DONE && textAlpha > 0) {
            String teksti = gameStats.getStatusText();

            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));

        // lasketaan keskitys
            double textWidth = gc.getFont().getSize() * teksti.length() * 0.6;
            double x = (canvas.getWidth() - textWidth) / 2;
            double y = canvas.getHeight() / 2;

        // MUSTA REUNA
            gc.setStroke(Color.web("#000000"));
            gc.setLineWidth(3);
            gc.strokeText(teksti, x, y);

        // KELTAINEN SISUS
            gc.setFill(Color.web("#ffd966")); // lämmin keltainen
            gc.fillText(teksti, x, y);
        }
    }
}