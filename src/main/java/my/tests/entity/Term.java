package my.tests.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Chris Sekaran on 2/6/14.
 */
@Entity
@Table(name = "Term")
public class Term {

    @Id
    @NotNull
    @Length(min = 1, max = 40)
    private String termId;

    @Column
    @NotNull
    @Length(min = 1, max = 100)
    private String termText;

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getTermText() {
        return termText;
    }

    public void setTermText(String termText) {
        this.termText = termText;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Term{");
        sb.append("termId='").append(termId).append('\'');
        sb.append(", termText='").append(termText).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
