import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationTest
{

    private static final String SUCCESS = "Success";
    private static final String EMPLOYEE_NON_UNIQUE_ID_ERROR = "Employee with that employee number already exists, please enter a unique value";

    private final Organisation organisation = new Organisation(
            new CEO("Elf", "Elrond", "CEO", new Date(), false, 100L, null, false)
    );

    @BeforeEach
    void setUp()
    {
        organisation.addManager("Larry", "Clarke", "Director", new Date(), false, 4L, 5L, false);
        organisation.addManager("John", "Burkins", "Manager", new Date(), false, 8L, 4L, true);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 3L, 4L, false);
        organisation.addEmployee("Manjooth", "Kler", "Employee", new Date(), false, 1L, 3L, true);
        organisation.addEmployee("Jay", "Bird", "Employee", new Date(), false, 12L, 3L, false);
        organisation.addTeam("teamOne", 3L, Arrays.asList(1L, 2L, 3L, 12L));
    }

    @Test
    void shouldReturnErrorWhenEmployeeNumberIsNotUnique()
    {
        final String result = organisation.addEmployee("Manjooth", "Kler", "Employee", new Date(), false, 1L, 3L, false);
        assertEquals(EMPLOYEE_NON_UNIQUE_ID_ERROR, result);
    }

    @Test
    void shouldReturnSuccessWhenEmployeeNumberIsUnique()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2L, 3L, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenEmployeeManagerNumberIsNotValid()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2L, 10L, false);
        assertEquals("Invalid Manager - Manager id must be valid", result);
    }

    @Test
    void shouldReturnSuccessWhenEmployeeManagerNumberIsValid()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2L, 3L, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenEmployeeRoleIsIncorrect()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "ABC", new Date(), false, 2L, 3L, false);
        assertEquals("Invalid role - enter correct role", result);
    }

    @Test
    void shouldReturnSuccessWhenEmployeeRoleIsCorrect()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2L, 3L, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenManagerEmployeeNumberIsNotUnique()
    {
        final String result = organisation.addManager("Jane", "Doe", "Manager", new Date(), false, 1L, 3L, false);
        assertEquals(EMPLOYEE_NON_UNIQUE_ID_ERROR, result);
    }

    @Test
    void shouldReturnSuccessWhenManagerEmployeeNumberIsUnique()
    {
        final String result = organisation.addManager("Jay", "Bird", "Manager", new Date(), false, 2L, 3L, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnCeo(){
        assertEquals(CEO.class, organisation.getCeo().getClass());
    }

    @Test
    void shouldNotAllowContractorsToBeManagers()
    {
        final String result = organisation.addManager("Jay", "Bird", "Manager", new Date(), true, 2L, 3L, false);
        assertEquals("Contractors cannot be managers", result);
    }

    @Test
    void shouldReturnErrorWhenManagerRoleIsIncorrect()
    {
        final String result = organisation.addManager("Jay", "Bird", "Employee", new Date(), true, 2L, 3L, false);
        assertEquals("Invalid role - enter correct role", result);
    }

    @Test
    void shouldReturnSuccessWhenManagerRoleIsCorrect()
    {
        final String result1 = organisation.addManager("Jay", "Bird", "Manager", new Date(), false, 2L, 3L, false);
        final String result2 = organisation.addManager("John", "Bennett", "Director", new Date(), false, 22L, 3L, false);
        final String result3 = organisation.addManager("Mike", "Newton", "VicePresident", new Date(), false, 23L, 3L, false);

        assertEquals(SUCCESS, result1);
        assertEquals(SUCCESS, result2);
        assertEquals(SUCCESS, result3);
    }

    @Test
    void shouldReturnErrorWhenTeamNameIsNotUnique(){
        String result = organisation.addTeam("teamOne", 3L, List.of(3L));
        assertEquals("Invalid team name - team name must be unique", result);
    }

    @Test
    void shouldReturnSuccessWhenTeamNameIsUnique(){
        String result = organisation.addTeam("teamTwo", 3L, List.of(3L));
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenNewTeamDoesNotExistToMoveTo(){
        String result = organisation.moveTeam("teamAMillion", "teamTwo", 4L, false);
        assertEquals("Invalid team - team does not exist", result);
    }

    @Test
    void shouldReturnErrorWhenOldTeamDoesNotExistToMoveTo(){
        String result = organisation.moveTeam("teamOne", "teamTwo", 4L, false);
        assertEquals("Invalid team - team does not exist", result);
    }

    @Test
    void shouldReturnErrorIfEmployeeNotPartOfOldTeamBeforeMoving(){
        organisation.addTeam("teamTwo", 4L, List.of(4L));
        String result = organisation.moveTeam("teamOne", "teamTwo", 5L, true);

        assertEquals("Error - employee not part of old team", result);
    }

    @Test
    void shouldBeAbleToMoveTeams()
    {
        organisation.addTeam("teamTwo", 4L, List.of(4L));
        organisation.moveTeam("teamOne", "teamTwo", 3L, true);

        assertEquals("teamTwo", organisation.getTeam(3L).getTeamName());
    }

    @Test
    void shouldReportToManagerOfNewTeam()
    {
        organisation.addTeam("teamTwo", 4L, List.of(4L));
        organisation.moveTeam("teamOne", "teamTwo", 3l, true);

        assertEquals(4l, organisation.getTeam(3L).getManagerEmployeeId());
    }

    @Test
    void shouldNotManageOldTeamMembers()
    {
        organisation.addTeam("teamTwo", 4L, List.of(4L));
        organisation.moveTeam("teamOne", "teamTwo", 3L, true);

        assertEquals(1L, organisation.getTeam(1l).getManagerEmployeeId());
    }

    @Test
    void shouldPromoteOldestTeamMemberAsNewManagerWhenManagerMovesToANewTeam()
    {
        organisation.addTeam("teamTwo", 4L, List.of(4L));
        organisation.moveTeam("teamOne", "teamTwo", 3L, true);

        assertEquals(1l, organisation.getTeam(2L).getManagerEmployeeId());
    }

    @Test
    void shouldNoLongerBeEmployeeWhenPromotedToManagerOfTeam()
    {
        organisation.addTeam("teamTwo", 4L, List.of(4L));
        organisation.moveTeam("teamOne", "teamTwo", 3L, true);

        assertFalse(organisation.getTeam(2L).isTeamMember(3L));
    }

    @Test
    void shouldReturnErrorIfEmployeeAlreadyOnHoliday()
    {
        String result = organisation.goOnHoliday(1L);
        assertEquals("Invalid - employee already on holiday", result);
    }

    @Test
    void shouldBeAbleToGoOnHolidayAsEmployee()
    {
        String result = organisation.goOnHoliday(12L);
        assertEquals(SUCCESS ,result);
    }

    @Test
    void shouldBeAbleToGoOnHolidayAsManager()
    {
        String result = organisation.goOnHoliday(3L);
        assertEquals(SUCCESS ,result);
    }

    @Test
    void shouldBeAbleToReportToManagersManagerWhenManagerGoesOnHoliday()
    {
        organisation.goOnHoliday(3L);
        assertEquals(4L, organisation.getTeam(1L).getCurrentManager());
    }

    @Test
    void shouldReturnErrorIfEmployeeNotOnHoliday()
    {
        String result = organisation.returnFromHoliday(12L);
        assertEquals("Invalid - employee not on holiday", result);
    }

    @Test
    void shouldBeAbleToReturnFromHolidayAsEmployee(){
        String result = organisation.returnFromHoliday(1L);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldBeAbleToReturnFromHolidayAsManager()
    {
        organisation.goOnHoliday(3L);
        String result = organisation.returnFromHoliday(3L);

        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldBeAbleToReportToManagerWhenManagerBackFromHoliday()
    {
        organisation.goOnHoliday(3L);
        organisation.returnFromHoliday(3L);

        assertEquals(3L, organisation.getTeam(1L).getCurrentManager());
    }

    @Test
    void shouldBeAbleToPromoteEmployee() // just promoting, we do not care about team etc
    {
        organisation.addEmployee("Jay", "Bird", "Employee", new Date(), false, 6L, 3L, false);
        String result = organisation.promote(6L, 4L, true, "Manager");

        assertEquals(SUCCESS, result);
        assertEquals("Manager", organisation.getManager(6L).get().getRole());
    }

    @Test
    void shouldReturnErrorWhenTryingToPromoteVicePresident()
    {
        organisation.addManager("Vince", "Marlow", "VicePresident", new Date(), false, 5L, 100L, false);
        String result = organisation.promote(5L, 100L, false, "CEO");

        assertEquals("Invalid - cannot promote VP", result);
    }

    @Test
    void shouldReturnErrorIfManagerDoesNotHave2ManagersBelowThem()
    {
        organisation.addManager("John", "Bennett", "Manager", new Date(), false, 13L, 100L, false);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 14L, 13L, false);
        String response = organisation.promote(13l, 101l, false, "Director");

        assertEquals("Manager is not able to be promoted to Director", response);
    }

    @Test
    void shouldReturnErrorIfManagerDoesNotHave20EmployeesBelowThem()
    {
        organisation.addManager("John", "Bennett", "Manager", new Date(), false, 13L, 100L, false);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 14L, 13L, false);
        organisation.addManager("Vince", "Marlow", "Manager", new Date(), false, 15L, 13L, false);

        organisation.addTeam("teamThree", 14L, List.of(30L, 31L, 32L));

        String response = organisation.promote(13L, 101L, false, "Director");
        assertEquals("Manager is not able to be promoted to Director", response);
    }

    @Test
    void shouldReturnSuccessAndPromoteIfManagerHas20DirectReportsAnd2ManagersBelowThem()
    {
        organisation.addManager("John", "Bennett", "Manager", new Date(), false, 13L, 100L, false);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 14L, 13L, false);
        organisation.addManager("Vince", "Marlow", "Manager", new Date(), false, 15L, 13L, false);

        organisation.addTeam("teamThree",14L, List.of(30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L));
        organisation.addTeam("teamFour",15L, List.of(30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L));

        String response = organisation.promote(13L, 101L, false, "Director");
        assertEquals(SUCCESS, response);
        assertEquals("Director", organisation.getManager(13L).get().getRole());
    }

    @Test
    void shouldReturnErrorIfDirectorDoesNotHave4ManagersBelowThem()
    {
        organisation.addManager("John", "Bennett", "Director", new Date(), false, 13L, 100L, false);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 14L, 13L, false);
        String response = organisation.promote(13L, 101L, false, "VicePresident");

        assertEquals("Director is not able to be promoted to Vice President", response);
    }

    @Test
    void shouldReturnErrorIfManagerDoesNotHave40EmployeesBelowThem()
    {
        organisation.addManager("John", "Bennett", "Director", new Date(), false, 13L, 100L, false);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 14L, 13L, false);
        String response = organisation.promote(13L, 101L, false, "VicePresident");

        assertEquals("Director is not able to be promoted to Vice President", response);
    }

    @Test
    void shouldReturnSuccessAndPromoteIfDirectorHas40DirectReportsAnd4ManagersBelowThem()
    {
        organisation.addManager("John", "Bennett", "Director", new Date(), false, 13L, 100L, false);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 14L, 13L, false);
        organisation.addManager("Vince", "Marlow", "Manager", new Date(), false, 15L, 13L, false);
        organisation.addManager("Jane", "Doe", "Manager", new Date(), false, 16L, 13L, false);
        organisation.addManager("Jay", "Bird", "Manager", new Date(), false, 17L, 13L, false);

        organisation.addTeam("teamThree", 14L, List.of(30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L));
        organisation.addTeam("teamFour", 15L, List.of(30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L));
        organisation.addTeam("teamFive", 16L, List.of(30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L));
        organisation.addTeam("teamSix", 17L, List.of(30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L));

        String response = organisation.promote(13L, 101L, false, "VicePresident");
        assertEquals(SUCCESS, response);
        assertEquals("VicePresident", organisation.getManager(13l).get().getRole());
    }
}
