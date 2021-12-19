import java.util.ArrayList;
import java.util.List;

public class Team {

    private String teamName;
    private Long managerEmployeeId;
    private List<Long> teamMembersIds = new ArrayList<>(); // manager is included in the teamMembers
    private Long currentManager;

    public Team(final String teamName, final Long managerEmployeeId, final List<Long> teamMembersIds, final Long currentManager) {
        this.teamName = teamName;
        this.managerEmployeeId = managerEmployeeId;
        this.teamMembersIds.addAll(teamMembersIds);
        this.currentManager = managerEmployeeId;
    }

    public Long getCurrentManager() {
        return currentManager;
    }

    public void setCurrentManager(Long currentManager) {
        this.currentManager = currentManager;
    }

    public Long getManagerEmployeeId()
    {
        return managerEmployeeId;
    }

    public void setManagerEmployeeId(Long managerEmployeeId) {
        this.managerEmployeeId = managerEmployeeId;
    }

    public List<Long> getTeamMembersIds()
    {
        return teamMembersIds;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public boolean isTeamMember(final long employeeNumber)
    {
        return teamMembersIds.contains(employeeNumber);
    }

    public void addMember(final long employeeNumber)
    {
        teamMembersIds.add(employeeNumber);
    }

    public void removeMember(final long employeeNumber)
    {
        teamMembersIds.remove(employeeNumber);
    }
}
