package Tennis;

import java.util.Objects;

public abstract class Person 
{
    protected String name;
    protected int age;

    public Person() //Default Constructor
    {
        this.name = "";
        this.age = 0;
    }
    
    public Person(String name, int age) //Overloaded Constructor
    {
        this.name = name;
        this.age = age;
    }

    public void setName(String name) //Setter
    {
        this.name = name;
    }

    public void setAge(int age) 
    {
        this.age = age;
    }

    public String getName() //Getter
    {
        return name;
    }

    public int getAge() 
    {
        return age;
    }

    @Override
    public int hashCode() //Hash
    {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + this.age;
        return hash;
    }

    @Override
    public boolean equals(Object obj)  //Equals Method
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (this.age != other.age) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }
    
    public Person(Person other) //Copy constructor
    {
        this.name = other.name;
        this.age = other.age;
    }
    
    
    
    @Override
    public String toString()
    {
        return name + "," + age;
    }
    
}

