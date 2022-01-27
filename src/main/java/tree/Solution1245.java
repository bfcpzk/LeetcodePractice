package tree;

import java.util.*;

/**
 * 1245. Tree Diameter
 * The diameter of a tree is the number of edges in the longest path in that tree.
 *
 * There is an undirected tree of n nodes labeled from 0 to n - 1. You are given a 2D array edges where
 * edges.length == n - 1 and edges[i] = [ai, bi] indicates that there is an undirected edge between
 * nodes ai and bi in the tree.
 *
 * Return the diameter of the tree.
 *
 * Example 1:
 *
 * Input: edges = [[0,1],[0,2]]
 * Output: 2
 * Explanation: The longest path of the tree is the path 1 - 0 - 2.
 */
public class Solution1245 {

    /**
     * 任意一个点开始的 bfs 搜索最后一个点一定是整个树中最长路径的某一个端点，
     * 然后第二次 bfs 从这个端点出发进一步找出最长路径的另一个端点
     */
    static class BFSFarthestNode {
        public int treeDiameter(int[][] edges) {
            if (edges.length == 0) return 0;
            List<Integer>[] g = new List[edges.length + 1];
            for (int i = 0 ; i < g.length ; i++) g[i] = new ArrayList<>();
            for (int[] e : edges) {
                g[e[0]].add(e[1]);
                g[e[1]].add(e[0]);
            }

            int[] nodeDistance = bfs(0, g);
            nodeDistance = bfs(nodeDistance[0], g);

            return nodeDistance[1];
        }

        private int[] bfs(int v, List<Integer>[] g) {
            Queue<Integer> queue = new LinkedList<>();
            boolean[] visited = new boolean[g.length];
            visited[v] = true;
            int lastNode = -1, dist = -1;
            queue.offer(v);

            while (!queue.isEmpty()) {
                Queue<Integer> tmpQueue = new LinkedList<>();

                while (!queue.isEmpty()) {
                    int tv = queue.poll();
                    for (int neighbor : g[tv]) {
                        if (!visited[neighbor]) {
                            visited[neighbor] = true;
                            tmpQueue.offer(neighbor);
                            lastNode = neighbor;
                        }
                    }
                }

                dist += 1;
                queue = tmpQueue;
            }

            return new int[]{lastNode, dist};
        }
    }

    /**
     * 找到所有叶子节点，然后 bfs, 一层一层往上 像剥洋葱一样，直到最后剩下一个或两个节点的时候停止，
     * 这一个或两个点就是图的 centroid，而此时已经走过的路径长度 * 2 (+1 or not) 就是最终结果。
     * 这个计算图的 centroid 的方法，对于一棵树来说，可以用于计算最小高度树的根节点。310. Minimum Height Trees
     */
    class BFSCentroidOfGraph {
        public int treeDiameter(int[][] edges) {
            int n = edges.length + 1;
            Set<Integer>[] g = new Set[n];
            for (int i = 0 ; i < n ; i++) g[i] = new HashSet<>();
            for (int[] e : edges) {
                g[e[0]].add(e[1]);
                g[e[1]].add(e[0]);
            }

            Queue<Integer> leaves = new LinkedList<>();
            for (int i = 0 ; i < n ; i++) {
                if (g[i].size() == 1) leaves.offer(i);
            }
            int vLeft = n;
            int layers = 0;
            while (vLeft > 2) {
                vLeft -= leaves.size();

                Queue<Integer> nextLeaves = new LinkedList<>();

                while (!leaves.isEmpty()) {
                    int leaf = leaves.poll();
                    int neighbor = g[leaf].iterator().next();
                    g[neighbor].remove(leaf);
                    if (g[neighbor].size() == 1) nextLeaves.offer(neighbor);
                }

                layers += 1;
                leaves = nextLeaves;
            }

            if (vLeft == 2) return 2 * layers + 1;
            else return 2 * layers;

        }
    }
}