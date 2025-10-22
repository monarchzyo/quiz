package Tennis;
public class BinarySearchTreeNode 
{
    BinarySearchTreeNode left;
    Student data;
    BinarySearchTreeNode right;
    public BinarySearchTreeNode(Student student)
    {
        data = student;
        left = right = null;
    }
    public synchronized void insert(Student student)
    {
        if (student.compareTo(data) < 0)
        {
            if (left == null)
                left = new BinarySearchTreeNode(student);
            else
                left.insert(student);
        }
        else if (student.compareTo(data) >= 0)
        {
            if (right == null)
                right = new BinarySearchTreeNode(student);
            else 
                right.insert(student);
        }
    }

    public BinarySearchTreeNode getLeft() {
        return left;
    }

    public void setLeft(BinarySearchTreeNode left) {
        this.left = left;
    }

    public Student getData() {
        return data;
    }

    public void setData(Student data) {
        this.data = data;
    }

    public BinarySearchTreeNode getRight() {
        return right;
    }

    public void setRight(BinarySearchTreeNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinarySearchTree{" + "data=" + data + '}';
    }
    
}
