import java.util.Stack;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class EulerianCircuit {
    private final Graph G;
    private LinkedList<Integer> finalCircuit; // Armazena o circuito euleriano final

    // =========================================================
    // CONSTRUTOR / LÓGICA PRINCIPAL
    // =========================================================
    
    public EulerianCircuit(Graph G) {
        this.G = G;
        this.finalCircuit = new LinkedList<>();
        
        // 1. RODAR AS PRÉ-VERIFICAÇÕES
        if (!checkEulerianConditions()) {
            return; // Interrompe se as condições não forem atendidas
        }

        // 2. RODAR HIERHOLZER
        findCircuitHierholzer();
    }

    // =========================================================
    // PRÉ-VERIFICAÇÕES
    // =========================================================

    private boolean checkEulerianConditions() {
        // A ordem é importante: se não for conexo, a DFS pode falhar se rodar a partir de um ponto ruim.
        
        // 1. CHECAGEM DE CONECTIVIDADE (Ignorando vértices isolados)
        if (!isConnectedIgnoringIsolated()) {
            StdOut.println("Desconexo (dois componentes com arestas).");
            return false;
        }

        // 2. CHECAGEM DE GRAU PAR
        if (!isAllDegreesEven()) {
            StdOut.println("Nao euleriano (graus impares).");
            return false;
        }
        
        return true;
    }

    private boolean isAllDegreesEven() {
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) % 2 != 0) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isConnectedIgnoringIsolated() {
        int V = G.V();
        int nonIsolatedCount = 0;
        int startVertex = -1; 

        // 1. Contar vértices não isolados e definir o ponto de partida
        for (int v = 0; v < V; v++) {
            if (G.degree(v) > 0) {
                nonIsolatedCount++;
                if (startVertex == -1) {
                    startVertex = v;
                }
            }
        }

        // Se há 0 ou 1 vértice com arestas, é trivialmente conexo
        if (nonIsolatedCount <= 1) return true; 

        // 2. Rodar DFS a partir de um vértice não isolado
        // A classe DepthFirstSearch deve estar disponível na pasta src/
        DepthFirstSearch dfs = new DepthFirstSearch(G, startVertex); 
        
        // 3. Contar quantos vértices NÃO ISOLADOS foram alcançados
        int reachedCount = 0;
        for (int v = 0; v < V; v++) {
            if (G.degree(v) > 0 && dfs.marked(v)) {
                reachedCount++;
            }
        }

        return reachedCount == nonIsolatedCount;
    }

    // =========================================================
    // ALGORITMO DE HIERHOLZER (Tempo Linear O(E))
    // =========================================================

    private void findCircuitHierholzer() {
        // Estrutura de adjacência mutável (para simular a remoção de arestas)
        LinkedList<Integer>[] adj_copy = (LinkedList<Integer>[]) new LinkedList[G.V()];
        for (int v = 0; v < G.V(); v++) {
            adj_copy[v] = new LinkedList<Integer>();
            // Copia os vizinhos
            for (int w : G.adj(v)) {
                adj_copy[v].add(w); 
            }
        }

        Stack<Integer> stack = new Stack<>();
        
        // Encontrar o ponto de partida (qualquer vértice com grau > 0)
        int startVertex = 0;
        while (G.degree(startVertex) == 0 && startVertex < G.V() - 1) {
            startVertex++;
        }

        stack.push(startVertex);

        while (!stack.isEmpty()) {
            int v = stack.peek();

            if (!adj_copy[v].isEmpty()) {
                // 1. Percorre uma aresta (v, w)
                int w = adj_copy[v].removeFirst(); 

                // 2. Remove a aresta simétrica (w, v)
                // O (Integer) v força a remoção do OBJETO v (o valor) e não de um índice
                adj_copy[w].remove((Integer) v); 
                
                // 3. Empilha o novo vértice e continua
                stack.push(w);
            } else {
                // Não há mais arestas não usadas em v.
                // Pop do vértice e adiciona ao INÍCIO do circuito final.
                finalCircuit.addFirst(stack.pop());
            }
        }
    }


    // =========================================================
    // MAIN e I/O
    // =========================================================

    public static void main(String[] args) {
        Graph G = readGraph();
        
        if (G == null) {
            // StdOut.println("Erro: Não foi possível ler o grafo ou o arquivo estava vazio/inválido.");
            return; 
        }

        // Roda a lógica de verificação e Hierholzer
        EulerianCircuit ec = new EulerianCircuit(G);
        
        // Imprimir o resultado
        if (!ec.finalCircuit.isEmpty()) {
            // O circuito está em finalCircuit na ordem correta
            for (int i = 0; i < ec.finalCircuit.size(); i++) {
                StdOut.print(ec.finalCircuit.get(i) + (i == ec.finalCircuit.size() - 1 ? "" : " "));
            }
            StdOut.println();
        }
    }

    private static Graph readGraph() {
        try {
            // Assume-se que o arquivo de entrada foi redirecionado para StdIn
            int V = StdIn.readInt();
            Graph G = new Graph(V);
            int E = StdIn.readInt();
            
            for (int i = 0; i < E; i++) {
                int v = StdIn.readInt();
                int w = StdIn.readInt();
                G.addEdge(v, w);
            }
            return G;
        } catch (NoSuchElementException e) {
            // Se a leitura falhar (e.g., arquivo vazio ou formato incorreto)
            return null; 
        }
    }
}