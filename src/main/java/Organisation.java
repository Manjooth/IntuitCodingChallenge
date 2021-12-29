import java.util.*;
import java.util.stream.Collectors;

public class Organisation
{

    private static final String SUCCESS = "Success";
    private static final String EMPLOYEE_NON_UNIQUE_ID_ERROR = "Employee with that employee number already exists, please enter a unique value";
    // making this final means we can only have one instance
    private final CEO ceo;
    private final List<Employee> employees = new ArrayList<>();
    private final List<Manager> managers = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();

    enum ROLE_NAMES_ENUM
    {
        Manager,
        Director,
        VicePresident,
        CEO
    }

    public Organisation(final CEO ceo) {
        this.ceo = ceo;
    }

    public CEO getCeo() {
        return ceo;
    }

    public String addEmployee( // make params final so they cannot be manipulated
                               final String firstName,
                               final String lastName,
                               final String role,
                               final Date startDate,
                               final boolean isContractor,
                               final long employeeNumber,
                               final long managerNumber,
                               final boolean isOnHoliday
    ) {
        if (!checkEmployeeNumberIsUnique(employeeNumber)) {
            return EMPLOYEE_NON_UNIQUE_ID_ERROR;
        }

        if(!checkIfEmployeeRoleIsCorrect(role)){
            return "Invalid role - enter correct role";
        }

        if (!checkManagerNumber(managerNumber)) {
            return "Invalid Manager - Manager id must be valid";
        }

        employees.add(new Employee(firstName, lastName, role, startDate, isContractor, employeeNumber, managerNumber, isOnHoliday));

        return SUCCESS;
    }

    public String addManager(
            final String firstName,
            final String lastName,
            final String role,
            final Date startDate,
            final boolean isContractor,
            final long employeeNumber,
            final long managerNumber,
            final boolean isOnHoliday
    ) {
        if (!checkEmployeeNumberIsUnique(employeeNumber)) {
            return EMPLOYEE_NON_UNIQUE_ID_ERROR;
        }

        if(!checkIfManagerRoleIsCorrect(role)){
            return "Invalid role - enter correct role";
        }

        if(isContractor){
            return "Contractors cannot be managers";
        }

        managers.add(new Manager(firstName, lastName, role, startDate, isContractor, employeeNumber, managerNumber, isOnHoliday));

        return SUCCESS;
    }

    public String addTeam(final String teamName, final Long managerId, final List<Long> teamMembers)
    {
        if (!checkTeamNameIsUnique(teamName)) {
            return "Invalid team name - team name must be unique";
        }
        teams.add(new Team(teamName, managerId, teamMembers, managerId));

        return SUCCESS;
    }

    public String moveTeam(final String oldTeam, final String newTeam, final long employeeNumber, final boolean isManager) {
        if(!checkTeamExists(oldTeam) || !checkTeamExists(newTeam)){
            return "Invalid team - team does not exist";
        }

        if(!checkEmployeeExistsInOldTeam(oldTeam, employeeNumber)){
            return "Error - employee not part of old team";
        }

        Team previousTeam = null;
        Team nextTeam = null;

        for(Team team : teams){
            if(team.getTeamName().equals(oldTeam)){
                previousTeam = team;
            }else if(team.getTeamName().equals(newTeam)){
                nextTeam = team;
            }
        }

        nextTeam.addMember(employeeNumber);
        previousTeam.setManagerEmployeeId(-1L);
        previousTeam.removeMember(employeeNumber);

        return promoteOldestTeamMateToManager(previousTeam.getTeamMembersIds(), previousTeam);
    }

    public Team getTeam(final long employeeNumber){
        final Optional<Team> maybeTeam = teams.stream().filter(team -> team.getTeamMembersIds().contains(employeeNumber)).findFirst();
        return maybeTeam.orElse(null);
    }

