package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table (name = "follow")
@NamedQueries({
    @NamedQuery(
            name = "isRegisterdFollows",
            query = "SELECT f FROM Follow AS f WHERE f.employee_code = :employee_code AND f.follow_code = :follow_code"
            )
})

public class Follow {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "employee_code")
    private String employee_code;

    @Column (name = "follow_code")
    private String follow_code;


    public String getEmployee_code() {
        return employee_code;
    }


    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }


    public String getFollow_code() {
        return follow_code;

    }
    public void setFollow_code(String follow_code) {
        this.follow_code = follow_code;
    }
}




