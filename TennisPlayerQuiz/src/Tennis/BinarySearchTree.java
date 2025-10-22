package Tennis;

public class BinarySearchTree 
{
    private BinarySearchTreeNode root;
    StringBuilder buffer = new StringBuilder("");
    public BinarySearchTree()
            {
                root = null;
            }
    public synchronized boolean add(Student student) 
    {
        if (root == null) {
            root = new BinarySearchTreeNode(student);
            return true;
        } else {
            // Check for duplicates before inserting
            if (contains(student)) {
                return false;
            }
            root.insert(student);
            return true;
        }
    }
    public synchronized void remove(Student student) {
        root = remove(student, root);
    }
    
    private BinarySearchTreeNode remove(Student student, BinarySearchTreeNode node) {
        if (node == null)
            return node;                        // Item not found; do nothing
        
        int comparison = student.compareTo(node.data);
        
        if (comparison < 0)
            node.left = remove(student, node.left);
        else if (comparison > 0)
            node.right = remove(student, node.right);
        else if (node.left != null && node.right != null) // Two children
        {
            node.data = findMin(node.right).data;
            node.right = remove(node.data, node.right);
        }
        else    // Case 1 & case 2: remove leaf node & single child
            node = (node.left != null) ? node.left : node.right;
        return node;
    }
    public Student findMin()
    {
        if (root == null)
            return null;
        return findMin(root).data;
    }
    private BinarySearchTreeNode findMin(BinarySearchTreeNode node) 
    {
        if (node == null)
            return null;
        else if (node.left == null)
            return node;
        return findMin(node.left);
    }
    public boolean contains(Student student) {
        if (nodeWith(student, root) == null)
            return false;
        else {
            BinarySearchTreeNode foundStudent = nodeWith(student, root);
            return (foundStudent.data).equals(student);
        }
    }
    public BinarySearchTreeNode nodeWith(Student student, BinarySearchTreeNode node) {
        if (node == null) {
            return null;
        }
        
        int comparison = student.compareTo(node.data);
        
        if (comparison == 0) {
            return node;
        } else if (comparison < 0) {
            return nodeWith(student, node.left);
        } else {
            return nodeWith(student, node.right);
        }
    }
    public BinarySearchTreeNode nodeWith(String studentName, BinarySearchTreeNode node) {
        if (node == null) {
            return null;
        }
        
        // Compare with current node's student name
        int nameComparison = studentName.compareTo(node.data.getName());
        
        if (nameComparison == 0) {
            return node;
        } else if (nameComparison < 0) {
            return nodeWith(studentName, node.left);
        } else {
            return nodeWith(studentName, node.right);
        }
    }
    public String getBuffer() {
        return buffer.toString();
    }
    
    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public BinarySearchTreeNode getRoot() {
        return root;
    }
    public synchronized void inorderTraversal() {
        inorderHelper(root);
    }
    
    private void inorderHelper(BinarySearchTreeNode node) {
        if (node == null)
            return;
        inorderHelper(node.left);
        System.out.print(node.data + " ");
        inorderHelper(node.right);
    }
    public synchronized void preorderTraversal() {
        preorderHelper(root);
    }
    
    private void preorderHelper(BinarySearchTreeNode node) {
        if (node == null)
            return;
        System.out.print(node.data + " ");
        preorderHelper(node.left);
        preorderHelper(node.right);
    }
    public synchronized void postorderTraversal() {
        postorderHelper(root);
    }
    
    private void postorderHelper(BinarySearchTreeNode node) {
        if (node == null)
            return;
        postorderHelper(node.left);
        postorderHelper(node.right);
        System.out.print(node.data + " ");
    }

    @Override
    public String toString() {
        return "BinarySearchTree{" + "root=" + root + '}';
    }
    
}
