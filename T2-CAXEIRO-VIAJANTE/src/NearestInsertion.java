import algs4.In;
import algs4.StdOut;
import algs4.StdDraw;

public class NearestInsertion {

    public static void main(String[] args) {
        if (args.length == 0) {
            StdOut.println("Uso: java NearestInsertion <caminho-do-arquivo>");
            return;
        }

        // Usa o caminho fornecido como argumento
        In in = new In(args[0]);

        int width = in.readInt();
        int height = in.readInt();
        int border = 20;
        StdDraw.setCanvasSize(width, height + border);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(-border, height);
        StdDraw.enableDoubleBuffering();

        Tour tour = new Tour();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point p = new Point(x, y);
            tour.insertNearest(p);
        }

        tour.draw();
        StdDraw.show();
        StdOut.println(tour);
        StdOut.printf("Comprimento do ciclo = %.4f\n", tour.length());
        StdOut.printf("Número de pontos = %d\n", tour.size());
    }
}
