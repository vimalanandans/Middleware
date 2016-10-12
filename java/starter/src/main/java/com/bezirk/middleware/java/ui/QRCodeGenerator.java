package com.bezirk.middleware.java.ui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class QRCodeGenerator extends Application {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(QRCodeGenerator.class);

    @Override
    public void start(Stage primaryStage) {

        Parameters parameters = getParameters();
        if (parameters == null || parameters.getRaw().size() == 0) {
            throw new IllegalArgumentException("No parameters passed to QRCodeGenerator. Data to be " +
                    "embedded in the QRCodeGenerator needs to be passed. Usage\n " +
                    "Application.launch(QRCodeGenerator.class, \"Data to be displayed in QRCode\");");
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 400;
        int height = 400;

        BufferedImage bufferedImage = null;
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(getParameters().getRaw().get(0),
                    BarcodeFormat.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();

            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

        } catch (WriterException e) {
            logger.error("Writer Exception\n" + e);
        }

        ImageView qrView = new ImageView();
        qrView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

        StackPane root = new StackPane();
        root.getChildren().add(qrView);

        Scene scene = new Scene(root, 450, 450);

        primaryStage.setTitle("Bezirk QR Code Display");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}