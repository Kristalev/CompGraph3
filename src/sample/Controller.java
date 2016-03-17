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
        fig.drawTriangulation(holst,dX);
        flag = 0;
    }

    @FXML
    void clickSimpleModelButton(ActionEvent event) {
        holst.getGraphicsContext2D().clearRect(0,0,holst.getWidth(),holst.getHeight());
        fig.drawSimpl(holst,dX);
        flag = 1;
    }

    @FXML
    void clickMinusBatton(ActionEvent event) {
        fig.rotateLightY(-0.1);
        dX -= 30;
        switch (flag){
            case 0:
                clickTriangleButtom(new ActionEvent());
                break;
            case 1:
                clickSimpleModelButton(new ActionEvent());
                break;
            case 2:
                clickGouraudShadingButton(new ActionEvent());
            default: break;
        }

    }

    @FXML
    void clickPlusBatton(ActionEvent event) {
        fig.rotateLightY(0.1);
        dX += 30;
        switch (flag){
            case 0:
                clickTriangleButtom(new ActionEvent());
                break;
            case 1:
                clickSimpleModelButton(new ActionEvent());
                break;
            case 2:
                clickGouraudShadingButton(new ActionEvent());
            default: break;
        }

    }

    @FXML
    void clickGouraudShadingButton(ActionEvent event) {
        holst.getGraphicsContext2D().clearRect(0,0,holst.getWidth(),holst.getHeight());
        fig.gouraudShading(holst,dX);
        flag = 2;
    }

    @FXML
    private Button simpleModelButton;

    private Figure fig;
    private int flag;
    private int dX;
    @FXML
    void initialize() {
        flag = -1;
        dX = 0;
        assert holst != null : "fx:id=\"holst\" was not injected: check your FXML file 'sample.fxml'.";
        assert triangleButtom != null : "fx:id=\"triangleButtom\" was not injected: check your FXML file 'sample.fxml'.";
        fig = new Figure();
    }

}