    public String goOnHoliday(final long employeeNumber) {
        if(checkIfOnHoliday(employeeNumber)){
            return "Invalid - employee already on holiday";
        }

        // is manager
        if(managers.stream().anyMatch(manager -> manager.getEmployeeNumber() == employeeNumber)){
            final Optional<Manager> managerToGoOnHoliday = managers.stream().filter(manager -> manager.getEmployeeNumber() == employeeNumber).findFirst();
            if(managerToGoOnHoliday.isPresent()){
                final Team team = getTeam(employeeNumber);
                managerToGoOnHoliday.get().goingOnHoliday(team);
                final List<Long> teamToUpdateCurrentManagerOf = team.getTeamMembersIds();

                employees
                        .stream()
                        .filter(
                                employee -> teamToUpdateCurrentManagerOf.contains(employee.getEmployeeNumber())
                        )
                        .forEach(employee -> employee.setManagerNumber(team.getCurrentManager()));

            }
        } else {
            setEmployeeHoliday(employeeNumber, true);
        }

        return SUCCESS;
    }

    public String returnFromHoliday(final long employeeNumber) {
        if(!checkIfOnHoliday(employeeNumber)){
            return "Invalid - employee not on holiday";
        }

        if(managers.stream().anyMatch(manager -> manager.getEmployeeNumber() == employeeNumber)){
            final Optional<Manager> managerReturningFromHoliday = managers.stream().filter(managers -> managers.getEmployeeNumber() == employeeNumber).findFirst();
            if(managerReturningFromHoliday.isPresent()){
                final Team team = getTeam(employeeNumber);
                managerReturningFromHoliday.get().returningFromHoliday(team);
                final List<Long> teamToUpdateCurrentManagerOf = team.getTeamMembersIds();

                employees
                        .stream()
                        .filter(
                                employee -> teamToUpdateCurrentManagerOf.contains(employee.getEmployeeNumber())
                        )
                        .forEach(employee -> employee.setManagerNumber(team.getCurrentManager()));
            }
        } else { setEmployeeHoliday(employeeNumber, false); }

        return SUCCESS;
    }

    public String promote(final long employeeNumber, final long newManagerNumber, final boolean isEmployee, final String newRole)
    {
        if(isEmployee)
        {
            // Optional is null safe
            final Optional<Employee> employeeToPromote = employees.stream().filter(employee -> employee.getEmployeeNumber() == employeeNumber).findFirst();
            if(!employeeToPromote.isEmpty())
            {
                employees.remove(employeeToPromote.get());
                String response = addManager(
                        employeeToPromote.get().getFirstName(),
                        employeeToPromote.get().getLastName(),
                        newRole,
                        employeeToPromote.get().getStartDate(),
                        employeeToPromote.get().isContractor(),
                        employeeToPromote.get().getEmployeeNumber(),
                        newManagerNumber,
                        employeeToPromote.get().isOnHoliday()
                );

                return response;
            }
        }
        else
        {
            final Optional<Manager> managerToPromote = managers.stream().filter(manager -> manager.getEmployeeNumber() == employeeNumber).findFirst();
            if(managerToPromote.isPresent())
            {
                if(managerToPromote.get().getRole().equals(ROLE_NAMES_ENUM.VicePresident.name()))
                {
                    return "Invalid - cannot promote VP";
                }
                else if(managerToPromote.get().getRole().equals(ROLE_NAMES_ENUM.Manager.name()))
                {
                    final List<Long> managerList = getNoOfSubordinateManagers(managerToPromote.get().getEmployeeNumber());
                    if(!(managerList.size() >= 2) ||!(getNoOfSubordinateEmployees(managerList) >= 20))
                    {
                        return "Manager is not able to be promoted to Director";
                    }
                }
                else if(managerToPromote.get().getRole().equals(ROLE_NAMES_ENUM.Director.name()))
                {
                    final List<Long> managerList = getNoOfSubordinateManagers(managerToPromote.get().getEmployeeNumber());
                    if(!(managerList.size() >= 4) ||!(getNoOfSubordinateEmployees(managerList) >= 40))
                    {
                        return "Director is not able to be promoted to Vice President";
                    }
                }

                managerToPromote.get().setRole(newRole);
                managerToPromote.get().setManagerNumber(newManagerNumber);
            }
        }

        return SUCCESS;
    }

