public class Main {

    private static int nonIsolatedVertex(Graph G) {
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) > 0) return v;
        }
        return -1;
    }

    private static boolean isEulerianConnected(Graph G) {
        int s = nonIsolatedVertex(G);
        if (s == -1) return true;
        
        BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);
        
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) > 0 && !bfs.hasPathTo(v)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            StdOut.println("Executar: java -cp src Main data/nomearquivo.txt");
            return;
        }

        In in = new In(args[0]);
        Graph G = new Graph(in);
        
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) % 2 != 0) {
                StdOut.println("Não euleriano - graus ímpares");
                return; 
            }
        }
        
        if (!isEulerianConnected(G)) {
             StdOut.println("Desconexo");
             return; 
        }
        
        StdOut.println("Circuito Euleriano implementando algoritmo de Hierholzer");
        EulerianCycle hier = new EulerianCycle(G); 
        
        if (hier.hasEulerianCycle()) {
            for (int v : hier.cycle()) StdOut.print(v + " ");
            StdOut.println();
        } else {
             StdOut.println("Erro interno na execução do Hierholzer.");
        }
        
        StdOut.println("Circuito Euleriano implementando algoritmo de Fleury");
        EulerianCycleFleury fleury = new EulerianCycleFleury(G);
        
        if (fleury.hasEulerianCycle()) {
            for (int v : fleury.cycle()) StdOut.print(v + " ");
            StdOut.println();
        } else {
             StdOut.println("Erro interno na execução do Fleury.");
        }
    }
}