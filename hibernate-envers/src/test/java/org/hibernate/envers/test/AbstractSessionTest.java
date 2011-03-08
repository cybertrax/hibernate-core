package org.hibernate.envers.test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.service.spi.ServiceRegistry;
import org.hibernate.testing.ServiceRegistryBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base class for testing envers with Session.
 *
 * @author Hern&aacute;n Chanfreau
 *
 */
public abstract class AbstractSessionTest {

	protected Configuration config;
	private ServiceRegistry serviceRegistry;
	private SessionFactory sessionFactory;
	private Session session ;
	private AuditReader auditReader;


	@BeforeClass
    @Parameters("auditStrategy")
    public void init(@Optional String auditStrategy) throws URISyntaxException {
        config = new Configuration();
        URL url = Thread.currentThread().getContextClassLoader().getResource(getHibernateConfigurationFileName());
        config.configure(new File(url.toURI()));

        if (auditStrategy != null && !"".equals(auditStrategy)) {
            config.setProperty("org.hibernate.envers.audit_strategy", auditStrategy);
        }

        this.initMappings();

		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
		sessionFactory = config.buildSessionFactory( serviceRegistry );
    }

	protected abstract void initMappings() throws MappingException, URISyntaxException ;

	protected String getHibernateConfigurationFileName(){
		return "hibernate.test.session-cfg.xml";
	}


	private SessionFactory getSessionFactory(){
		return sessionFactory;
    }


    @BeforeMethod
    public void newSessionFactory() {
      session = getSessionFactory().openSession();
      auditReader = AuditReaderFactory.get(session);
    }

	@AfterClass
	public void closeSessionFactory() {
		try {
	   		sessionFactory.close();
		}
		finally {
			if ( serviceRegistry != null ) {
				ServiceRegistryBuilder.destroy( serviceRegistry );
				serviceRegistry = null;
			}
		}
	}


	protected Session getSession() {
		return session;
	}



	protected AuditReader getAuditReader() {
		return auditReader;
	}

}
