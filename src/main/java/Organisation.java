import java.util.*;
import java.util.stream.Collectors;

public class Organisation {

    private static final String SUCCESS = "Success";
    private static final String EMPLOYEE_NON_UNIQUE_ID_ERROR = "Employee with that employee number already exists, please enter a unique value";
    // making this final means we can only have one instance
    private final CEO ceo;
    private final List<Employee> employees = new ArrayList<>();
    private final List<Manager> managers = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();

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
        List<Team> maybeTeam = teams.stream().filter(team -> team.getTeamMembersIds().contains(employeeNumber)).collect(Collectors.toList());
        return maybeTeam.get(0);
    }

    public String goOnHoliday(final long employeeNumber) {
        if(checkIfOnHoliday(employeeNumber)){
            return "Invalid - employee already on holiday";
        }

        // is manager
        if(managers.stream().anyMatch(manager -> manager.getEmployeeNumber() == employeeNumber)){
            List<Manager> managerToGoOnHoliday = managers.stream().filter(manager -> manager.getEmployeeNumber() == employeeNumber).collect(Collectors.toList());
            if(!managerToGoOnHoliday.isEmpty()){
                Team team = getTeam(employeeNumber);
                managerToGoOnHoliday.get(0).goingOnHoliday(team);
                List<Long> teamToUpdateCurrentManagerOf = team.getTeamMembersIds();
                for(Employee employee : employees){
                    if(teamToUpdateCurrentManagerOf.contains(employee.getEmployeeNumber())){
                        employee.setManagerNumber(team.getCurrentManager());
                    }
                }
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
            List<Manager> managerReturningFromHoliday = managers.stream().filter(manager -> manager.getEmployeeNumber() == employeeNumber).collect(Collectors.toList());
            if(!managerReturningFromHoliday.isEmpty()){
                Team team = getTeam(employeeNumber);
                managerReturningFromHoliday.get(0).returningFromHoliday(team);
                List<Long> teamToUpdateCurrentManagerOf = team.getTeamMembersIds();
                for(Employee employee : employees){
                    if(teamToUpdateCurrentManagerOf.contains(employee.getEmployeeNumber())){
                        employee.setManagerNumber(team.getCurrentManager());
                    }
                }
            }
        } else { setEmployeeHoliday(employeeNumber, false); }

        return SUCCESS;
    }

    public String promote(final long employeeNumber, final long newManagerNumber, final boolean isEmployee, final String newRole) {
        if(isEmployee){
            // Optional is null safe
            Optional<Employee> employeeToPromote = employees.stream().filter(employee -> employee.getEmployeeNumber() == employeeNumber).findFirst();
            if(!employeeToPromote.isEmpty()){
                employees.remove(employeeToPromote.get());
                managers.add(new Manager(
                        employeeToPromote.get().getFirstName(),
                        employeeToPromote.get().getLastName(),
                        newRole,
                        employeeToPromote.get().getStartDate(),
                        employeeToPromote.get().isContractor(),
                        employeeToPromote.get().getEmployeeNumber(),
                        newManagerNumber,
                        employeeToPromote.get().isOnHoliday()
                ));
            }
        }else{
            Optional<Manager> managerToPromote = managers.stream().filter(manager -> manager.getEmployeeNumber() == employeeNumber).findFirst();
            if(!managerToPromote.isEmpty()){
                if(managerToPromote.get().getRole().equals("Vice President")){
                    return "Invalid - cannot promote VP";
                }


                managerToPromote.get().setRole(newRole);
                managerToPromote.get().setManagerNumber(newManagerNumber);
            }
        }

        return SUCCESS;
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
        String[] roles = new String[]{"Manager", "Director", "Vice President", "CEO"};
        return Arrays.stream(roles).anyMatch(role::contains);
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

//        List<Employee> employeesInTeam = employees.stream().filter(employee -> teamMembersIds.contains(employee)).collect(Collectors.toList());

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
}
