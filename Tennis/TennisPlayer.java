package Tennis;

import java.util.Objects;

public class TennisPlayer extends Person implements Comparable<TennisPlayer>
{
    private String country;
    private String email;
    private String phone;
    private String rank;

public TennisPlayer() //Default
{
    super();
    this.country = "";
    this.email = "";
    this.phone = "";
    this.rank = "";
}

public TennisPlayer(String name, int age, String country, String email, String phone, String rank) //Overload Constructor
{
        super(name, age);
        this.country = country;
        this.email = email;
        this.phone = phone;
        this.rank = rank;
}
public TennisPlayer(TennisPlayer other) //Copy constructor
{
    super(other);
    this.country = other.country;
    this.email = other.email;
    this.phone = other.phone;
    this.rank = other.rank;
}

    public String getCountry() //Getter
    {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) //Setter
    {
        this.phone = phone;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public int hashCode() { //Hash
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.country);
        hash = 67 * hash + Objects.hashCode(this.email);
        hash = 67 * hash + Objects.hashCode(this.phone);
        hash = 67 * hash + Objects.hashCode(this.rank);
        return hash;
    }

    @Override
    public boolean equals(Object obj) { //Equals
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TennisPlayer other = (TennisPlayer) obj;
        return true;
    }

        @Override
        public String toString() { //ToString
            return super.toString() + "," + country + "," + email + "," + phone + "," + rank;
        }
        
    @Override
    public int compareTo(TennisPlayer other)
    {
        return this.getName().compareTo(other.getName());
    }

 


    
    
    
    
    
}
