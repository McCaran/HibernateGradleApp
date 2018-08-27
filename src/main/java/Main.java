import model.Pet;
import model.User;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class Main {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) {
        testInserting("Mikhail", "Konevschinsky", 20);
        testUpdate();
        testCriteria();
        printAll();
        ourSessionFactory.close();
    }

    private static void testInserting(String name, String surname, int age) {
        Session session = ourSessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Pet pet = new Pet();
            pet.setName("Knopka");
            pet.setType("Mouse");
            User user = new User();
            user.setName(name);
            user.setSurname(surname);
            user.setAge(age);
            pet.setOwner(user);
            session.save(user);
            session.save(pet);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        }finally {
            session.close();
        }
    }

    private static void testUpdate() {
        Session session = ourSessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("update User set age = 18 where id =: developerID");
            query.setParameter("developerID", 1);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
        } finally {
            session.close();
        }
    }

    private static void testCriteria() {
        Session session = ourSessionFactory.openSession();
        EntityManager em = ourSessionFactory.createEntityManager();
        try {
            CriteriaQuery<Pet> criteriaQuery = em.getCriteriaBuilder().createQuery(Pet.class);
            Root<Pet> root = criteriaQuery.from(Pet.class);
            criteriaQuery.select(root);
            System.out.println("Вывод списка питомцев через Criteria API:");
            System.out.println(em.createQuery(criteriaQuery).getResultList());
        } finally {
            session.close();
        }
    }

    private static void printAll() {
        Session session = getSession();
        try {
            System.out.println("Выводим ползьзователей и их питомцев: ");
            Query query = session.createQuery("from User ");
            for (Object o: query.list()) {
                User user = (User)o;
                System.out.println(user + "\nПитомцы:\n" + user.getPets());
            }
        } finally {
            session.close();
        }
    }
}