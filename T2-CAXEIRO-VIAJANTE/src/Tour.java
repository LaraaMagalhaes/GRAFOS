// importação de bibliotecsa
import algs4.Point2D;
import algs4.StdDraw;
import algs4.StdOut;
import java.util.HashMap; 
import java.util.Map;


public class Tour {

// pra representar o roteiro do caxeiro é usado uma lista ligada circular e cada elemento da lista é um node
    private static class Node { //cria um elemnto 
        private Point point; //"post-it" com o nome e localização da cidade
        private Node next; //seta q aponta pro próximo post-it
    }

    //variáveis da classe
    private Node start; // cidade de partida
    private int count; //contador de cidades q ja estão
    private final boolean useKdTree; // interruptor de v ou f para uso ou n da kdtree
    private KdTree tree; // se v guarda a kdtree aqui
    private Map<Point2D, Node> pointToNodeMap;// HASHMAP- catálogo de endereços, relacionar um ponto com a kdtree a um "post-it"
    
    public Tour() { //opção padrão vai ser a ingenua, caso queira a kdtree tem q especificar 
        this(false);
    }


    //construtores
    public Tour(boolean useKdTree) { //uso ou n da kdtree
    this.useKdTree = useKdTree;//pega o true ou F
    this.start = null;
    this.count = 0;
    if (useKdTree) { //sim ou n para a kdtree
        this.tree = new KdTree(); //se sim cria ferramentas
        this.pointToNodeMap = new HashMap<>(); //kdtree e catalogo(hash)

    }
}
    //cria um roteiro de teste
    public Tour(Point a, Point b, Point c, Point d) {
        this();
        insertNearestNaive(a);
        insertNearestNaive(b);
        insertNearestNaive(c);
        insertNearestNaive(d);
    }



    //métodos auxiliares pra gerenciar
    public int size() { //retorna numero de cidades no roteiro
        return count;
    }

    public double length() { //mede a distancia entre 1 e 2, 2 e 3, etc etc  /  ate voltar pro 1. 
        if (start == null || start.next == start)
            return 0.0;

        double total = 0.0;
        Node current = start;
        do {
            total += current.point.distanceTo(current.next.point); // ai soma tudo e da o comprimento total 
            current = current.next;
        } while (current != start); // do-while pra garantir a execução para o primeiro e parar quando tem so 1 no na tour, completou o ciclo e terminou o loop
        return total;
    }

    public String toString() { //metod pra devolver string
        StringBuilder sb = new StringBuilder();
        if (start == null)
            return "(Tour vazio)";

        Node current = start; 
        do {
            sb.append(current.point.toString()).append("\n"); // pega o ponto da cidade e anota
            current = current.next; //vai pra proxima
        } while (current != start); // ate terminar o circuito

        return sb.toString();
    }

    public void draw() { // desenha as linhas e conecta os pontos
        if (start == null || start.next == start)
            return;

        Node current = start;
        do {
            current.point.drawTo(current.next.point);
            current = current.next;
        } while (current != start);
    }





    public void insertNearest(Point p) { // método principal q entra uma cidade/ponto p e decide se usa a kdtree ou ingenua
        if (useKdTree) {
            insertNearestKd(p); 
        } else {
            insertNearestNaive(p);
        }
    }

    public void insertNearestNaive(Point p) { // implementação ingenua
        if (start == null) { // checa se o roteiro ta vazio
            start = new Node(); //cria o primeiro "post-it"
            start.point = p; //coloca no post-it a primeira localização
            start.next = start; // como so tem 1 entao a proxima vai ser ela dnv
            count = 1; //atualiza
            return; 
        }

        //encontrar o ponto
        Node nearest = start; // pega a primeira cidade e guarda em nearst
        double minDist = p.distanceTo(start.point); // mede a distancia da nova p ate a proxima p e guarda como menor ate o momento

        Node current = start.next; //verificar as outras cidades
        while (current != start) { //esse loop principal basicamente diz q tem q percorrer o roteiro de cidades ate dar a volta completa
            double dist = p.distanceTo(current.point); // mede a distancia da p para current
            if (dist < minDist) { // compara a distancia com o record, se sim atualiza 
                minDist = dist;
                nearest = current;
            }
            current = current.next;// e passa pra próxima
        }

        Node newNode = new Node(); // insere outro ponto
        newNode.point = p; //guarda a localização desse novo ponto
        newNode.next = nearest.next;// 
        nearest.next = newNode; //ajusta a setagem para newnode (nearest -> newNode -> antiga_proxima)

        count++;
    }
    
    public void insertNearestKd(Point p) { //kdtree
        Point2D p2d = new Point2D(p.x(), p.y()); // só converte o ponto p para o formato especifico da kdtree
        // Se a lista estiver vazia, cria o primeiro Node (raiz) da tour, insere o ponto, mantém a circularidade e atualiza KD-Tree e HashMap
        if (start == null) { // mesma logica de checagem e criação do primeiro ponto
            start = new Node();
            start.point = p;
            start.next = start; 
            count = 1;


            tree.insert(p2d); // mas adiciona a localização no mapa da kdtree
            pointToNodeMap.put(p2d, start); // e atualiza o catálogo de endereço 9 (A localização p2d corresponde ao post-it start)
            return;
        }

        // em vez do while só pergunta pra kdtree qual o mais proximo
        Point2D nearest2D = tree.nearest(p2d);

        // pergunta pro catalogo o node correspondente 
        Node nearestNode = pointToNodeMap.get(nearest2D);

        // insere o novo ponto após o vizinho mais próximo igual a ingenua
        Node newNode = new Node();
        newNode.point = p;
        newNode.next = nearestNode.next;
        nearestNode.next = newNode;

        count++;

        // aTUALIZA AS ESTRUTURAS PARA O FUTURO
        tree.insert(p2d);//adiciona ao mapa
        pointToNodeMap.put(p2d, newNode);//atualiza o catáçogo
    }

}