public class EulerianCycleFleury {

    private Bag<Integer> cycle;

    private static class Edge {
        int v, w;
        boolean isUsed;

        Edge(int v, int w) {
            this.v = v;
            this.w = w;
            this.isUsed = false;
        }

        int other(int vertex) {
            if (vertex == v) return w;
            else if (vertex == w) return v;
            else throw new IllegalArgumentException("Vértice inválido");
        }
    }

    public EulerianCycleFleury(Graph G) {
        if (G.E() == 0) return;

        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) % 2 != 0) return;
        }

        Bag<Edge>[] adj = (Bag<Edge>[]) new Bag[G.V()];
        for (int v = 0; v < G.V(); v++) {
            adj[v] = new Bag<>();
        }

        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                if (v <= w) { 
                    Edge e = new Edge(v, w);
                    adj[v].add(e);
                    adj[w].add(e);
                }
            }
        }

        int start = nonIsolatedVertex(G);
        if (start == -1) return;

        cycle = new Bag<>();
        int current = start;

        while (hasUnusedEdge(adj, current)) {
            Edge nextEdge = chooseEdge(adj, current);
            nextEdge.isUsed = true;

            cycle.add(current);

            current = nextEdge.other(current);
        }

        cycle.add(current);
    }

    private Edge chooseEdge(Bag<Edge>[] adj, int v) {
        Edge bridgeCandidate = null;

        for (Edge e : adj[v]) {
            if (!e.isUsed) {
                if (!isBridge(adj, v, e)) {
                    return e; 
                } else {
                    bridgeCandidate = e; 
                }
            }
        }
        return bridgeCandidate; 
    }

    private boolean isBridge(Bag<Edge>[] adj, int v, Edge e) {
        if (countUnused(adj[v]) == 1) return false; 

        e.isUsed = true;
        int count1 = dfsCount(adj, v);
        int count2 = dfsCount(adj, e.other(v));
        e.isUsed = false;

        return count1 > count2; 
    }

    private int dfsCount(Bag<Edge>[] adj, int start) {
        boolean[] visited = new boolean[adj.length];
        Stack<Integer> stack = new Stack<>();
        stack.push(start);
        int count = 0;

        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (!visited[v]) {
                visited[v] = true;
                count++;
                for (Edge e : adj[v]) {
                    if (!e.isUsed) {
                        int w = e.other(v);
                        if (!visited[w]) stack.push(w);
                    }
                }
            }
        }
        return count;
    }

    private int countUnused(Bag<Edge> edges) {
        int c = 0;
        for (Edge e : edges) if (!e.isUsed) c++;
        return c;
    }

    private static int nonIsolatedVertex(Graph G) {
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) > 0) return v;
        }
        return -1;
    }

    private boolean hasUnusedEdge(Bag<Edge>[] adj, int v) {
        for (Edge e : adj[v]) if (!e.isUsed) return true;
        return false;
    }

    public Iterable<Integer> cycle() {
        return cycle;
    }

    public boolean hasEulerianCycle() {
        return cycle != null && !cycle.isEmpty();
    }
}
