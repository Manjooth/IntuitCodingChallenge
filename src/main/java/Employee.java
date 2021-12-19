import java.util.Date;

public class Employee {

    private String firstName;
    private String lastName;
    private String role;
    private Date startDate;
    private boolean isContractor;
    private Long employeeNumber;
    private Long managerNumber;
    private boolean isOnHoliday;

    public Employee(
            final String firstName,
            final String lastName,
            final String role,
            final Date startDate,
            final boolean isContractor,
            final Long employeeNumber,
            final Long managerNumber,
            final boolean isOnHoliday
    )
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.startDate = startDate;
        this.isContractor = isContractor;
        this.employeeNumber = employeeNumber;
        this.managerNumber = managerNumber;
        this.isOnHoliday = isOnHoliday;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getRole()
    {
        return role;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public boolean isContractor()
    {
        return isContractor;
    }

    public Long getEmployeeNumber()
    {
        return employeeNumber;
    }

    public Long getManagerNumber()
    {
        return managerNumber;
    }

    public boolean isOnHoliday()
    {
        return isOnHoliday;
    }

    public void setFirstName(final String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName)
    {
        this.lastName = lastName;
    }

    public void setRole(final String role)
    {
        this.role = role;
    }

    public void setStartDate(final Date startDate)
    {
        this.startDate = startDate;
    }

    public void setContractor(final boolean contractor)
    {
        isContractor = contractor;
    }

    public void setEmployeeNumber(final Long employeeNumber)
    {
        this.employeeNumber = employeeNumber;
    }

    public void setManagerNumber(final Long managerNumber)
    {
        this.managerNumber = managerNumber;
    }

    public void setOnHoliday(boolean onHoliday)
    {
        isOnHoliday = onHoliday;
    }
}
