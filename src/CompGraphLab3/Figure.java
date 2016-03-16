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

    private void drawTriangleSimple(Point3D[] triangle, Canvas holst, int dX)
    {
        // нормали в вершинах
        double[] na = new double[3];

        int cntNA = 0;

        for (int i = 0; i < triangels.size(); i++)
        {
            // заполняем значения нормали
            if (this.pointInTriangle(triangle[1], triangels.get(i)))
            {
                na[0] += normals[i][0];
                na[1] += normals[i][1];
                na[2] += normals[i][2];
                cntNA++;
            }

        }
        // нормализуем нормали

        na[0] = na[0] / cntNA;
        na[1] = na[1] / cntNA;
        na[2] = na[2] / cntNA;


        Point3D[] int1 = new Point3D[2];
        Point3D[] int2 = new Point3D[2];

        if (triangle[1].getY() == triangle[2].getY())
        {
            int1[0] = new Point3D(triangle[0].getX(),triangle[0].getY(),triangle[0].getZ());
            int1[1] = new Point3D(triangle[1].getX(),triangle[1].getY(),triangle[1].getZ());
            int2[0] = new Point3D(triangle[0].getX(),triangle[0].getY(),triangle[0].getZ());
            int2[1] = new Point3D(triangle[2].getX(),triangle[2].getY(),triangle[2].getZ());

        }
        else
        {
            int1[0] = new Point3D(triangle[0].getX(),triangle[0].getY(),triangle[0].getZ());
            int1[1] = new Point3D(triangle[1].getX(),triangle[1].getY(),triangle[1].getZ());
            int2[0] = new Point3D(triangle[2].getX(),triangle[2].getY(),triangle[2].getZ());
            int2[1] = new Point3D(triangle[1].getX(),triangle[1].getY(),triangle[1].getZ());

        }

        for (double y = int1[0].getY(); y < int1[1].getY(); y++)
        {
            Point3D p1 = this.intersectIntervalsAndY(y, int1);
            Point3D p2 = this.intersectIntervalsAndY(y, int2);

            for (double x = p2.getX(); x < p1.getX(); x++)
            {
                // интенсивность
                int I = (int)Math.min(this.GetIntension(na, 0, 100), 255);
                I = Math.max(I, 0);
                holst.getGraphicsContext2D().setFill(new Color(I,I,I+30,0));
                holst.getGraphicsContext2D().fillRect(holst.getWidth()/2 + x,holst.getHeight()/2 + y,1,1);
            }
        }

    }

    private Point3D intersectIntervalsAndY(double y, Point3D[] int1)
    {
        Point3D p1 = int1[0];
        Point3D p2 = int1[1];
        double t = (y - p1.getY()) / (p1.getY() - p2.getY());
        double xx = p1.getX() + t * (p1.getX() - p2.getX());
        double yy = p1.getY() + t * (p1.getY() - p2.getY());
        double zz = p1.getZ() + t * (p1.getZ() - p2.getZ());
        return new Point3D(xx, yy, zz);
    }

    private boolean pointInTriangle(Point3D p, Point3D[] t){
        if (eqPoint(p,t[0]) || eqPoint(p,t[1]) || eqPoint(p,t[2])) return true;
        return false;
    }

    private boolean eqPoint(Point3D p1, Point3D p2){
        if (Math.abs( p1.getX() - p2.getX()) > 0.0001) return false;
        if (Math.abs( p1.getX() - p2.getY()) > 0.0001) return false;
        if (Math.abs( p1.getX() - p2.getZ()) > 0.0001) return false;
        return true;
    }

    // вычисление интенсивности в na
    private float GetIntension(double[] na, int u, int I)
    {
        double d = 2,
                k = 1,
                Ia = 10,		        // интенсивность фонового освещения
                Il = 400,		        // интенсивность источника
                kd = 0.6,		        // коэффициент диффузного отражения
                ka = 1,	        // коэффициент фонового освещения
                ks = 1;	        // коэффициент зеркального отражения
        if ((na[0] * observer[0] + na[1] * observer[1] + na[2] * observer[2]) < 0) return 0;
        double[] R = new double[3];
        double val = (na[0] * lightSource[0] + na[1] * lightSource[1] + na[2] * lightSource[2]);
        double norm = Math.sqrt(na[0] * na[0] + na[1] * na[1] + na[2] * na[2]);
        R[0] = (2 * na[0] * val / norma(na) - lightSource[0]);
        R[1] = (2 * na[1] * val / norma(na) - lightSource[1]);
        R[2] = (2 * na[2] * val / norma(na) - lightSource[2]);

        double nL = (lightSource[0] * na[0] + lightSource[1] * na[1] + lightSource[2] * na[2]) / norma(na) / norma(lightSource);
        double RS = (R[0] * observer[0] + R[1] * observer[1] + R[2] * observer[2]) / norma(R) / norma(observer);


        return (float)((Ia * ka) + (Il / (k + d)) * (kd * nL + ks * Math.pow(RS, 10)));

    }

    private float norma(double[] vect)
    {
        float res = 0;
        for (int i = 0; i < 3; i++)
            res += vect[i] * vect[i];
        return (float)Math.sqrt(res);

    }
}
