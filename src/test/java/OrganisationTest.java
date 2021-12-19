import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationTest
{

    private static final String SUCCESS = "Success";
    private static final String EMPLOYEE_NON_UNIQUE_ID_ERROR = "Employee with that employee number already exists, please enter a unique value";

    private final Organisation organisation = new Organisation(
            new CEO("Elf", "Elrond", "CEO", new Date(), false, 100l, null, false)
    );

    @BeforeEach
    void setUp()
    {
        organisation.addManager("Larry", "Clarke", "Director", new Date(), false, 4l, 5l, false);
        organisation.addManager("John", "Burkins", "Manager", new Date(), false, 8l, 4l, true);
        organisation.addManager("Mike", "Newton", "Manager", new Date(), false, 3l, 4l, false);
        organisation.addEmployee("Manjooth", "Kler", "Employee", new Date(), false, 1l, 3l, true);
        organisation.addEmployee("Jay", "Bird", "Employee", new Date(), false, 12l, 3l, false);
        organisation.addTeam("teamOne",3l, Arrays.asList(1l, 2l, 3l, 12l));
    }

    @Test
    void shouldReturnErrorWhenEmployeeNumberIsNotUnique()
    {
        final String result = organisation.addEmployee("Manjooth", "Kler", "Employee", new Date(), false, 1l, 3l, false);
        assertEquals(EMPLOYEE_NON_UNIQUE_ID_ERROR, result);
    }

    @Test
    void shouldReturnSuccessWhenEmployeeNumberIsUnique()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2l, 3l, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenEmployeeManagerNumberIsNotValid()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2l, 10l, false);
        assertEquals("Invalid Manager - Manager id must be valid", result);
    }

    @Test
    void shouldReturnSuccessWhenEmployeeManagerNumberIsValid()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2l, 3l, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenEmployeeRoleIsIncorrect()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "ABC", new Date(), false, 2l, 3l, false);
        assertEquals("Invalid role - enter correct role", result);
    }

    @Test
    void shouldReturnSuccessWhenEmployeeRoleIsCorrect()
    {
        final String result = organisation.addEmployee("Jane", "Doe", "Employee", new Date(), false, 2l, 3l, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenManagerEmployeeNumberIsNotUnique()
    {
        final String result = organisation.addManager("", "", "Manager", new Date(), false, 1l, 3l, false);
        assertEquals(EMPLOYEE_NON_UNIQUE_ID_ERROR, result);
    }

    @Test
    void shouldReturnSuccessWhenManagerEmployeeNumberIsUnique()
    {
        final String result = organisation.addManager("Manjooth", "Kler", "Manager", new Date(), false, 2l, 3l, false);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnCeo(){
        assertEquals(CEO.class, organisation.getCeo().getClass());
    }

    @Test
    void shouldNotAllowContractorsToBeManagers()
    {
        final String result = organisation.addManager("Manjooth", "Kler", "Manager", new Date(), true, 2l, 3l, false);
        assertEquals("Contractors cannot be managers", result);
    }

    @Test
    void shouldReturnErrorWhenManagerRoleIsIncorrect()
    {
        final String result = organisation.addManager("Manjooth", "Kler", "Employee", new Date(), true, 2l, 3l, false);
        assertEquals("Invalid role - enter correct role", result);
    }

    @Test
    void shouldReturnSuccessWhenManagerRoleIsCorrect()
    {
        final String result1 = organisation.addManager("Manjooth", "Kler", "Manager", new Date(), false, 2l, 3l, false);
        final String result2 = organisation.addManager("Manjooth", "Kler", "Director", new Date(), false, 22l, 3l, false);
        final String result3 = organisation.addManager("Manjooth", "Kler", "Vice President", new Date(), false, 23l, 3l, false);

        assertEquals(SUCCESS, result1);
        assertEquals(SUCCESS, result2);
        assertEquals(SUCCESS, result3);
    }

    @Test
    void shouldReturnErrorWhenTeamNameIsNotUnique(){
        String result = organisation.addTeam("teamOne",3l, Arrays.asList(3l));
        assertEquals("Invalid team name - team name must be unique", result);
    }

    @Test
    void shouldReturnSuccessWhenTeamNameIsUnique(){
        String result = organisation.addTeam("teamTwo",3l, Arrays.asList(3l));
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenNewTeamDoesNotExistToMoveTo(){
        String result = organisation.moveTeam("teamAMillion", "teamTwo", 4l, false);
        assertEquals("Invalid team - team does not exist", result);
    }

    @Test
    void shouldReturnErrorWhenOldTeamDoesNotExistToMoveTo(){
        String result = organisation.moveTeam("teamOne", "teamTwo", 4l, false);
        assertEquals("Invalid team - team does not exist", result);
    }

    @Test
    void shouldReturnErrorIfEmployeeNotPartOfOldTeamBeforeMoving(){
        organisation.addTeam("teamTwo",4l, Arrays.asList(4l));
        String result = organisation.moveTeam("teamOne", "teamTwo", 5l, true);

        assertEquals("Error - employee not part of old team", result);
    }

    @Test
    void shouldBeAbleToMoveTeams()
    {
        organisation.addTeam("teamTwo",4l, Arrays.asList(4l));
        organisation.moveTeam("teamOne", "teamTwo", 3l, true);

        assertEquals("teamTwo", organisation.getTeam(3l).getTeamName());
    }

    @Test
    void shouldReportToManagerOfNewTeam()
    {
        organisation.addTeam("teamTwo",4l, Arrays.asList(4l));
        organisation.moveTeam("teamOne", "teamTwo", 3l, true);

        assertEquals(4l, organisation.getTeam(3l).getManagerEmployeeId());
    }

    @Test
    void shouldNotManageOldTeamMembers()
    {
        organisation.addTeam("teamTwo",4l, Arrays.asList(4l));
        organisation.moveTeam("teamOne", "teamTwo", 3l, true);

        assertEquals(1L, organisation.getTeam(1l).getManagerEmployeeId());
    }

    @Test
    void shouldPromoteOldestTeamMemberAsNewManagerWhenManagerMovesToANewTeam()
    {
        organisation.addTeam("teamTwo",4l, Arrays.asList(4l));
        organisation.moveTeam("teamOne", "teamTwo", 3l, true);

        assertEquals(1l, organisation.getTeam(2l).getManagerEmployeeId());
    }

    @Test
    void shouldNoLongerBeEmployeeWhenPromotedToManagerOfTeam()
    {
        organisation.addTeam("teamTwo",4l, Arrays.asList(4l));
        organisation.moveTeam("teamOne", "teamTwo", 3l, true);

        assertFalse(organisation.getTeam(2l).isTeamMember(3l));
    }

    @Test
    void shouldReturnErrorIfEmployeeAlreadyOnHoliday()
    {
        String result = organisation.goOnHoliday(1l);
        assertEquals("Invalid - employee already on holiday", result);
    }

    @Test
    void shouldBeAbleToGoOnHolidayAsEmployee()
    {
        String result = organisation.goOnHoliday(12l);
        assertEquals(SUCCESS ,result);
    }

    @Test
    void shouldBeAbleToGoOnHolidayAsManager()
    {
        String result = organisation.goOnHoliday(3l);
        assertEquals(SUCCESS ,result);
    }

    @Test
    void shouldBeAbleToReportToManagersManagerWhenManagerGoesOnHoliday()
    {
        organisation.goOnHoliday(3l);
        assertEquals(4l, organisation.getTeam(1l).getCurrentManager());
    }

    @Test
    void shouldReturnErrorIfEmployeeNotOnHoliday()
    {
        String result = organisation.returnFromHoliday(12l);
        assertEquals("Invalid - employee not on holiday", result);
    }

    @Test
    void shouldBeAbleToReturnFromHolidayAsEmployee(){
        String result = organisation.returnFromHoliday(1l);
        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldBeAbleToReturnFromHolidayAsManager()
    {
        organisation.goOnHoliday(3l);
        String result = organisation.returnFromHoliday(3l);

        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldBeAbleToReportToManagerWhenManagerBackFromHoliday()
    {
        organisation.goOnHoliday(3l);
        organisation.returnFromHoliday(3l);

        assertEquals(3l, organisation.getTeam(1l).getCurrentManager());
    }

    @Test
    void shouldBeAbleToPromoteEmployee() // just promoting, we do not care about team etc
    {
        organisation.addEmployee("Manjooth", "Kler", "Employee", new Date(), false, 6l, 3l, false);
        String result = organisation.promote(6l, 4l, true, "Manager");

        assertEquals(SUCCESS, result);
    }

    @Test
    void shouldReturnErrorWhenTryingToPromoteVicePresident()
    {
        organisation.addManager("Vince", "Marlow", "Vice President", new Date(), false, 5l, 100l, false);
        String result = organisation.promote(5l, 100l, false, "CEO");

        assertEquals("Invalid - cannot promote VP", result);
    }

    @Test
    void shouldBeAbleToPromoteIfManagerHas20DirectReportsAnd2ManagersBelowThem()
    {

    }

    @Test
    void shouldBeAbleToPromoteIfDirectorHas40DirectReportsAnd4ManagersBelowThem()
    {

    }
}
