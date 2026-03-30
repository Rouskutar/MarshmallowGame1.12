package com.example.marshmallowgame1;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class BackgroundManager {

    private ImageView view;

    public BackgroundManager(double width, double height) {

        var url = getClass().getResource("/pic/stocksnap-stars-2616537_640.jpg");
        if (url != null) {
            Image img = new Image(url.toExternalForm());
            view = new ImageView(img);
            view.setFitWidth(width);
            view.setFitHeight(height);
            view.setPreserveRatio(false);
        } else {
            System.out.println("Taustakuvaa ei löytynyt");
        }
    }

    public ImageView getView() {
        return view;
    }
}