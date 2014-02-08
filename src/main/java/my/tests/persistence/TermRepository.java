package my.tests.persistence;

import my.tests.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Chris Sekaran on 2/6/14.
 */
public interface TermRepository extends JpaRepository<Term, String> {
}
