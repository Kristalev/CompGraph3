package sample;

import CompGraphLab3.Figure;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
public class Controller {
    @FXML
    private Canvas holst;

    @FXML
    private Button triangleButtom;

    @FXML
    void clickTriangleButtom(ActionEvent event) {
        holst.getGraphicsContext2D().clearRect(0,0,holst.getWidth(),holst.getHeight());
        fig.drawTriangulation(holst,0);
    }

    @FXML
    void clickSimpleModelButton(ActionEvent event) {
        holst.getGraphicsContext2D().clearRect(0,0,holst.getWidth(),holst.getHeight());
        fig.drawSimpl(holst,0);
    }

    private Figure fig;
    @FXML
    void initialize() {
        assert holst != null : "fx:id=\"holst\" was not injected: check your FXML file 'sample.fxml'.";
        assert triangleButtom != null : "fx:id=\"triangleButtom\" was not injected: check your FXML file 'sample.fxml'.";
        fig = new Figure();
    }

}
