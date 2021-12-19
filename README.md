- Aim to work for 2 hours
- Using different classes to inherit properties
- Code re-usability and readability
- Testing every requirement (TDD style)


- Employee
- Manager inherits from Employee
- CEO inherits from Employee

If we have many employees in a company in which a few of them are
managers. If the managers are all essentially the same as employees, except perhaps that
the have direct reports, then there is no need to create any new classes with repeat attributes. The
Employee class is sufficient to represent them. The manager will have additional attributes such as e.g. subordinates

## Operations:
### Changing teams (implemented)
An employee can move to another team within the organisation. When moving to
a different team, an employee starts reporting to a new manager, without
transferring their past subordinates to the new team. Instead, the most senior
(based on start date) of their subordinates should be promoted to manage the
employee’s former team.

### Employee goes on holidays (implemented)
When an employee goes on holidays, all their subordinates start reporting to the
employee's manager temporarily.

### Employee comes back from holidays (implemented)
When an employee comes back from holidays, all their subordinates come back
to report to them, unless they have moved teams.
 
### Promotions (partially implemented)
- When an employee is promoted, they effectively become a peer of their former
manager. Unfortunately, there is a single CEO for the company, so it is not
possible to promote one of the CEO’s subordinates.
- An employee can also become a Director if they have at least 20 employees in
their unit (people subordinate to them, and their subordinates), including at least
2 managers (people that also have direct reports).
- An employee can also become a Vice President if they have at least 40
employees in their unit (people subordinate to them, and their subordinates),
  including at least 4 directors.
