import java.util.Date;

public class CEO extends Employee{ // could potentially make this into a Singleton

    public CEO(
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
        super(firstName, lastName, "CEO", startDate, false, employeeNumber, null, isOnHoliday);
    }

    public void setUpCEO(
            final String firstName,
            final String lastName,
            final Date startDate,
            final Long employeeNumber
    ){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setRole("CEO");
        this.setStartDate(startDate);
        this.setContractor(false);
        this.setEmployeeNumber(employeeNumber);
        this.setManagerNumber(null);
    }
}
