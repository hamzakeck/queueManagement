package dao;

import dao.impl.*;

/**
 * Factory for creating DAO instances (Singleton pattern)
 */
@SuppressWarnings("java:S6548") // Singleton pattern is intentional
public class DAOFactory {

    private CitizenDAO citizenDAO;
    private EmployeeDAO employeeDAO;
    private AdministratorDAO administratorDAO;
    private TicketDAO ticketDAO;
    private ServiceDAO serviceDAO;
    private AgencyDAO agencyDAO;

    private DAOFactory() {
        // Initialize all DAOs
        this.citizenDAO = new CitizenDAOImpl();
        this.employeeDAO = new EmployeeDAOImpl();
        this.administratorDAO = new AdministratorDAOImpl();
        this.ticketDAO = new TicketDAOImpl();
        this.serviceDAO = new ServiceDAOImpl();
        this.agencyDAO = new AgencyDAOImpl();
    }

    private static final class Holder {
        private static final DAOFactory INSTANCE = new DAOFactory();
    }

    public static DAOFactory getInstance() {
        return Holder.INSTANCE;
    }

    public CitizenDAO getCitizenDAO() {
        return citizenDAO;
    }

    public EmployeeDAO getEmployeeDAO() {
        return employeeDAO;
    }

    public AdministratorDAO getAdministratorDAO() {
        return administratorDAO;
    }

    public TicketDAO getTicketDAO() {
        return ticketDAO;
    }

    public ServiceDAO getServiceDAO() {
        return serviceDAO;
    }

    public AgencyDAO getAgencyDAO() {
        return agencyDAO;
    }
}
