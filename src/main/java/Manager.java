import java.util.Date;

public class Manager extends Employee
{
    public Manager(
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
        super(firstName, lastName, role, startDate, isContractor, employeeNumber, managerNumber, isOnHoliday);
    }

    public void goingOnHoliday(Team team)
    {
        team.setCurrentManager(getManagerNumber());
        this.setOnHoliday(true);
    }

    public void returningFromHoliday(Team team)
    {
        team.setCurrentManager(getEmployeeNumber());
        this.setOnHoliday(false);
    }
}
