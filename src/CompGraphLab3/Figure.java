package CompGraphLab3;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class Figure {
    private int countCircles;
    private int[] radius;
    private double[] lightSource;
    private double[] observer;
    private ArrayList<Point3D[]> triangels;
    private double[][] normals;
    private int countPointInCircle;

    public double Func(float x)
    {
        return (float)10 * (2 - 11 * Math.sin(x * Math.PI / 200)) + 150; //кривая, задающая фигуру
        //return (float) (230 * Math.Sin(x*Math.PI/240));
    }

    public Figure()
    {
        countPointInCircle = 20;
        countCircles = 30; //количество окружностей
        radius = new int[countCircles];
        for (int i = 0; i < countCircles; i++)
            radius[i] = (int)(Func(i * 8) - 20);

        // Триангуляция
        this.triangulation();

        // Ищем нормали для треугольников
        normals = new double[triangels.size()][3];
        for (int i = 0; i < triangels.size(); i++)
        {
            double xn = (triangels.get(i)[2].getY() - triangels.get(i)[1].getY()) * (triangels.get(i)[0].getZ() - triangels.get(i)[1].getZ())
                    - (triangels.get(i)[2].getZ() - triangels.get(i)[1].getZ()) * (triangels.get(i)[0].getY() - triangels.get(i)[1].getY());
            double yn = (triangels.get(i)[2].getZ() - triangels.get(i)[1].getZ()) * (triangels.get(i)[0].getX() - triangels.get(i)[1].getX())
                    - (triangels.get(i)[2].getX() - triangels.get(i)[1].getX()) * (triangels.get(i)[0].getZ() - triangels.get(i)[1].getZ());
            double zn = (triangels.get(i)[2].getX() - triangels.get(i)[1].getX()) * (triangels.get(i)[0].getY() - triangels.get(i)[1].getY())
                    - (triangels.get(i)[2].getY() - triangels.get(i)[1].getY()) * (triangels.get(i)[0].getX() - triangels.get(i)[1].getX());
            double n = Math.sqrt(xn * xn + yn * yn + zn * zn);
            xn = xn / n;
            yn = yn / n;
            zn = zn / n;
            normals[i][0] = xn;
            normals[i][1] = yn;
            normals[i][2] = zn;
        }


        // направление на источник света
        lightSource = new double[3];
        lightSource[0] = 0;
        lightSource[1] = 0;
        lightSource[2] = 1;

        // направление на наблюдателя
        observer = new double[3];
        observer[0] = 0;
        observer[1] = 0;
        observer[2] = 1;
    }

    private void triangulation(){
        triangels = new ArrayList<>();
        for (int i = 0; i < countCircles - 1; i++)
        {
            double y1 = 15 * i - 230;
            double y2 = 15 * (i + 1) - 230;

            Point3D p1 = new Point3D(radius[i], y1, 0);
            Point3D p2 = new Point3D(radius[i + 1], y2, 0);
            for (int j = 0; j < countPointInCircle; j++)
            {
                Point3D p3 = rotateY(p1,j * 2 * Math.PI / countPointInCircle);
                Point3D p4 = rotateY(p1,(j + 1) * 2 * Math.PI / countPointInCircle);
                Point3D p5 = rotateY(p2,j * 2 * Math.PI / countPointInCircle);
                Point3D p6 = rotateY(p2,(j + 1) * 2 * Math.PI / countPointInCircle);

                Point3D[] triangle = new Point3D[3];
                triangle[0] = new Point3D(p3.getX(),p3.getY(),p3.getZ());
                triangle[1] = new Point3D(p5.getX(),p5.getY(),p5.getZ());
                triangle[2] = new Point3D(p6.getX(),p6.getY(),p6.getZ());
                triangels.add(triangle);

                triangle = new Point3D[3];
                triangle[0] = new Point3D(p3.getX(),p3.getY(),p3.getZ());
                triangle[1] = new Point3D(p6.getX(),p6.getY(),p6.getZ());
                triangle[2] = new Point3D(p4.getX(),p4.getY(),p4.getZ());
                triangels.add(triangle);
            }
        }
    }


    private Point3D rotateY(Point3D p,double alpha)
    {
        double x, y, z;
        y = p.getY();
        x =  p.getX() *Math.cos(alpha) - p.getZ() *Math.sin(alpha);
        z = p.getX() * Math.sin(alpha) +  p.getZ() * Math.cos(alpha);
        return new Point3D(x, y, z);
    }

    public void drawTriangulation(Canvas holst, int dX){
        for(Point3D[] tran: triangels){
            Point2D[] points = new Point2D[4];
            points[0] = proec(tran[0],holst.getWidth()+dX,holst.getHeight());
            points[1] = proec(tran[1],holst.getWidth()+dX,holst.getHeight());
            points[2] = proec(tran[2],holst.getWidth()+dX,holst.getHeight());
            boolean ok = true;
            for (int j = 0; j < 3; j++)
                if (tran[j].getZ() < 0) ok = false;
            if (ok){
                holst.getGraphicsContext2D().setStroke(Color.DARKBLUE);
                holst.getGraphicsContext2D().strokeLine(points[0].getX(),points[0].getY(),points[1].getX(),points[1].getY());
                holst.getGraphicsContext2D().strokeLine(points[1].getX(),points[1].getY(),points[2].getX(),points[2].getY());
                holst.getGraphicsContext2D().strokeLine(points[2].getX(),points[2].getY(),points[0].getX(),points[0].getY());
            }
        }
    }

    private Point2D proec(Point3D point, double w, double h){
        return new Point2D((int)(w/2+point.getX()),(int)(h/2-point.getY()));
    }
}
