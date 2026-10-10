package com.poo2026_2.view.professores;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class CafeteiraView extends ImageView {

    private Image cafeteiraVazia;
    private Image cafeteiraCheia;

    public CafeteiraView() {

        Image imagemOriginal = new Image(getClass().getResource("/images/professores/cafeteira.png").toExternalForm());

        PixelReader leitor = imagemOriginal.getPixelReader();

        cafeteiraVazia = new WritableImage(leitor, 100, 150, 550, 550);
        cafeteiraCheia = new WritableImage(leitor, 820, 150, 550, 550);

        setImage(cafeteiraVazia);

        setFitWidth(90);
        setFitHeight(110);
        setPreserveRatio(true);
    }

    public void mostrarCheia() {
        setImage(cafeteiraCheia);
    }

    public void mostrarVazia() {
        setImage(cafeteiraVazia);
    }
}