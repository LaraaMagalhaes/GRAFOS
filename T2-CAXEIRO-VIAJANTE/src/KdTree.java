import algs4.Point2D;
import algs4.RectHV;

public class KdTree {

    private static class Node {
        Point2D point;
        Node left, right;
        boolean vertical;
        RectHV rect; // AABB da subárvore

        Node(Point2D p, boolean vertical, RectHV rect) {
            this.point = p;
            this.vertical = vertical;
            this.rect = rect;
        }
    }

    private Node root;
    private int size = 0;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (root == null) {
            // limites amplos; use limites conhecidos se houver
            RectHV all = new RectHV(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                                    Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            root = new Node(p, true, all);
            size = 1;
        } else {
            root = insert(root, p, true);
        }
    }

    private Node insert(Node node, Point2D p, boolean vertical) {
        if (node == null) return null; // não acontece: controlado no pai
        if (node.point.equals(p)) return node;

        if (vertical) {
            if (p.x() < node.point.x()) {
                if (node.left == null) {
                    RectHV r = new RectHV(node.rect.xmin(), node.rect.ymin(), node.point.x(), node.rect.ymax());
                    node.left = new Node(p, !vertical, r);
                    size++;
                } else {
                    insert(node.left, p, !vertical);
                }
            } else {
                if (node.right == null) {
                    RectHV r = new RectHV(node.point.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
                    node.right = new Node(p, !vertical, r);
                    size++;
                } else {
                    insert(node.right, p, !vertical);
                }
            }
        } else {
            if (p.y() < node.point.y()) {
                if (node.left == null) {
                    RectHV r = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.point.y());
                    node.left = new Node(p, !vertical, r);
                    size++;
                } else {
                    insert(node.left, p, !vertical);
                }
            } else {
                if (node.right == null) {
                    RectHV r = new RectHV(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.rect.ymax());
                    node.right = new Node(p, !vertical, r);
                    size++;
                } else {
                    insert(node.right, p, !vertical);
                }
            }
        }
        return node;
    }

    public Point2D nearest(Point2D p) {
        if (root == null) return null;
        return nearest(root, p, root.point);
    }

    private Point2D nearest(Node node, Point2D p, Point2D best) {
        if (node == null) return best;

        double bestDist = dist2(p, best);
        double rectDist = rectDist2(p, node.rect);
        if (rectDist >= bestDist) return best; // poda forte por AABB

        double d = dist2(p, node.point);
        if (d < bestDist) { best = node.point; bestDist = d; }

        Node first, second;
        if ((node.vertical && p.x() < node.point.x()) ||
            (!node.vertical && p.y() < node.point.y())) {
            first = node.left;  
            second = node.right;
        } else {
            first = node.right; 
            second = node.left;
        }

        best = nearest(first, p, best);
        bestDist = dist2(p, best);
        if (second != null && rectDist2(p, second.rect) < bestDist)
            best = nearest(second, p, best);

        return best;
    }

    // util: distância ao quadrado entre pontos
    private static double dist2(Point2D a, Point2D b) {
        double dx = a.x() - b.x(), dy = a.y() - b.y();
        return dx*dx + dy*dy;
    }

    // util: distância ao quadrado de ponto a AABB
    private static double rectDist2(Point2D p, RectHV r) {
        double dx = 0.0, dy = 0.0;
        if      (p.x() < r.xmin()) dx = r.xmin() - p.x();
        else if (p.x() > r.xmax()) dx = p.x() - r.xmax();
        if      (p.y() < r.ymin()) dy = r.ymin() - p.y();
        else if (p.y() > r.ymax()) dy = p.y() - r.ymax();
        return dx*dx + dy*dy;
    }
}
