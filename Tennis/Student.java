package Tennis;
public class Student extends Person implements Comparable<Student>     
{
    private int correct;
    private int totalQuestions;
    
    public Student() //Default
    {
        super();
        this.correct = 0;
        this.totalQuestions = 0;
    }
   public Student (String name, int age, int correct, int totalQuestions) //Overload
   {
       super(name, age);
       this.correct = correct;
       this.totalQuestions = totalQuestions;
   }
   public Student(Student other) //copy
   {
       super(other);
       this.correct = other.correct;
       this.totalQuestions = other.totalQuestions;
   }

    public int getCorrect() //getter and setter
    {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
   
    @Override
    public int compareTo(Student other)
    {
        int nameCompare = this.getName().compareTo(other.getName());
        if (nameCompare != 0)
        {
            return nameCompare;
        }
        else
        {
            return Integer.compare(this.getAge(), other.getAge());
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.correct;
        hash = 83 * hash + this.totalQuestions;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Student other = (Student) obj;
        if (this.correct != other.correct) {
            return false;
        }
        return this.totalQuestions == other.totalQuestions;
    }
    
    public double calculatePercent()
    {
        if (totalQuestions == 0)
        {
            return 0;
        }
        else
        {
            return (double) correct/totalQuestions * 100;
        }
    }
    
    @Override
    public String toString()
    {
        return super.toString() + "," + correct + "," + totalQuestions;
    }
}
