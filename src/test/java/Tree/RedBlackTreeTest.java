package Tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class RedBlackTreeTest {

    private RedBlackTree<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = new RedBlackTree<>();
    }

    @Test
    @DisplayName("Test single insertion")
    void testSingleInsertion() {
        assertTrue(tree.insert(10));
        assertEquals(1, tree.size());
        assertTrue(tree.contains(10));
        assertTrue(tree.insert(10, true) == false); // already exists
    }

    @Test
    @DisplayName("Test multiple insertions and structure")
    void testMultipleInsertions() {
        int[] values = { 10, 20, 30, 15, 25, 5, 1 };
        for (int v : values) {
            assertTrue(tree.insert(v));
            assertTrue(tree.insert(v, true) == false); // Check for existence but also validates structure
        }
        assertEquals(values.length, tree.size());
    }

    @Test
    @DisplayName("Test deletion of leaf nodes")
    void testDeleteLeaf() {
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);

        // Delete leaf node 5 (should be black-height balanced)
        assertTrue(tree.delete(5, true));
        assertFalse(tree.contains(5));
        assertEquals(2, tree.size());
    }

    @Test
    @DisplayName("Test deletion of root node")
    void testDeleteRoot() {
        tree.insert(10);
        assertTrue(tree.delete(10, true));
        assertEquals(0, tree.size());
        assertFalse(tree.contains(10));
    }

    @Test
    @DisplayName("Test complex deletions")
    void testComplexDeletions() {
        int[] values = { 20, 10, 30, 5, 15, 25, 35, 3, 7, 13, 17, 23, 27, 33, 37 };
        for (int v : values)
            tree.insert(v);

        // Delete various nodes
        int[] toDelete = { 3, 10, 20, 37, 25 };
        for (int v : toDelete) {
            assertTrue(tree.delete(v, true), "Failed to delete " + v);
            assertFalse(tree.contains(v));
        }

        assertEquals(values.length - toDelete.length, tree.size());
    }
}
