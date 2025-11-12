package dao;

import dao.impl.*;

/**
 * Factory for creating DAO instances (Singleton pattern)
 */
public class DAOFactory {
    private static DAOFactory instance;

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

    public static DAOFactory getInstance() {
        if (instance == null) {
            synchronized (DAOFactory.class) {
                if (instance == null) {
                    instance = new DAOFactory();
                }
            }
        }
        return instance;
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
