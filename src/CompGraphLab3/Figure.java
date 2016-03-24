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
        //return 10 * (2 - 11 * Math.sin(x * Math.PI / 120)) + 150; //кривая, задающая фигуру
        return 10 * (2 - 11 * Math.sin(x * Math.PI / 200)) + 150; //кривая, задающая фигуру
        //return (float) (230 * Math.Sin(x*Math.PI/240));
    }


    public Figure()
    {
        countPointInCircle = 50;
        countCircles = 100; //количество окружностей
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
                holst.getGraphicsContext2D().setStroke(Color.BLACK);
                holst.getGraphicsContext2D().strokeLine(points[0].getX(),points[0].getY(),points[1].getX(),points[1].getY());
                holst.getGraphicsContext2D().strokeLine(points[1].getX(),points[1].getY(),points[2].getX(),points[2].getY());
                holst.getGraphicsContext2D().strokeLine(points[2].getX(),points[2].getY(),points[0].getX(),points[0].getY());
            }
        }
    }

    private Point2D proec(Point3D point, double w, double h){
        return new Point2D((w/2+point.getX()),(h/2-point.getY()));
    }

    // рисуем простейшую модель
    public void drawSimpl(Canvas holst,int dX)
    {
        for(Point3D[] tran: triangels){
            boolean ok = true;
            for (int j = 0; j < 3; j++)
                if (tran[j].getZ() < 0) ok = false;
            if (ok) this.drawTriangleSimple(tran, holst, dX);
        }
    }

    private void drawTriangleSimple(Point3D[] triangle, Canvas holst, int dX)
    {
        // нормали в вершинах
        double[] na;// = new double[3];
        na = normals[triangels.indexOf(triangle)];

        int I = (int)Math.min(this.getIntension(na), 255);
        I = Math.max(I, 0);
        Point2D[] points =  new Point2D[3];
        points[0] = proec(triangle[0],holst.getWidth()+dX,holst.getHeight());
        points[1] = proec(triangle[1],holst.getWidth()+dX,holst.getHeight());
        points[2] = proec(triangle[2],holst.getWidth()+dX,holst.getHeight());
        holst.getGraphicsContext2D().setFill(Color.rgb(I,I,I));
        double[] arX = {points[0].getX(),points[1].getX(),points[2].getX()};
        double[] arY = {points[0].getY(),points[1].getY(),points[2].getY()};
        holst.getGraphicsContext2D().fillPolygon(arX,arY ,3);
    }

    // закраска методом Гуро
    public void gouraudShading(Canvas holst,int dX)
    {
        for(Point3D[] tran: triangels){
            boolean ok = true;
            for (int j = 0; j < 3 && ok; j++)
                if (tran[j].getZ() < 0) ok = false;
            if (ok) this.drawTriangleGouraudShading(tran, holst, dX);
        }
    }

    private void drawTriangleGouraudShading(Point3D[] triangle, Canvas holst, int dX)
    {
        double[] na = new double[3];
        double[] nb = new double[3];
        double[] nc = new double[3];
        int cntNA = 0, cntNB = 0, cntNC = 0;

        // заполняем значения нормалей
        for(Point3D[] tran: triangels){
            if (pointInTriangle(triangle[0], tran))
            {
                na[0] += normals[triangels.indexOf(tran)][0];
                na[1] += normals[triangels.indexOf(tran)][1];
                na[2] += normals[triangels.indexOf(tran)][2];
                cntNA++;
            }
            if (pointInTriangle(triangle[1], tran))
            {
                nb[0] += normals[triangels.indexOf(tran)][0];
                nb[1] += normals[triangels.indexOf(tran)][1];
                nb[2] += normals[triangels.indexOf(tran)][2];
                cntNB++;
            }
            if (pointInTriangle(triangle[2], tran))
            {
                nc[0] += normals[triangels.indexOf(tran)][0];
                nc[1] += normals[triangels.indexOf(tran)][1];
                nc[2] += normals[triangels.indexOf(tran)][2];
                cntNC++;
            }
        }
            // нормализуем нормали
        na[0] = na[0] / cntNA;
        na[1] = na[1] / cntNA;
        na[2] = na[2] / cntNA;

        nb[0] = nb[0] / cntNB;
        nb[1] = nb[1] / cntNB;
        nb[2] = nb[2] / cntNB;

        nc[0] = nc[0] / cntNC;
        nc[1] = nc[1] / cntNC;
        nc[2] = nc[2] / cntNC;
        int Ia = (int)Math.min(this.getIntension(na), 255);
        int Ib = (int)Math.min(this.getIntension(nb), 255);
        int Ic = (int)Math.min(this.getIntension(nc), 255);

        Point3D[] int1 = new Point3D[2];
        Point3D[] int2 = new Point3D[2];
        double[] nI11, nI12, nI21, nI22;
        int flag = 0;
        if (triangle[1].getY() == triangle[2].getY()){
            int1[0] = new Point3D(triangle[0].getX(),triangle[0].getY(),triangle[0].getZ());    //вершина треуг точка a
            int1[1] = new Point3D(triangle[1].getX(),triangle[1].getY(),triangle[1].getZ());    //конец точка b
            int2[0] = new Point3D(triangle[0].getX(),triangle[0].getY(),triangle[0].getZ());     //вершина треуг точка a
            int2[1] = new Point3D(triangle[2].getX(),triangle[2].getY(),triangle[2].getZ());     //конец точка с
            nI11 = na;
            nI12 = nb;
            nI21 = na;
            nI22 = nc;
            flag = 1;

        }else{
            int1[0] = new Point3D(triangle[0].getX(),triangle[0].getY(),triangle[0].getZ());    //конец точка a
            int1[1] = new Point3D(triangle[1].getX(),triangle[1].getY(),triangle[1].getZ());     //вершина треуг точка b
            int2[0] = new Point3D(triangle[2].getX(),triangle[2].getY(),triangle[2].getZ());    //конец точка с
            int2[1] = new Point3D(triangle[1].getX(),triangle[1].getY(),triangle[1].getZ());     //вершина треуг точка b
            nI11 = na;
            nI12 = nb;
            nI21 = nc;
            nI22 = nb;
            flag = 2;
        }
        //Закоменченный гуро.
        /*for (double y = int1[0].getY(); y < int1[1].getY(); y++) {
            Point3D p1 = this.intersectIntervalsAndY(y, int1);
            Point3D p2 = this.intersectIntervalsAndY(y, int2);
            double[] N1 = new double[3];
            double[] N2 = new double[3];

            // интерполирование нормалей
            N1[0] = nI12[0] + (nI11[0] - nI12[0]) * (y - int1[1].getY()) / (int1[0].getY() - int1[1].getY());
            N1[1] = nI12[1] + (nI11[1] - nI12[1]) * (y - int1[1].getY()) / (int1[0].getY() - int1[1].getY());
            N1[2] = nI12[2] + (nI11[2] - nI12[2]) * (y - int1[1].getY()) / (int1[0].getY() - int1[1].getY());

            N2[0] = nI22[0] + (nI21[0] - nI22[0]) * (y - int2[1].getY()) / (int2[0].getY() - int2[1].getY());
            N2[1] = nI22[1] + (nI21[1] - nI22[1]) * (y - int2[1].getY()) / (int2[0].getY() - int2[1].getY());
            N2[2] = nI22[2] + (nI21[2] - nI22[2]) * (y - int2[1].getY()) / (int2[0].getY() - int2[1].getY());

            for (double x = p2.getX(); x < p1.getX(); x++) {
                double[] N = new double[3];
                // нормаль в точке грани
                N[0] = N1[0] + (N2[0] - N1[0]) * (x - p1.getX()) / (p2.getX() - p1.getX());
                N[1] = N1[1] + (N2[1] - N1[1]) * (x - p1.getX()) / (p2.getX() - p1.getX());
                N[2] = N1[2] + (N2[2] - N1[2]) * (x - p1.getX()) / (p2.getX() - p1.getX());

                int I = Math.min(255, (int) this.getIntension(N));
                I = Math.max(I, 0);
                Point2D point = proec(new Point3D(x, y, 0), holst.getWidth() + dX, holst.getHeight());
                holst.getGraphicsContext2D().getPixelWriter().setColor((int) point.getX(), (int) point.getY(), Color.rgb(I, I, I));
                //g.FillRectangle(new SolidBrush(Color.FromArgb(I, I, I+30)), mx / 2 + x, my / 2 - y, 1, 1);
            }*/

        if (flag == 1){
            for(double y = int1[1].getY();  y > int1[0].getY(); y--) {
                Point3D p1 = this.intersectIntervalsAndY(y, int2);
                Point3D p2 = this.intersectIntervalsAndY(y, int1);
                int I1 = (int) (Ia + ((Ic - Ia) * (y - int1[0].getY())) / (int2[1].getY() - int2[0].getY()));
                int I2 = (int) (Ia + ((Ib - Ia) * (y - int1[0].getY())) / (int1[1].getY() - int1[0].getY()));
                for (double x = p1.getX(); x < p2.getX(); x++) {
                    // интенсивность
                    int I = (int) (I1 + (I2 - I1) * (x - p1.getX()) / (p2.getX() - p1.getX()));
                    I = Math.min(I, 255);
                    I = Math.max(0, I);
                    Point2D point = proec(new Point3D(x, y, 0), holst.getWidth() + dX, holst.getHeight());
                    //holst.getGraphicsContext2D().getPixelWriter().setColor((int)(x+holst.getWidth()/2+dX),(int)(x-holst.getHeight()/2), Color.rgb(I,I,I));
                    holst.getGraphicsContext2D().getPixelWriter().setColor((int) point.getX(), (int) point.getY(), Color.rgb(I, I, I));
                    //g.FillRectangle(new SolidBrush(Color.FromArgb(I, I, I+30)), mx / 2 + x, my / 2 - y, 1, 1);
                }
            }
        }
        if (flag == 2){
            for(double y = int1[1].getY();  y > int1[0].getY(); y--){
                Point3D p1 = this.intersectIntervalsAndY1(y, int2);
                Point3D p2 = this.intersectIntervalsAndY1(y, int1);
                int I1 = (int)(Ib + ((Ic - Ib)*(y - int1[1].getY())) / (int2[0].getY()- int1[1].getY()));
                int I2 = (int)(Ib + ((Ia - Ib)*(y - int1[1].getY())) / (int1[0].getY()- int1[1].getY()));
                for (double x = p1.getX(); x < p2.getX(); x++)
                {
                    // интенсивность
                    int I = (int)(I1 + (I2- I1)*(x - p1.getX())/(p2.getX() - p1.getX()));
                    I = Math.min(I, 255);
                    I = Math.max(0,I);
                    Point2D point =  proec(new Point3D(x,y,0),holst.getWidth()+dX,holst.getHeight());
                    //holst.getGraphicsContext2D().getPixelWriter().setColor((int)(x+holst.getWidth()/2+dX),(int)(x-holst.getHeight()/2), Color.rgb(I,I,I));
                    holst.getGraphicsContext2D().getPixelWriter().setColor((int)point.getX(),(int)point.getY(), Color.rgb(I,I,I));
                    //g.FillRectangle(new SolidBrush(Color.FromArgb(I, I, I+30)), mx / 2 + x, my / 2 - y, 1, 1);
                }
            }
        }
        /*for(double y = int1[0].getY();  y < int1[1].getY(); y++){
            Point3D p1 = this.intersectIntervalsAndY(y, int1);
            Point3D p2 = this.intersectIntervalsAndY(y, int2);
            int I1 = 0 ;
            int I2 = 0;
            if (flag == 1){
                I1 = (int)(Ia + ((Ic - Ia)*(y - int1[0].getY())) / (int2[1].getY()- int2[0].getY()));
                I2 = (int)(Ia + ((Ib - Ia)*(y - int1[0].getY())) / (int1[1].getY()- int1[0].getY()));
            }
            if (flag == 2){
                I1 = (int)(Ib + ((Ic - Ib)*(y - int1[1].getY())) / (int2[0].getY()- int1[1].getY()));
                I2 = (int)(Ib + ((Ia - Ib)*(y - int1[1].getY())) / (int1[0].getY()- int1[1].getY()));
            }
            for (double x = p2.getX(); x < p1.getX(); x++)
            {
                // интенсивность
                int I = (int)(I1 + (I2- I1)*(x - p1.getX())/(p2.getX() - p1.getX()));
                I = Math.min(I, 255);
                I = Math.max(0,I);
                Point2D point =  proec(new Point3D(x,y,0),holst.getWidth()+dX,holst.getHeight());
                //holst.getGraphicsContext2D().getPixelWriter().setColor((int)(x+holst.getWidth()/2+dX),(int)(x-holst.getHeight()/2), Color.rgb(I,I,I));
                holst.getGraphicsContext2D().getPixelWriter().setColor((int)point.getX(),(int)point.getY(), Color.rgb(I,I,I));
                //g.FillRectangle(new SolidBrush(Color.FromArgb(I, I, I+30)), mx / 2 + x, my / 2 - y, 1, 1);
            }

        }*/
    }


    private boolean pointInTriangle(Point3D p, Point3D[] t){
        if (eqPoint(p,t[0]) || eqPoint(p,t[1]) || eqPoint(p,t[2])) return true;
        return false;
    }

    private boolean eqPoint(Point3D p1, Point3D p2){
        if (Math.abs( p1.getX() - p2.getX()) > 0.0001) return false;
        if (Math.abs( p1.getY() - p2.getY()) > 0.0001) return false;
        if (Math.abs( p1.getZ() - p2.getZ()) > 0.0001) return false;
        return true;
    }

    // вычисление интенсивности в na
    private double getIntension(double[] na)
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
        R[0] = (2 * na[0] * val / norma(na) - lightSource[0]);
        R[1] = (2 * na[1] * val / norma(na) - lightSource[1]);
        R[2] = (2 * na[2] * val / norma(na) - lightSource[2]);

        double nL = (lightSource[0] * na[0] + lightSource[1] * na[1] + lightSource[2] * na[2]) / norma(na) / norma(lightSource);
        double RS = (R[0] * observer[0] + R[1] * observer[1] + R[2] * observer[2]) / norma(R) / norma(observer);


        return ((Ia * ka) + (Il / (k + d)) * (kd * nL + ks * Math.pow(RS, 10)));

    }

    private double norma(double[] vect)
    {
        double res = 0;
        for (int i = 0; i < 3; i++)
            res += vect[i] * vect[i];
        return Math.sqrt(res);
    }

    public void rotateLightY(double alpha)
    {
        Point3D p = rotateY(new Point3D(lightSource[0], lightSource[1], lightSource[2]),alpha);
        lightSource[0] = p.getX();
        lightSource[1] = p.getY();
        lightSource[2] = p.getZ();
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

    private Point3D intersectIntervalsAndY1(double y, Point3D[] int1)
    {
        Point3D p2 = int1[0];
        Point3D p1 = int1[1];
        double t = (y - p1.getY()) / (p1.getY() - p2.getY());
        double xx = p1.getX() + t * (p1.getX() - p2.getX());
        double yy = p1.getY() + t * (p1.getY() - p2.getY());
        double zz = p1.getZ() + t * (p1.getZ() - p2.getZ());
        return new Point3D(xx, yy, zz);
    }
}