    public Optional<Manager> getManager(final Long employeeNumber)
    { // test method helper
        return managers.stream().filter(manager -> manager.getEmployeeNumber() == employeeNumber).findFirst();
    }

    private boolean checkEmployeeNumberIsUnique(final long employeeNumber) {
        return employees.stream().noneMatch(employee -> employee.getEmployeeNumber() == employeeNumber)
                && managers.stream().noneMatch(manager -> manager.getEmployeeNumber() == employeeNumber);
    }

    private boolean checkIfEmployeeRoleIsCorrect(final String role) {
        return role.equals("Employee");
    }

    private boolean checkManagerNumber(final long managerNumber) {
        return managers.stream().anyMatch(manager -> manager.getEmployeeNumber() == managerNumber);
    }

    private boolean checkIfManagerRoleIsCorrect(final String role) {
        return Arrays.stream(ROLE_NAMES_ENUM.values()).anyMatch(role_name -> role_name.name().equals(role));
    }

    private boolean checkTeamNameIsUnique(final String teamName) {
        return teams.stream().noneMatch(team -> team.getTeamName() == teamName);
    }

    private boolean checkTeamExists(final String newTeam) {
        return teams.stream().anyMatch(team -> team.getTeamName().equals(newTeam));
    }

    private boolean checkEmployeeExistsInOldTeam(final String oldTeam, final long employeeNumber) {
        return teams.stream().anyMatch(team -> team.getTeamName().equals(oldTeam) && team.getTeamMembersIds().contains(employeeNumber));
    }

    private String promoteOldestTeamMateToManager(final List<Long> teamMembersIds, final Team previousTeam) {
        List<Employee> employeesInTeam = new ArrayList<>();

        for(Employee employee : employees){
            long number = employee.getEmployeeNumber();
            if(teamMembersIds.contains(number)){
                employeesInTeam.add(employee);
            }
        }

        Date oldestEmployeeDate = new Date();
        Employee newManager = employeesInTeam.get(0);

        for(Employee employee : employeesInTeam){
            if(employee.getStartDate().before(oldestEmployeeDate)){
                oldestEmployeeDate = employee.getStartDate();
                newManager = employee;
            }
        }

        employees.remove(newManager.getEmployeeNumber());
        previousTeam.setManagerEmployeeId(newManager.getEmployeeNumber());

        String response = addManager(
                newManager.getFirstName(),
                newManager.getLastName(),
                newManager.getRole(),
                newManager.getStartDate(),
                newManager.isContractor(),
                newManager.getEmployeeNumber(),
                -1l,
                newManager.isOnHoliday()
        );

        return SUCCESS;
    }

    private boolean checkIfOnHoliday(final long employeeId) {
        return employees.stream().anyMatch(employee -> employee.getEmployeeNumber() == employeeId && employee.isOnHoliday())
                || managers.stream().anyMatch(manager -> manager.getEmployeeNumber() == employeeId && manager.isOnHoliday());
    }

    private void setEmployeeHoliday(final long employeeNumber, final boolean setOnHoliday) {
        Optional<Employee> employeeToGoOnHoliday = employees.stream().filter(employee -> employee.getEmployeeNumber() == employeeNumber).findFirst();
        employeeToGoOnHoliday.ifPresent(employee -> employee.setOnHoliday(setOnHoliday));
    }

    private List<Long> getNoOfSubordinateManagers(final long employeeNumber) {
        return managers
                .stream()
                .filter(manager -> manager.getManagerNumber() == employeeNumber)
                .map(Employee::getEmployeeNumber)
                .collect(Collectors.toList());
    }

    private int getNoOfSubordinateEmployees(final List<Long> managerList) {
        return teams
                .stream()
                .filter(team -> managerList.contains(team.getManagerEmployeeId()))
                .map(team -> team.getTeamMembersIds().size())
                .collect(Collectors.toList())
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
